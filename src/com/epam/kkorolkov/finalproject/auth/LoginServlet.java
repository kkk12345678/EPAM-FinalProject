package com.epam.kkorolkov.finalproject.auth;

import com.epam.kkorolkov.finalproject.db.entity.User;
import com.epam.kkorolkov.finalproject.exception.DbConnectionException;
import com.epam.kkorolkov.finalproject.exception.DbException;
import com.epam.kkorolkov.finalproject.exception.DaoException;
import com.epam.kkorolkov.finalproject.util.UserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger("LOGIN");

    /** Page to redirect after successful login */
    private static final String REDIRECT_ADMIN = "/admin";
    private static final String REDIRECT_CLIENT = "/shop";

    /** Page to redirect after unsuccessful login */
    private static final String REDIRECT_UNSUCCESSFUL_LOGIN =
            "/error?code=400&message=";

    /** Page to redirect after exception is thrown */
    private static final String REDIRECT_ERROR_CONNECTION =
            "/error?code=500&message=Unable to connect to the database. Try again later.";
    private static final String REDIRECT_ERROR_DAO =
            "/error?code=500&message=Cannot instantiate DAO. See server logs for details.";
    private static final String REDIRECT_ERROR_DB =
            "/error?code=500&message=Database error occurred. See server logs for details.";
    private static final String REDIRECT_ERROR_NO_ALGORITHM =
            "/error?code=500&message=Hashing algorithm not found. See server logs for details.";

    /** Logger messages */
    private static final String MESSAGE_CREDENTIALS_INVALID =
            "Invalid email or password.";
    private static final String MESSAGE_SUCCESSFUL_LOGIN =
            "User with id = %d successfully logged in. Redirecting to %s.";
    private static final String MESSAGE_ALREADY_LOGGED_IN =
            "User with id = %d has been already logged in. Redirecting to %s.";
    private static final String MESSAGE_CURRENT_USER =
            "Current user is ";

    /** Request parameters keys */
    private static final String PARAM_USER = "user";
    private static final String PARAM_EMAIL = "email";
    private static final String PARAM_PASSWORD = "password";

    /** JSP page to include */
    private static final String INCLUDE_JSP = "./jsp/auth/login.jsp";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String context = request.getServletContext().getContextPath();
        String email = request.getParameter(PARAM_EMAIL);
        String password = request.getParameter(PARAM_PASSWORD);
        String page = REDIRECT_CLIENT;
        try {
            Optional<User> optional = UserUtils.validate(email, password);
            if (optional.isEmpty()) {
                LOGGER.info(MESSAGE_CREDENTIALS_INVALID);
                response.sendRedirect(context + REDIRECT_UNSUCCESSFUL_LOGIN + MESSAGE_CREDENTIALS_INVALID);
                return;
            }
            User user = optional.get();
            request.getSession().setAttribute(PARAM_USER, user);
            if (user.getIsAdmin()) {
                page = REDIRECT_ADMIN;
            }
            LOGGER.info(String.format(MESSAGE_SUCCESSFUL_LOGIN, user.getId(), page));
            response.sendRedirect(context + page);
        } catch (DbConnectionException e) {
            response.sendRedirect(context + REDIRECT_ERROR_CONNECTION);
        } catch (DbException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DB);
        } catch (DaoException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DAO);
        } catch (NoSuchAlgorithmException e) {
            response.sendRedirect(context + REDIRECT_ERROR_NO_ALGORITHM);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(PARAM_USER);
        LOGGER.info(MESSAGE_CURRENT_USER + user);
        if (user != null) {
            String page = (!user.getIsAdmin()) ? REDIRECT_CLIENT : REDIRECT_ADMIN;
            LOGGER.info(String.format(MESSAGE_ALREADY_LOGGED_IN, user.getId(), page));
            response.sendRedirect(request.getServletContext().getContextPath() + page);
            return;
        }
        request.getRequestDispatcher(INCLUDE_JSP).include(request, response);
    }
}
