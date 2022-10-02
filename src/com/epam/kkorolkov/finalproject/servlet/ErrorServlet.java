package com.epam.kkorolkov.finalproject.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/error")
public class ErrorServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        String message = request.getParameter("message");
        request.setAttribute("message", message);
        request.getRequestDispatcher("./jsp/error/" + code + ".jsp").include(request, response);
    }
}
