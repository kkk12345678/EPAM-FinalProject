package com.epam.kkorolkov.finalproject.filter;

import com.epam.kkorolkov.finalproject.db.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * {@code WelcomeFilter} redirects user who visits the main page
 * to the appropriate page depending on their role.
 */
@WebFilter(filterName = "WelcomeFilter")
public class WelcomeFilter implements Filter {
    private static final String ATTR_USER = "user";
    private static final Logger LOGGER = LogManager.getLogger("WELCOME");
    private static final String MESSAGE_USER = "User is ";
    private static final String MESSAGE_REDIRECT = "Redirecting to";
    private static final String PAGE_SHOP = "/shop";
    private static final String PAGE_ADMIN = "/admin";

    /**
     * The {@code doFilter} method retrieves {@link User} from {@link HttpSession}.
     * If {@link User} is admin redirects user to administrator's panel,
     * otherwise redirects to client's main page.
     *
     * @param request request to process.
     * @param response response associated with the request.
     * @param chain provides access to the next filter in the chain for this filter
     *              to pass the request and response to for further processing.
     *
     * @throws IOException is thrown if an I/O error occurs
     * during this filter's processing of the request.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        User user = (User) ((HttpServletRequest) request).getSession().getAttribute(ATTR_USER);
        LOGGER.info(MESSAGE_USER + user);
        String page = (user == null || !user.getIsAdmin()) ? PAGE_SHOP : PAGE_ADMIN;
        LOGGER.info(MESSAGE_REDIRECT + page);
        ((HttpServletResponse) response).sendRedirect(request.getServletContext().getContextPath() + page);
    }

    public void init(FilterConfig config) {}

    public void destroy() {}
}
