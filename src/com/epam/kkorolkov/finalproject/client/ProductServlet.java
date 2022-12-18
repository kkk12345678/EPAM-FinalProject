package com.epam.kkorolkov.finalproject.client;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.BookDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Book;
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
import java.util.Optional;

/**
 * The {@code ProductServlet} is a servlet which task is to represent
 * a product corresponding to a row in the table <i>books</i>
 * to a customer.
 *
 * {@code doGet} method is overridden.
 */
@WebServlet("/product/*")
public class ProductServlet extends HttpServlet {
    /** Page to redirect if book not found */
    private static final String REDIRECT_NO_PRODUCT = "/shop";

    /** Page to redirect after exception is thrown */
    private static final String REDIRECT_ERROR_CONNECTION =
            "/error?code=500&message=Unable to connect to the database. Try again later.";
    private static final String REDIRECT_ERROR_DAO =
            "/error?code=500&message=Cannot instantiate DAO. See server logs for details.";
    private static final String REDIRECT_ERROR_DB =
            "/error?code=500&message=Database error occurred. See server logs for details.";

    /** Request attributes */
    private static final String ATTR_BOOK = "book";

    /** JSP page to include */
    private static final String INCLUDE_JSP = "../jsp/client/product.jsp";

    /**
     * {@code doGet} method handles GET request. Retrieves data from
     * {@link BookDao#get(Connection, int)} and shows it to a customer.
     *
     * @param request {@link HttpServletRequest} object provided by Tomcat.
     * @param response {@link HttpServletResponse} object provided by Tomcat.
     *
     * @throws ServletException is thrown if the request for the GET could not be handled.
     * @throws IOException is thrown if an input or output exception occurs.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String context = request.getServletContext().getContextPath();
        DataSource dataSource = null;
        Connection connection = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            BookDao bookDao = AbstractDaoFactory.getInstance().getBookDao();
            Optional<Book> optional = bookDao.get(connection, request.getPathInfo().substring(1));
            if (optional.isEmpty()) {
                response.sendRedirect(context + REDIRECT_NO_PRODUCT);
                return;
            }
            request.setAttribute(ATTR_BOOK, optional.get());
            request.getRequestDispatcher(INCLUDE_JSP).include(request, response);
        } catch (DbConnectionException e) {
            response.sendRedirect(context + REDIRECT_ERROR_CONNECTION);
        } catch (DbException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DB);
        } catch (DaoException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DAO);
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }
}
