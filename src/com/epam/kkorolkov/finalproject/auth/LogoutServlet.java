package com.epam.kkorolkov.finalproject.auth;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The {@code LogoutServlet} is a servlet which task is to
 * remove user's data from the session.
 *
 * Only {@code doGet} method is overridden.
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    /** Request parameters keys */
    private static final String PARAM_USER = "user";

    /** Page to redirect after successful logout */
    private static final String REDIRECT_SHOP = "/shop";

    /**
     * {@code doGet} method handles GET request.
     * Removes user's data from the session and redirects the user to the main page.
     *
     * @param request {@link HttpServletRequest} object provided by Tomcat.
     * @param response {@link HttpServletResponse} object provided by Tomcat.
     *
     * @throws IOException is thrown if an input or output exception occurs.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().removeAttribute(PARAM_USER);
        response.sendRedirect(request.getServletContext().getContextPath() + REDIRECT_SHOP);
    }
}
