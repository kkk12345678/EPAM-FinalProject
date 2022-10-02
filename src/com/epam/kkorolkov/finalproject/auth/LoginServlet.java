package com.epam.kkorolkov.finalproject.auth;

import com.epam.kkorolkov.finalproject.db.entity.User;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.util.UserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger("LOGIN");

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String page = "/shop";
        User sessionUser = (User) request.getSession().getAttribute("user");
        if (sessionUser == null) {
            try {
                Optional<User> optional = UserUtils.validate(email, password);

                if (optional.isEmpty()) {
                    LOGGER.info("Invalid email or password.");
                    response.sendRedirect(request.getServletContext().getContextPath() + "/error?code=400&message=Invalid email or password");
                } else {
                    User user = optional.get();
                    request.getSession().setAttribute("user", user);
                    if (user.getIsAdmin()) {
                        page = "/admin";
                    }
                    LOGGER.info(String.format("User with id = %d successfully logged in. Redirecting to %s.", user.getId(), page));
                    response.sendRedirect(request.getServletContext().getContextPath() + page);
                }
            } catch (DBException e) {
                //TODO
                e.printStackTrace();
            }
        } else {
            if (sessionUser.getIsAdmin()) {
                page = "/admin";
            }
            LOGGER.info(String.format("User with id = %d has been already logged in. Redirecting to %s.", sessionUser.getId(), page));
            response.sendRedirect(request.getServletContext().getContextPath() + page);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        LOGGER.info("User is " + user);
        if (user == null) {
            request.getRequestDispatcher("./jsp/user/auth/login.jsp").include(request, response);
        } else {
            String page = (!user.getIsAdmin()) ? "/shop" : "/admin";
            LOGGER.info(String.format("User with id = %d has already logged in. Redirecting to %s." , user.getId(), page));
            response.sendRedirect(request.getServletContext().getContextPath() + page);
        }
    }
}
