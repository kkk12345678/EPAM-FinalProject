package com.epam.kkorolkov.finalproject.admin.user;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.UserDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.User;
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
 * The {@code BlockUserServlet} is a servlet which task is to
 * update field {@code isBlocked} by setting it to {@code true}.
 *
 * Only {@code doPost} method is overridden.
 */
@WebServlet("/admin/block-user")
public class BlockUserServlet extends HttpServlet {
    /** Logger */
    private static final Logger LOGGER = LogManager.getLogger("BLOCK USER");

    /** Page to redirect after successful request processing */
    private static final String REDIRECT_SUCCESS = "/admin/users";

    /** Page to redirect after exception is thrown */
    private static final String REDIRECT_ERROR_CONNECTION =
            "/error?code=500&message=Unable to connect to the database. Try again later.";
    private static final String REDIRECT_ERROR_DAO =
            "/error?code=500&message=Cannot instantiate DAO. See server logs for details.";
    private static final String REDIRECT_ERROR_DB =
            "/error?code=500&message=Database error occurred. See server logs for details.";
    private static final String REDIRECT_ERROR_REQUEST =
            "/error?code=500&message=POST request parameter ID is not a valid integer. See server logs for details.";

    /** Logger messages */
    private static final String MESSAGE_ID_INVALID = "POST user_id (%s) parameter is not integer.";

    /** Keys of request parameters */
    private static final String PARAM_ID = "id";

    /**
     * {@code doPost} method handles POST request with the parameter which specify a user id.
     * Method reads request parameter, invokes {@link UserDao#get(Connection, int)}
     * to get an instance of {@link User}, changes the field {@code isBlocked},
     * and invokes {@link UserDao#update(Connection, User)}.
     *
     * @param request {@link HttpServletRequest} object provided by Tomcat.
     * @param response {@link HttpServletResponse} object provided by Tomcat.
     * @throws IOException is thrown if an input or output exception occurs.
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String context = request.getServletContext().getContextPath();
        String idParameter = request.getParameter(PARAM_ID);
        Connection connection = null;
        DataSource dataSource = null;
        try {
            int id = Integer.parseInt(idParameter);
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            UserDao userDao = AbstractDaoFactory.getInstance().getUserDao();
            User user = userDao.get(connection, id).orElseThrow(DbException::new);
            user.setBlocked(true);
            userDao.update(connection, user);
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
