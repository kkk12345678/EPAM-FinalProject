package com.epam.kkorolkov.finalproject.admin.category;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.CategoryDao;
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
 * The {@code DeleteCategoryServlet} is a servlet which task is to
 * delete a record from the table <i>categories</i>.
 *
 * Only {@code doPost} method is overridden.
 */
@WebServlet("/admin/delete-category")
public class DeleteCategoryServlet extends HttpServlet {
    /** Logger */
    private static final Logger LOGGER = LogManager.getLogger("DELETE CATEGORY");

    /** Page to redirect after successful request processing */
    private static final String REDIRECT_SUCCESS = "/admin/categories";

    /** Page to redirect after exception is thrown */
    private static final String REDIRECT_ERROR_CONNECTION =
            "/error?code=500&message=Unable to connect to the database. Try again later.";
    private static final String REDIRECT_ERROR_DAO =
            "/error?code=500&message=Cannot instantiate DAO. See server logs for details.";
    private static final String REDIRECT_ERROR_DB =
            "/error?code=500&message=Database error occurred. See server logs for details.";
    private static final String REDIRECT_ERROR_REQUEST =
            "/error?code=500&message=POST request parameter ID is not a positive integer. See server logs for details.";

    /** Logger messages */
    private static final String MESSAGE_ID_INVALID = "POST category_id (%s) parameter is not integer.";

    /** Keys of request parameters */
    private static final String PARAM_ID = "id";

    /**
     * {@code doPost} method handles POST request with one parameter
     * {@code id} - id of a category to be deleted.
     *
     * In order to implement the behavior method gets {@link DataSource} from the factory
     * and then gets {@link Connection} on the provided datasource. These operations may
     * produce {@link DbConnectionException} which indicates that database is unreachable.<br><br>
     *
     * The next step is to get {@link CategoryDao} from the factory. This may produce
     * {@link DaoException} if DAO is cannot be instantiated.<br><br>
     *
     * Finally, method {@code delete} is invoked on obtained DAO. This may produce
     * {@link NumberFormatException} if POST parameter <i>id</i> is not a positive integer,
     * or {@link DbException} if in the database there is no record with <i>id</i>
     * or during communication some {@link java.sql.SQLException} is thrown.<br><br>
     *
     * @param request {@link HttpServletRequest} object provided by Tomcat.
     * @param response {@link HttpServletResponse} object provided by Tomcat.
     *
     * @throws IOException is thrown if an input or output exception occurs.
     *
     * @see AbstractDataSourceFactory#getDataSource()
     * @see AbstractDaoFactory#getCategoryDao()
     * @see CategoryDao#delete(Connection, int)
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String context = request.getServletContext().getContextPath();
        Connection connection = null;
        DataSource dataSource = null;
        String idParameter= request.getParameter(PARAM_ID);
        try {
            int id = Integer.parseInt(idParameter);
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            if (connection != null) {
                CategoryDao categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();
                categoryDao.delete(connection, id);
            }
            response.sendRedirect(context + REDIRECT_SUCCESS);
        } catch (DbConnectionException e) {
            response.sendRedirect(context + REDIRECT_ERROR_CONNECTION);
        } catch (DbException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DB);
        } catch (DaoException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DAO);
        } catch (NumberFormatException e) {
            LOGGER.info(String.format(MESSAGE_ID_INVALID, idParameter));
            LOGGER.error(e.getMessage());
            response.sendRedirect(context + REDIRECT_ERROR_REQUEST);
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }
}
