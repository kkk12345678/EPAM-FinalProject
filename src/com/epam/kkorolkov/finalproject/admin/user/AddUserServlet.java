package com.epam.kkorolkov.finalproject.admin.user;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.UserDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.User;
import com.epam.kkorolkov.finalproject.exception.DBConnectionException;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.exception.DaoException;
import com.epam.kkorolkov.finalproject.exception.ValidationException;
import com.epam.kkorolkov.finalproject.util.UserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;

@WebServlet("/admin/add-user")
public class AddUserServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger("ADD USER");

    /** Page to redirect after successful request processing */
    private static final String REDIRECT_SUCCESS = "/admin/users";

    /** Page to redirect after exception is thrown */
    private static final String REDIRECT_ERROR_CONNECTION =
            "/error?code=500&message=Unable to connect to the database. Try again later.";
    private static final String REDIRECT_ERROR_DAO =
            "/error?code=500&message=Cannot instantiate DAO. See server logs for details.";
    private static final String REDIRECT_ERROR_DB =
            "/error?code=500&message=Database error occurred. See server logs for details.";
    private static final String REDIRECT_ERROR_VALIDATION =
            "/error?code=400&message=";
    private static final String REDIRECT_ERROR_NO_ALGORITHM =
            "/error?code=500&message=Hashing algorithm not found. See server logs for details.";

    /** Logger messages */
    private static final String MESSAGE_NO_ALGORITHM = "Hashing algorithm not found.";

    /** Request parameters keys */
    private static final String PARAM_FIRST_NAME = "firstname";
    private static final String PARAM_LAST_NAME = "lastname";
    private static final String PARAM_EMAIL = "email";
    private static final String PARAM_PASSWORD = "password";

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String context = request.getServletContext().getContextPath();
        String firstName = request.getParameter(PARAM_FIRST_NAME);
        String lastName = request.getParameter(PARAM_LAST_NAME);
        String email = request.getParameter(PARAM_EMAIL);
        String password = request.getParameter(PARAM_PASSWORD);
        Connection connection = null;
        DataSource dataSource = null;
        try {
            User user = new User();
            user.setEmail(email);
            user.setPassword(UserUtils.hash(password));
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setAdmin(false);
            user.setBlocked(false);
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            if (connection != null) {
                UserDao userDao = AbstractDaoFactory.getInstance().getUserDao();
                userDao.insert(connection, user);
            }
            response.sendRedirect(context + REDIRECT_SUCCESS);
        } catch (DBConnectionException e) {
            response.sendRedirect(context + REDIRECT_ERROR_CONNECTION);
        } catch (DBException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DB);
        } catch (DaoException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DAO);
        } catch (ValidationException e) {
            response.sendRedirect(context + REDIRECT_ERROR_VALIDATION + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            LOGGER.info(MESSAGE_NO_ALGORITHM);
            LOGGER.error(e.getMessage());
            response.sendRedirect(context + REDIRECT_ERROR_NO_ALGORITHM);
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }
}
