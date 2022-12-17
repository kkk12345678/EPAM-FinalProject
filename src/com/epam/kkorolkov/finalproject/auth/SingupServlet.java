package com.epam.kkorolkov.finalproject.auth;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.UserDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.User;
import com.epam.kkorolkov.finalproject.exception.DbConnectionException;
import com.epam.kkorolkov.finalproject.exception.DbException;
import com.epam.kkorolkov.finalproject.exception.DaoException;
import com.epam.kkorolkov.finalproject.exception.ValidationException;
import com.epam.kkorolkov.finalproject.util.UserUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.Optional;

@WebServlet("/signup")
public class SingupServlet extends HttpServlet {
    /** Page to redirect after successful signup */
    private static final String REDIRECT_CLIENT = "/shop";

    /** Page to redirect after exception is thrown */
    private static final String REDIRECT_ERROR_CONNECTION =
            "/error?code=500&message=Unable to connect to the database. Try again later.";
    private static final String REDIRECT_ERROR_DAO =
            "/error?code=500&message=Cannot instantiate DAO. See server logs for details.";
    private static final String REDIRECT_ERROR_DB =
            "/error?code=500&message=Database error occurred. See server logs for details.";
    private static final String REDIRECT_ERROR_NO_ALGORITHM =
            "/error?code=500&message=Hashing algorithm not found. See server logs for details.";
    private static final String REDIRECT_USER_EXISTS =
            "%s/error?code=400&message=User with email %s already exists.";
    private static final String REDIRECT_ERROR_VALIDATION =
            "/error?code=400&message=";

    /** Request parameters keys */
    private static final String PARAM_FIRST_NAME = "firstname";
    private static final String PARAM_LAST_NAME = "lastname";
    private static final String PARAM_EMAIL = "email";
    private static final String PARAM_PASSWORD = "password";

    /** Request attributes */
    private static final String ATTR_USER = "user";


    /** JSP page to include */
    private static final String INCLUDE_JSP = "./jsp/auth/signup.jsp";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String context = request.getServletContext().getContextPath();
        Connection connection = null;
        DataSource dataSource = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            UserDao userDao = AbstractDaoFactory.getInstance().getUserDao();
            String email = request.getParameter(PARAM_EMAIL);
            Optional<User> optional = userDao.get(connection, email);
            if (optional.isPresent()) {
                response.sendRedirect(String.format(REDIRECT_USER_EXISTS, context, email));
                return;
            }
            UserUtils.validateEmail(email);
            User user = new User();
            user.setEmail(email);
            user.setFirstName(request.getParameter(PARAM_FIRST_NAME));
            user.setLastName(request.getParameter(PARAM_LAST_NAME));
            user.setPassword(request.getParameter(PARAM_PASSWORD));
            user.setBlocked(false);
            user.setAdmin(false);
            user.setId(userDao.insert(connection, user));
            request.getSession().setAttribute(ATTR_USER, user);
            response.sendRedirect(context + REDIRECT_CLIENT);
        } catch (DbConnectionException e) {
            response.sendRedirect(context + REDIRECT_ERROR_CONNECTION);
        } catch (DbException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DB);
        } catch (DaoException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DAO);
        } catch (ValidationException e) {
            response.sendRedirect(context + REDIRECT_ERROR_VALIDATION + e.getMessage());
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(INCLUDE_JSP).include(request, response);
    }
}
