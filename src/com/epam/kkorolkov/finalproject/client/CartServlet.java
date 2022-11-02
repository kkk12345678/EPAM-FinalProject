package com.epam.kkorolkov.finalproject.client;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.BookDao;
import com.epam.kkorolkov.finalproject.db.dao.LanguageDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Book;
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
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger("CART");

    /** Page to redirect after successful request processing */
    private static final String REDIRECT_SUCCESS = "/cart";

        /** Page to redirect after exception is thrown */
    private static final String REDIRECT_ERROR_CONNECTION =
            "/error?code=500&message=Unable to connect to the database. Try again later.";
    private static final String REDIRECT_ERROR_DAO =
            "/error?code=500&message=Cannot instantiate DAO. See server logs for details.";
    private static final String REDIRECT_ERROR_DB =
            "/error?code=500&message=Database error occurred. See server logs for details.";
    private static final String REDIRECT_ERROR_REQUEST =
            "/error?code=500&message=Unable to update cart. See server logs for details.";

    /** Logger messages */
    private static final String MESSAGE_ID_INVALID = "POST book_id (%s) parameter is not valid integer.";
    private static final String MESSAGE_NOT_FOUND = "Book not found with id = ";

    /** Request parameters */
    private static final String PARAM_ACTION = "action";
    private static final String PARAM_ID = "id";
    private static final String PARAM_QUANTITY = "quantity";

    /** Commands */
    private static final String COMMAND_ADD = "add";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_INCREASE = "increase";
    private static final String COMMAND_DECREASE = "decrease";

    /** Session attributes */
    private static final String ATTR_CART = "cart";

    /** JSP page to include */
    private static final String INCLUDE_JSP = "./jsp/client/cart.jsp";


    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String context = request.getServletContext().getContextPath();
        String action = request.getParameter(PARAM_ACTION);
        String idParameter = request.getParameter(PARAM_ID);
        HttpSession session = request.getSession();

        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute(ATTR_CART);
        try {
            if (action == null) {
                prepareRequest(request, cart);
                request.getRequestDispatcher(INCLUDE_JSP).include(request, response);
                return;
            }
            int id = Integer.parseInt(idParameter);
            if (COMMAND_ADD.equals(action)) {
                if (cart == null) {
                    cart = new HashMap<>();
                }
                int quantity = Integer.parseInt(request.getParameter(PARAM_QUANTITY));
                cart.put(id, cart.containsKey(id) ? quantity + cart.get(id) : quantity);
                session.setAttribute(ATTR_CART, cart);
                try (PrintWriter printWriter = response.getWriter()) {
                    printWriter.print(cart.keySet().size());
                }
                return;
            }
            if (COMMAND_DELETE.equals(action)) {
                cart.remove(id);
            }
            if (COMMAND_INCREASE.equals(action)) {
                cart.put(id, cart.get(id) + 1);
            }
            if (COMMAND_DECREASE.equals(action)) {
                int n = cart.get(id) - 1;
                if (n > 0) {
                    cart.put(id, n);
                } else {
                    cart.remove(id);
                }
            }
            if (cart.keySet().size() > 0) {
                session.setAttribute(ATTR_CART, cart);
            } else {
                session.removeAttribute(ATTR_CART);
            }
            response.sendRedirect(context + REDIRECT_SUCCESS);
        } catch (DBConnectionException e) {
            response.sendRedirect(context + REDIRECT_ERROR_CONNECTION);
        } catch (DBException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DB);
        } catch (DaoException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DAO);
        } catch (BadRequestException e) {
            response.sendRedirect(context + REDIRECT_ERROR_REQUEST);
        } catch (NumberFormatException e) {
            LOGGER.info(String.format(MESSAGE_ID_INVALID, idParameter));
            LOGGER.error(e.getMessage());
            response.sendRedirect(context + REDIRECT_ERROR_REQUEST);
        }
    }

    private void prepareRequest(HttpServletRequest request, Map<Integer, Integer> cart) throws DBException, DaoException, BadRequestException {
        Map<Book, Integer> detailedCart = new HashMap<>();
        DataSource dataSource = null;
        Connection connection = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            BookDao bookDao = AbstractDaoFactory.getInstance().getBookDao();
            Optional<Book> optional;
            for (int id : cart.keySet()) {
                optional = bookDao.get(connection, id);
                if (optional.isPresent()) {
                    detailedCart.put(optional.get(), cart.get(id));
                } else {
                    LOGGER.error(MESSAGE_NOT_FOUND + id);
                    throw new BadRequestException();
                }
            }
            request.setAttribute(ATTR_CART, detailedCart);
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }
}