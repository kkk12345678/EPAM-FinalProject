package com.epam.kkorolkov.finalproject.admin;

import com.epam.kkorolkov.finalproject.db.dao.*;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.exception.BadRequestException;
import com.epam.kkorolkov.finalproject.exception.DBConnectionException;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.exception.DaoException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    /** Page to redirect after exception is thrown */
    private static final String REDIRECT_ERROR_CONNECTION =
            "/error?code=500&message=Unable to connect to the database. Try again later.";
    private static final String REDIRECT_ERROR_DAO =
            "/error?code=500&message=Cannot instantiate DAO. See server logs for details.";
    private static final String REDIRECT_ERROR_DB =
            "/error?code=500&message=Database error occurred. See server logs for details.";
    private static final String REDIRECT_ERROR_PARAMS =
            "/error?code=400&message=Some GET parameters are incorrect. See server logs for details.";

    /** Request attributes keys */
    private static final String ATTR_USER = "user";
    private static final String ATTR_USERS_COUNT = "usersCount";
    private static final String ATTR_CATEGORIES_COUNT = "categoriesCount";
    private static final String ATTR_PUBLISHERS_COUNT = "publishersCount";
    private static final String ATTR_BOOKS_COUNT = "booksCount";
    private static final String ATTR_ORDERS_COUNT = "ordersCount";

    /** JSP page to include */
    private static final String INCLUDE_JSP = "./jsp/admin/welcome.jsp";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String context = request.getServletContext().getContextPath();
        Connection connection = null;
        DataSource dataSource = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            UserDao userDao = AbstractDaoFactory.getInstance().getUserDao();
            CategoryDao categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();
            PublisherDao publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
            OrderDao orderDao = AbstractDaoFactory.getInstance().getOrderDao();
            BookDao bookDao = AbstractDaoFactory.getInstance().getBookDao();
            request.setAttribute(ATTR_USERS_COUNT, userDao.count(connection));
            request.setAttribute(ATTR_CATEGORIES_COUNT, categoryDao.count(connection));
            request.setAttribute(ATTR_PUBLISHERS_COUNT, publisherDao.count(connection));
            request.setAttribute(ATTR_BOOKS_COUNT, bookDao.count(connection, null));
            request.setAttribute(ATTR_ORDERS_COUNT, orderDao.count(connection, null));
            request.setAttribute(ATTR_USER, request.getSession().getAttribute(ATTR_USER));
            request.getRequestDispatcher(INCLUDE_JSP).include(request, response);
        } catch (DBConnectionException e) {
            response.sendRedirect(context + REDIRECT_ERROR_CONNECTION);
        } catch (DBException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DB);
        } catch (DaoException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DAO);
        } catch (BadRequestException e) {
            response.sendRedirect(context + REDIRECT_ERROR_PARAMS);
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }
}
