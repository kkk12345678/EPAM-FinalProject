package com.epam.kkorolkov.finalproject.util;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Class {@code ErrorServlet} is a servlet which task is to change session
 * attribute {@code locale}.
 */
@WebServlet("/change-locale")
public class ChangeLocaleServlet extends HttpServlet {
    /** Keys of request parameters */
    private static final String PARAM_LOCALE = "locale";
    private static final String PARAM_PAGE = "page";

    /** Replacement strings */
    private static final String REPLACE_FROM = "ggg";
    private static final String REPLACE_TO = "&";

    /**
     * {@code doGet} method handles GET request.
     * Sets session attribute {@code locale} as specified in GET parameter {@code locale}.
     *
     * @param request {@link HttpServletRequest} object provided by Tomcat.
     * @param response {@link HttpServletResponse} object provided by Tomcat.
     *
     * @throws IOException is thrown if an input or output exception occurs.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String locale = request.getParameter(PARAM_LOCALE);
        String page = request.getParameter(PARAM_PAGE);
        HttpSession session = request.getSession();
        session.setAttribute(PARAM_LOCALE, locale);
        response.sendRedirect(page.replaceAll(REPLACE_FROM, REPLACE_TO));
    }
}
