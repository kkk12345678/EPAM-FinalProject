package com.epam.kkorolkov.finalproject.filter;

import com.epam.kkorolkov.finalproject.db.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * {@code SecurityFilter} does not allow a {@code User} without admin privileges to
 * browse pages which manages catalogue, users, and orders.
 */
@WebFilter(filterName = "SecurityFilter")
public class SecurityFilter implements Filter {
    /** Logger */
    private static final Logger LOGGER = LogManager.getLogger("SECURITY");

    /** Logger messages */
    private static final String MESSAGE_ACCESS_GRANTED = "Access for user %s is granted.";
    private static final String MESSAGE_ACCESS_DENIED = "Access for user %s is denied.";

    /** Session attributes */
    private static final String ATTR_USER = "user";

    /** Error redirect page */
    private static final String MESSAGE_REDIRECT_UNAUTHORIZED = "/error?code=401";

    /**
     * Method {@code doFilter} retrieves a {@code User} from {@code HttpSession} and
     * redirect them to error page if user is not an administrator.
     *
     * @param request - request to process.
     * @param response - response associated with the request.
     * @param chain Provides access to the next filter in the chain for this filter
     *              to pass the request and response to for further processing.
     *
     * @throws IOException if an I/O error occurs during this filter's processing of the request
     * @throws ServletException is thrown if if the processing fails for any other reason.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        User user = (User) ((HttpServletRequest) request).getSession().getAttribute(ATTR_USER);
        if (user == null || !user.getIsAdmin()) {
            LOGGER.info(String.format(MESSAGE_ACCESS_DENIED, user));
            ((HttpServletResponse) response).sendRedirect(
                    request.getServletContext().getContextPath() + MESSAGE_REDIRECT_UNAUTHORIZED);
        } else {
            LOGGER.info(String.format(MESSAGE_ACCESS_GRANTED, user));
            chain.doFilter(request, response);
        }
    }

    public void init(FilterConfig config) {}

    public void destroy() {}
}
