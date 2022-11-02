package com.epam.kkorolkov.finalproject.admin.order;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.OrderDao;
import com.epam.kkorolkov.finalproject.db.dao.StatusDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
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

@WebServlet("/admin/orders")
public class GetOrdersServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger("GET ORDERS");

    /** Number of shown products per page */
    private static final int LIMIT = 20;

    /** Page to redirect if <i>page</i> parameter is not specified */
    private static final String REDIRECT_NO_PAGE = "/admin/orders?page=1";

    /** Page to redirect after exception is thrown */
    private static final String REDIRECT_ERROR_CONNECTION =
            "/error?code=500&message=Unable to connect to the database. Try again later.";
    private static final String REDIRECT_ERROR_DAO =
            "/error?code=500&message=Cannot instantiate DAO. See server logs for details.";
    private static final String REDIRECT_ERROR_DB =
            "/error?code=500&message=Database error occurred. See server logs for details.";
    private static final String REDIRECT_ERROR_PAGE =
            "/error?code=400&message=Page parameter is incorrect. See server logs for details.";

    /** Logger messages */
    private static final String MESSAGE_ERROR_PAGE = "Page parameter is specified incorrectly.";

    /** Request parameters */
    private static final String PARAM_PAGE = "page";

    /** JSP page to include */
    private static final String INCLUDE_JSP = "../jsp/admin/orders/orders.jsp";

    /** Request attributes */
    private static final String ATTR_ORDERS = "orders";
    private static final String ATTR_STATUSES = "statuses";
    private static final String ATTR_CURRENT_PAGE = "currentPage";
    private static final String ATTR_TOTAL_PAGES = "totalPages";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String context = request.getServletContext().getContextPath();
        String pageParameter = request.getParameter(PARAM_PAGE);
        Map<String, String> parameters = CatalogueUtils.setOrderParameters(request);
        if (pageParameter == null || "".equals(pageParameter)) {
            response.sendRedirect(REDIRECT_NO_PAGE);
            return;
        }
        DataSource dataSource = null;
        Connection connection = null;
        try {
            int page = Integer.parseInt(pageParameter);
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            OrderDao orderDao = AbstractDaoFactory.getInstance().getOrderDao();
            StatusDao statusDao = AbstractDaoFactory.getInstance().getStatusDao();
            int totalPages = (orderDao.count(connection, parameters) - 1) / LIMIT + 1;
            if (page > totalPages) {
                page = totalPages;
            }
            request.setAttribute(ATTR_ORDERS, orderDao.getAll(connection, LIMIT, LIMIT * (page - 1), parameters));
            request.setAttribute(ATTR_STATUSES, statusDao.getAll(connection));
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
            response.sendRedirect(context + REDIRECT_ERROR_PAGE);
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }
}
