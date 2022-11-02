package com.epam.kkorolkov.finalproject.client;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.OrderDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.User;
import com.epam.kkorolkov.finalproject.exception.DBConnectionException;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.exception.DaoException;
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

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger("PROFILE");

    /** Number of shown orders per page */
    private static final int LIMIT = 20;

    /** Page to redirect if <i>page</i> parameter is not specified */
    private static final String REDIRECT_NO_PAGE = "/profile?page=1";

    /** Page to redirect if user is not logged in */
    private static final String REDIRECT_NO_USER = "/shop?page=1";

    /** Page to redirect after exception is thrown */
    private static final String REDIRECT_ERROR_CONNECTION =
            "/error?code=500&message=Unable to connect to the database. Try again later.";
    private static final String REDIRECT_ERROR_DAO =
            "/error?code=500&message=Cannot instantiate DAO. See server logs for details.";
    private static final String REDIRECT_ERROR_DB =
            "/error?code=500&message=Database error occurred. See server logs for details.";
    private static final String REDIRECT_ERROR_REQUEST =
            "/error?code=500&message=GET request parameter 'page' is not valid. See server logs for details.";

    /** Logger messages */
    private static final String MESSAGE_ERROR_PAGE = "Page parameter is specified incorrectly.";

    /** Request parameters */
    private static final String PARAM_PAGE = "page";
    private static final String PARAM_USER_ID = "user_id";

    /** JSP page to include */
    private static final String INCLUDE_JSP = "./jsp/client/profile.jsp";

    /** Request attributes */
    private static final String ATTR_ORDERS = "orders";
    private static final String ATTR_USER = "user";
    private static final String ATTR_CURRENT_PAGE = "currentPage";
    private static final String ATTR_TOTAL_PAGES = "totalPages";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String context = request.getServletContext().getContextPath();
        User user = (User) request.getSession().getAttribute(ATTR_USER);
        if (user == null) {
            response.sendRedirect(context + REDIRECT_NO_USER);
            return;
        }
        String pageParameter = request.getParameter(PARAM_PAGE);
        if (pageParameter == null || "".equals(pageParameter)) {
            response.sendRedirect(context + REDIRECT_NO_PAGE);
            return;
        }
        DataSource dataSource = null;
        Connection connection = null;
        try {
            int page = Integer.parseInt(pageParameter);
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            OrderDao orderDao = AbstractDaoFactory.getInstance().getOrderDao();
            Map<String, String> parameters = new HashMap<>();
            parameters.put(PARAM_USER_ID, Integer.toString(user.getId()));
            int totalPages = (orderDao.count(connection, parameters) - 1) / LIMIT + 1;
            if (page < 1 || page > totalPages) {
                throw new NumberFormatException();
            }
            request.setAttribute(ATTR_ORDERS, orderDao.getAllByUser(connection, LIMIT, LIMIT * (page - 1), user.getId()));
            request.setAttribute(ATTR_TOTAL_PAGES, totalPages);
            request.setAttribute(ATTR_CURRENT_PAGE, page);
            request.getRequestDispatcher(INCLUDE_JSP).include(request, response);
        } catch (DBConnectionException e) {
            response.sendRedirect(context + REDIRECT_ERROR_CONNECTION);
        } catch (DBException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DB);
        } catch (DaoException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DAO);
        } catch (NumberFormatException e) {
            LOGGER.info(MESSAGE_ERROR_PAGE);
            LOGGER.error(e.getMessage());
            response.sendRedirect(context + REDIRECT_ERROR_REQUEST);
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }
}
