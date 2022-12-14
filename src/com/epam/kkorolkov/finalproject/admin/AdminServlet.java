package com.epam.kkorolkov.finalproject.admin;

import com.epam.kkorolkov.finalproject.db.dao.*;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.exception.BadRequestException;
import com.epam.kkorolkov.finalproject.exception.DbConnectionException;
import com.epam.kkorolkov.finalproject.exception.DbException;
import com.epam.kkorolkov.finalproject.exception.DaoException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

/**
 * The {@code AdminServlet} is a servlet which task is to represent main administrator's page.
 * It contains number of rows in the tables <i>categories</i>, <i>publishers</i>,
 * <i>books</i>, <i>orders</i>, and <i>users</i>.
 *
 * Only {@code doGet} method is overridden.
 */
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

    /**
     * {@code doGet} method handles GET request. Retrieves data from
     * {@link UserDao#count(Connection)}, {@link OrderDao#count(Connection, Map)},
     * {@link PublisherDao#count(Connection)}, {@link CategoryDao#count(Connection)},
     * and {@link BookDao#count(Connection, Map)}. Finally, sets corresponding attributes
     * to {@code request}.
     *
     * @param request {@link HttpServletRequest} object provided by Tomcat.
     * @param response {@link HttpServletResponse} object provided by Tomcat.
     *
     * @throws ServletException is thrown if the request for the GET could not be handled.
     * @throws IOException is thrown if an input or output exception occurs.
     */
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
        } catch (DbConnectionException e) {
            response.sendRedirect(context + REDIRECT_ERROR_CONNECTION);
        } catch (DbException e) {
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
