package com.epam.kkorolkov.finalproject.admin.order;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.OrderDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.exception.DbConnectionException;
import com.epam.kkorolkov.finalproject.exception.DbException;
import com.epam.kkorolkov.finalproject.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

/**
 * The {@code UpdateOrderServlet} is a servlet which task is to
 * change order status in the table <i>orders</i>.
 *
 * Only {@code doPost} method is overridden.
 */
@WebServlet("/update-status")
public class UpdateOrderServlet extends HttpServlet {
    /** Logger */
    private static final Logger LOGGER = LogManager.getLogger("UPDATE STATUS");

    /** Page to redirect after exception is thrown */
    private static final String REDIRECT_ERROR_CONNECTION =
            "/error?code=500&message=Unable to connect to the database. Try again later.";
    private static final String REDIRECT_ERROR_DAO =
            "/error?code=500&message=Cannot instantiate DAO. See server logs for details.";
    private static final String REDIRECT_ERROR_DB =
            "/error?code=500&message=Database error occurred. See server logs for details.";
    private static final String REDIRECT_ERROR_PAGE =
            "/error?code=400&message=Some POST parameters are specified incorrectly. See server logs for details.";

    /** Request parameters */
    private static final String PARAM_ORDER_ID = "order_id";
    private static final String PARAM_STATUS_ID = "status_id";
    private static final String PARAM_PAGE = "page";

    /** Logger messages */
    private static final String MESSAGE_ERROR_PAGE = "Some POST parameters are specified incorrectly.";

    /**
     * {@code doPost} method handles POST request. Updates a row
     * in the table <i>orders</i> changing order status.
     * Reads request parameters {@code orderId} and {@code statusId} and invokes
     * {@link OrderDao#updateStatus(Connection, int, int)} method.
     *
     * @param request {@link HttpServletRequest} object provided by Tomcat.
     * @param response {@link HttpServletResponse} object provided by Tomcat.
     *
     * @throws IOException is thrown if an input or output exception occurs.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String context = request.getServletContext().getContextPath();
        DataSource dataSource = null;
        Connection connection = null;
        try {
            int orderId = Integer.parseInt(request.getParameter(PARAM_ORDER_ID));
            int statusId = Integer.parseInt(request.getParameter(PARAM_STATUS_ID));
            String page = request.getParameter(PARAM_PAGE);
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            OrderDao orderDao = AbstractDaoFactory.getInstance().getOrderDao();
            orderDao.updateStatus(connection, orderId, statusId);
            response.sendRedirect(page);
        } catch (DbConnectionException e) {
            response.sendRedirect(context + REDIRECT_ERROR_CONNECTION);
        } catch (DbException e) {
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
