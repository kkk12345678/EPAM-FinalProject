package com.epam.kkorolkov.finalproject.controller.category;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.CategoryDao;
import com.epam.kkorolkov.finalproject.db.dao.LanguageDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Category;
import com.epam.kkorolkov.finalproject.db.entity.Language;
import com.epam.kkorolkov.finalproject.exception.DBException;
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
        int id;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            id = 0;
        }

        DataSource dataSource = null;
        Connection connection = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            String tag = request.getParameter("tag");
            Map<Integer, Language> languages = getLanguages(connection);
            Map<Integer, String> descriptions = new HashMap<>();
            Map<Integer, String> names = new HashMap<>();
            for (int languageId : languages.keySet()) {
                descriptions.put(languageId, request.getParameter("description" + languageId));
                names.put(languageId, request.getParameter("name" + languageId));
            }
            CategoryDao categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();
            Category category = new Category();
            category.setId(id);
            category.setTag(tag.toLowerCase().replaceAll("[^a-z0-9]+", "-"));
            category.setNames(names);
            category.setDescriptions(descriptions);
            LOGGER.info(category);
            if (id == 0) {
                categoryDao.insert(connection, category);
            } else {
                categoryDao.update(connection, category);
            }
        } catch (DBException e) {
            // TODO handle DBException
            e.printStackTrace();
        } catch (NullPointerException e) {
            // TODO handle NullPointerException
            e.printStackTrace();
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
        response.sendRedirect("./categories");
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
            e.printStackTrace();
        } catch (NumberFormatException e) {
            // TODO handle NumberFormatException
            e.printStackTrace();
        } catch (NoSuchElementException e) {
            // TODO handle NoSuchElementException
            e.printStackTrace();
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
