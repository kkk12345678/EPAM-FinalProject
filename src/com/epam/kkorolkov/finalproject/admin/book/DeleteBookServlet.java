package com.epam.kkorolkov.finalproject.admin.book;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.BookDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.exception.DBConnectionException;
import com.epam.kkorolkov.finalproject.exception.DBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

/**
 * The {@code DeleteBookServlet} is a servlet which task is to
 * delete from the database record of product.
 *
 * Only {@code doPost} method is overridden.
 *
 */

@WebServlet("/admin/delete-book")
public class DeleteBookServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger("DELETE BOOK");

    /* Logger messages */
    private static final String MESSAGE_DATABASE_UNAVAILABLE = "";
    private static final String MESSAGE_ID_INVALID = "POST book_id (%s) parameter is not integer.";
    private static final String MESSAGE_DAO_INVALID = "Cannot obtain an instance of DAO.";

    /* Page to redirect after successful request processing */
    private static final String REDIRECT_FORMAT = "books?page=%s";

    /* Keys of request parameters */
    private static final String PARAM_ID = "id";
    private static final String PARAM_PAGE = "page";

    /**
     * {@code doPost} method handles POST request with two parameters:<br><ul>
     *     <li><i>id</i> - id of a book to be deleted</li>
     *     <li><i>page</i> - number of a page to which it is necessary to
     *     be redirected after successful deletion.</li></ul><br>
     *
     * In order to implement the behavior method gets {@code DataSource} from the factory
     * and then gets {@code Connection} on the provided datasource. These operations may
     * produce {@code DBConnectionException} which indicates that database is unreachable.<br><br>
     *
     * The next step is to get {@code BookDao} from the factory. This may produce
     * {@code IllegalStateException} if DAO is cannot be instantiated.<br><br>
     *
     * Finally, method {@code delete} is invoked on obtained DAO. This may produce
     * {@code NumberFormatException} if POST parameter <i>id</i> is not integer, or
     * {@code DBException} if in the database there is no record with <i>id</i> or
     * during communication some {@code SQLException} is thrown.
     *
     * @param request - HttpServletRequest object provided by Tomcat.
     * @param response - HttpServletResponse object provided by Tomcat.
     * @throws IOException is thrown if an input or output exception occurs.
     *
     * @see AbstractDataSourceFactory#getDataSource()
     * @see AbstractDaoFactory#getBookDao()
     * @see BookDao#delete(Connection, int)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Connection connection = null;
        DataSource dataSource = null;
        String idParameter = request.getParameter(PARAM_ID);
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            BookDao bookDao = AbstractDaoFactory.getInstance().getBookDao();
            bookDao.delete(connection, Integer.parseInt(idParameter));
            response.sendRedirect(String.format(REDIRECT_FORMAT,  request.getParameter(PARAM_PAGE)));
        } catch (DBConnectionException e) {
            //TODO handle DBConnectionException
            LOGGER.info(MESSAGE_DATABASE_UNAVAILABLE);
            LOGGER.error(e.getMessage());
            //response.sendRedirect();
        } catch (DBException e) {
            //TODO handle DBException
        } catch (IllegalStateException e) {
            LOGGER.info(MESSAGE_DAO_INVALID);
            LOGGER.error(e.getMessage());
            //TODO handle IllegalStateException
        } catch (NumberFormatException e) {
            LOGGER.info(String.format(MESSAGE_ID_INVALID, idParameter));
            //TODO Handle NumberFormatException
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }
}
