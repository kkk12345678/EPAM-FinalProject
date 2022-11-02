package com.epam.kkorolkov.finalproject.client;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.BookDao;
import com.epam.kkorolkov.finalproject.db.dao.LanguageDao;
import com.epam.kkorolkov.finalproject.db.dao.OrderDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Book;
import com.epam.kkorolkov.finalproject.db.entity.Order;
import com.epam.kkorolkov.finalproject.db.entity.User;
import com.epam.kkorolkov.finalproject.exception.BadRequestException;
import com.epam.kkorolkov.finalproject.exception.DBConnectionException;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.exception.DaoException;
import com.epam.kkorolkov.finalproject.util.DBUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@WebServlet("/order")
public class OrderServlet extends HttpServlet {
    /** Logger */
    private static final Logger LOGGER = LogManager.getLogger("ORDER");

    /** Page to redirect after successful request processing */
    private static final String REDIRECT_SUCCESS = "/order?success=1&id=";

    /** Page to redirect after exception is thrown */
    private static final String REDIRECT_ERROR_CONNECTION =
            "/error?code=500&message=Unable to connect to the database. Try again later.";
    private static final String REDIRECT_ERROR_DAO =
            "/error?code=500&message=Cannot instantiate DAO. See server logs for details.";
    private static final String REDIRECT_ERROR_DB =
            "/error?code=500&message=Database error occurred. See server logs for details.";
    private static final String REDIRECT_ERROR_PARAMS =
            "/error?code=400&message=Some request parameters are incorrect. See server logs for details.";
    private static final String REDIRECT_ERROR_SESSION =
            "/error?code=400&message=Some session parameters are incorrect. See server logs for details.";

    /** Logger messages */
    private static final String MESSAGE_ERROR_PARAMS = "Some request parameters are incorrect.";
    private static final String MESSAGE_SUCCESS = "Order # %d was successfully placed.";

    /** Session attributes */
    private static final String ATTR_CART = "cart";

    /** Request parameters */
    private static final String PARAM_ID = "id";
    private static final String PARAM_USER = "user";
    private static final String PARAM_TOTAL = "total";
    private static final String PARAM_SUCCESS = "success";

    /** JSP page to include */
    private static final String INCLUDE_JSP = "./jsp/client/success.jsp";

    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String context = request.getServletContext().getContextPath();
        DataSource dataSource = null;
        Connection connection = null;
        try {
            HttpSession session = request.getSession();
            Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute(ATTR_CART);
            User user = (User) session.getAttribute(PARAM_USER);
            if (cart == null || user == null) {
                response.sendRedirect(context + REDIRECT_ERROR_SESSION);
                return;
            }
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            OrderDao orderDao = AbstractDaoFactory.getInstance().getOrderDao();
            double total = Double.parseDouble(request.getParameter(PARAM_TOTAL));
            int id = orderDao.insert(connection, getOrder(connection, cart, user, total));
            session.removeAttribute(ATTR_CART);
            LOGGER.info(String.format(MESSAGE_SUCCESS, id));
            response.sendRedirect(context + REDIRECT_SUCCESS + id);
        } catch (DBConnectionException e) {
            response.sendRedirect(context + REDIRECT_ERROR_CONNECTION);
        } catch (DBException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DB);
        } catch (DaoException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DAO);
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String context = request.getServletContext().getContextPath();
        try {
            if (Integer.parseInt(request.getParameter(PARAM_SUCCESS)) == 1) {
                request.setAttribute(PARAM_ID, request.getParameter(PARAM_ID));
                request.getRequestDispatcher(INCLUDE_JSP).include(request, response);
            } else {
                throw new BadRequestException();
            }
        } catch (NumberFormatException e) {
            LOGGER.info(MESSAGE_ERROR_PARAMS);
            LOGGER.error(e.getMessage());
            response.sendRedirect(context + REDIRECT_ERROR_PARAMS);
        } catch (BadRequestException e) {
            response.sendRedirect(context + REDIRECT_ERROR_PARAMS);
        }
    }

    private Order getOrder(Connection connection, Map<Integer, Integer> cart, User user, double total) throws DBException, DaoException {
        Order order = new Order();
        order.setUser(user);
        order.setTotal(total);
        Map<Book, Integer> details = new HashMap<>();
        BookDao bookDao = AbstractDaoFactory.getInstance().getBookDao();
        Optional<Book> optional;
        for (int bookId : cart.keySet()) {
            optional = bookDao.get(connection, bookId);
            details.put(optional.orElseThrow(DBException::new), cart.get(bookId));
        }
        order.setDetails(details);
        return order;
    }
}
