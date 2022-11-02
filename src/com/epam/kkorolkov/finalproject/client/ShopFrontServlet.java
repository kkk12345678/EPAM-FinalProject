package com.epam.kkorolkov.finalproject.client;

import com.epam.kkorolkov.finalproject.db.dao.*;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.User;
import com.epam.kkorolkov.finalproject.exception.BadRequestException;
import com.epam.kkorolkov.finalproject.exception.DBConnectionException;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.exception.DaoException;
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
import java.util.Map;

@WebServlet("/shop")
public class ShopFrontServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger("GET BOOKS");

    /** Number of shown products per page */
    private static final int LIMIT = 20;

    /** Page to redirect if <i>user</i> is admin */
    private static final String REDIRECT_ADMIN = "/admin";

    /** Page to redirect after exception is thrown */
    private static final String REDIRECT_ERROR_CONNECTION =
            "/error?code=500&message=Unable to connect to the database. Try again later.";
    private static final String REDIRECT_ERROR_DAO =
            "/error?code=500&message=Cannot instantiate DAO. See server logs for details.";
    private static final String REDIRECT_ERROR_DB =
            "/error?code=500&message=Database error occurred. See server logs for details.";
    private static final String REDIRECT_ERROR_PARAMS =
            "/error?code=400&message=Some GET parameters are incorrect. See server logs for details.";

    /** Logger messages */
    private static final String MESSAGE_ERROR_PARAMS = "Some GET parameters are incorrect.";
    private static final String MESSAGE_ERROR_PAGE = "Page parameter is specified incorrectly.";

    /** JSP page to include */
    private static final String INCLUDE_PAGE = "jsp/client/products.jsp";

    /** Request parameters */
    private static final String PARAM_PAGE = "page";

    /** Request attributes */
    private static final String ATTR_USER = "user";
    private static final String ATTR_BOOKS = "books";
    private static final String ATTR_CATEGORIES = "categories";
    private static final String ATTR_PUBLISHERS = "publishers";
    private static final String ATTR_MAX_PRICE = "maxPrice";
    private static final String ATTR_MIN_PRICE = "minPrice";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String context = request.getServletContext().getContextPath();
        User user = (User) request.getSession().getAttribute(ATTR_USER);
        if (user != null && user.getIsAdmin()) {
            response.sendRedirect(context + REDIRECT_ADMIN);
            return;
        }
        String pageParameter = request.getParameter(PARAM_PAGE);
        DataSource dataSource = null;
        Connection connection = null;
        try {
            int page = (pageParameter == null || "".equals(pageParameter)) ? 1 : Integer.parseInt(pageParameter);
            if (page < 1) {
                LOGGER.info(MESSAGE_ERROR_PAGE);
                throw new BadRequestException();
            }
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            BookDao bookDao = AbstractDaoFactory.getInstance().getBookDao();
            PublisherDao publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
            CategoryDao categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();
            Map<String, String> parameters = CatalogueUtils.setBookParameters(request);
            request.setAttribute(ATTR_MAX_PRICE, bookDao.getMaxPrice(connection, parameters));
            request.setAttribute(ATTR_MIN_PRICE, bookDao.getMinPrice(connection, parameters));
            request.setAttribute(ATTR_PUBLISHERS, publisherDao.getAll(connection));
            request.setAttribute(ATTR_CATEGORIES, categoryDao.getAll(connection));
            request.setAttribute(ATTR_BOOKS, bookDao.getAll(connection, LIMIT, LIMIT * (page - 1), parameters));
            request.getRequestDispatcher(INCLUDE_PAGE).include(request, response);
        } catch (DBConnectionException e) {
            response.sendRedirect(context + REDIRECT_ERROR_CONNECTION);
        } catch (DBException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DB);
        } catch (DaoException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DAO);
        } catch (BadRequestException e) {
            response.sendRedirect(context + REDIRECT_ERROR_PARAMS);
        } catch (NumberFormatException e) {
            LOGGER.info(MESSAGE_ERROR_PARAMS);
            LOGGER.error(e.getMessage());
            response.sendRedirect(context + REDIRECT_ERROR_PARAMS);
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }
}
