package com.epam.kkorolkov.finalproject.util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Class {@code ErrorServlet} processes all thrown checked exceptions.
 */
@WebServlet("/error")
public class ErrorServlet extends HttpServlet {
    /** Keys of request parameters */
    private static final String PARAM_CODE = "code";
    private static final String PARAM_MESSAGE = "message";

    /** JSP page to include */
    private static final String INCLUDE_JSP = "./jsp/error/%s.jsp";

    /**
     * {@code doGet} method handles GET request.
     *
     * @param request - {@code HttpServletRequest} object provided by Tomcat.
     * @param response - {@code HttpServletResponse} object provided by Tomcat.
     *
     * @throws ServletException is thrown if the request for the GET could not be handled.
     * @throws IOException is thrown if an input or output exception occurs.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter(PARAM_CODE);
        String message = request.getParameter(PARAM_MESSAGE);
        request.setAttribute(PARAM_MESSAGE, message);
        request.getRequestDispatcher(String.format(INCLUDE_JSP, code)).include(request, response);
    }
}
