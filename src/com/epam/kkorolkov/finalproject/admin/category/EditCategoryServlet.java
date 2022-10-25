package com.epam.kkorolkov.finalproject.admin.category;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.CategoryDao;
import com.epam.kkorolkov.finalproject.db.dao.LanguageDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Category;
import com.epam.kkorolkov.finalproject.db.entity.Language;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.util.CatalogueUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@WebServlet("/admin/edit-category")
public class EditCategoryServlet extends HttpServlet {
    protected static final Logger LOGGER = LogManager.getLogger("Edit category");

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DataSource dataSource = null;
        Connection connection = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            Category category = new Category();
            CatalogueUtils.setDetailsFromRequest(request, category, getLanguages(connection));
            if (!CatalogueUtils.validate(category)) {
                request.setAttribute("message", String.format("Category with tag %s already exists.", category.getTag()));
                request.setAttribute("page", "./edit-category?id=" + category.getId());
                request.getRequestDispatcher("../jsp/error/400.jsp").include(request, response);
            } else {
                CategoryDao categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();
                if (category.getId() == 0) {
                    categoryDao.insert(connection, category);
                } else {
                    categoryDao.update(connection, category);
                }
                response.sendRedirect("./categories");
            }
        } catch (DBException e) {
            // TODO handle DBException
        } catch (NumberFormatException e) {
            // TODO handle NumberFormatException
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DataSource dataSource = null;
        Connection connection = null;
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            if (id != 0) {
                CategoryDao categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();
                Category category = categoryDao.get(connection, id).orElseThrow(NoSuchElementException::new);
                request.setAttribute("category", category);
            }
            request.setAttribute("languages", getLanguages(connection));

            request.getRequestDispatcher("../jsp/admin/categories/edit-category.jsp").include(request, response);
        } catch (DBException e) {
            // TODO handle DBException
        } catch (NumberFormatException e) {
            // TODO handle NumberFormatException
        } catch (NoSuchElementException e) {
            // TODO handle NoSuchElementException
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }

    }

    private Map<Integer, Language> getLanguages(Connection connection) throws DBException {
        LanguageDao languageDao = AbstractDaoFactory.getInstance().getLanguageDao();
        return languageDao.getAll(connection);
    }
}
