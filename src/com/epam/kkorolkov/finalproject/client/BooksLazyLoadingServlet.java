package com.epam.kkorolkov.finalproject.client;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.BookDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Book;
import com.epam.kkorolkov.finalproject.exception.DbConnectionException;
import com.epam.kkorolkov.finalproject.exception.DbException;
import com.epam.kkorolkov.finalproject.exception.DaoException;
import com.epam.kkorolkov.finalproject.util.CatalogueUtils;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * The {@code BooksLazyLoadingServlet} is a servlet which task is to
 * retrieve data from the table <i>books</i> with parameters specified
 * in request parameters.
 *
 * Only {@code doGet} method is overridden.
 */
@WebServlet("/load-books")
public class BooksLazyLoadingServlet extends HttpServlet {
    /** Logger */
    private static final Logger LOGGER = LogManager.getLogger("GET BOOKS");

    /** Number of shown products per page */
    private static final int LIMIT = 20;

    /** Request parameters */
    private static final String PARAM_PAGE = "page";

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
    private static final String MESSAGE_ERROR_PAGE = "Page parameter is specified incorrectly.";

    /**
     * {@code doGet} method handles GET request. Retrieves data from
     * {@link BookDao#getAll(Connection, int, int, Map)} using
     * filter parameters specified in request parameters. Writes the result
     * to the response in JSON format.
     *
     * @param request {@link HttpServletRequest} object provided by Tomcat.
     * @param response {@link HttpServletResponse} object provided by Tomcat.
     *
     * @throws IOException is thrown if an input or output exception occurs.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String context = request.getServletContext().getContextPath();
        Map<String, String> parameters = CatalogueUtils.setBookParameters(request);
        DataSource dataSource = null;
        Connection connection = null;
        try {
            int page = Integer.parseInt(request.getParameter(PARAM_PAGE));
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            BookDao bookDao = AbstractDaoFactory.getInstance().getBookDao();
            List<Book> books = bookDao.getAll(connection, LIMIT, LIMIT * (page - 1), parameters);
            String json = new Gson().toJson(books);
            try (PrintWriter writer = response.getWriter()) {
                writer.println(json);
            }
        } catch (DbConnectionException e) {
            response.sendRedirect(context + REDIRECT_ERROR_CONNECTION);
        } catch (DbException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DB);
        } catch (DaoException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DAO);
        } catch (NumberFormatException e) {
            LOGGER.info(MESSAGE_ERROR_PAGE);
            LOGGER.error(e.getMessage());
            response.sendRedirect(context + REDIRECT_ERROR_PARAMS);
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }
}
