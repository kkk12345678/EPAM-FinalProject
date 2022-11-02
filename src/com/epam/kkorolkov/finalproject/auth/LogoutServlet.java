package com.epam.kkorolkov.finalproject.auth;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    /** Request parameters keys */
    private static final String PARAM_USER = "user";

    /** Page to redirect after successful logout */
    private static final String REDIRECT_ADMIN = "/shop";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().removeAttribute(PARAM_USER);
        response.sendRedirect(request.getServletContext().getContextPath() + REDIRECT_ADMIN);
    }
}
