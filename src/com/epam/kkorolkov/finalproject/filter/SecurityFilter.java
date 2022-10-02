package com.epam.kkorolkov.finalproject.filter;

import com.epam.kkorolkov.finalproject.db.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "SecurityFilter")
public class SecurityFilter implements Filter {
    private static final Logger LOGGER = LogManager.getLogger("SECURITY");

    public void init(FilterConfig config) {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        User user = (User) ((HttpServletRequest) req).getSession().getAttribute("user");
        LOGGER.info("User is " + user);
        if (user == null || !user.getIsAdmin()) {
            LOGGER.info(String.format("Access for user %s is denied.", user));
            ((HttpServletResponse) resp).sendRedirect(req.getServletContext().getContextPath() + "/error?code=401");
        } else {
            LOGGER.info(String.format("Access for user %s is granted.", user));
            chain.doFilter(req, resp);
        }
    }

    public void destroy() {
    }
}
