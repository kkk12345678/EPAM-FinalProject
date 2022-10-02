package com.epam.kkorolkov.finalproject.filter;

import com.epam.kkorolkov.finalproject.db.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "WelcomeFilter")
public class WelcomeFilter implements Filter {
    private static final Logger LOGGER = LogManager.getLogger("WELCOME");

    public void init(FilterConfig config) throws ServletException {

    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        User user = (User) ((HttpServletRequest) req).getSession().getAttribute("user");
        LOGGER.info("User is " + user);
        String page = (user == null || !user.getIsAdmin()) ? "/shop" : "/admin";
        LOGGER.info("Redirecting to " + page);
        ((HttpServletResponse) resp).sendRedirect(req.getServletContext().getContextPath() + page);
    }
    public void destroy() {
    }
}
