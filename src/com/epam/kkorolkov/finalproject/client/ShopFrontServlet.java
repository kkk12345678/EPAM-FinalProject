package com.epam.kkorolkov.finalproject.client;

import com.epam.kkorolkov.finalproject.db.dao.*;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.User;
import com.epam.kkorolkov.finalproject.exception.BadRequestException;
import com.epam.kkorolkov.finalproject.exception.DBConnectionException;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.util.CatalogueUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

@WebServlet("/shop")
public class ShopFrontServlet extends HttpServlet {
    private static final int LIMIT = 20;
    private static final String INCLUDE_PAGE = "jsp/client/products.jsp";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user != null && user.getIsAdmin()) {
            response.sendRedirect("./admin");
        }
        String pageParameter = request.getParameter("page");
        int page = (pageParameter == null || "".equals(pageParameter)) ? 1 : Integer.parseInt(pageParameter);

        Map<String, String> parameters = CatalogueUtils.setBookParameters(request);
        DataSource dataSource = null;
        Connection connection = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            BookDao bookDao = AbstractDaoFactory.getInstance().getBookDao();
            PublisherDao publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
            CategoryDao categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();
            if (connection != null) {
                request.setAttribute("maxPrice", bookDao.getMaxPrice(connection, parameters));
                request.setAttribute("minPrice", bookDao.getMinPrice(connection, parameters));
                request.setAttribute("publishers", publisherDao.getAll(connection));
                request.setAttribute("categories", categoryDao.getAll(connection));
                request.setAttribute("books", bookDao.getAll(connection, LIMIT, LIMIT * (page - 1), parameters));
            }
        } catch (BadRequestException e) {
            // TODO handle BadRequestException
        } catch (DBConnectionException e) {
            // TODO handle DBConnectionException
        } catch (DBException e) {
            // TODO handle DBException
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
        request.getRequestDispatcher(INCLUDE_PAGE).include(request, response);
    }
}
