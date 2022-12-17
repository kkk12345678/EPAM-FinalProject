package com.epam.kkorolkov.finalproject.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * {@code CharsetFilter} sets encoding 'UTF-8' to all pages.
 */
@WebFilter(filterName = "CharsetFilter ")
public class CharsetFilter implements Filter {
    private static final String ENCODING_UTF_8 = "UTF-8";
    private static final String CHARSET_UTF_8 = "text/html; charset=UTF-8";
    private static final String REQUEST_ENCODING = "requestEncoding";

    private String encoding;

    /**
     * Method {@code init} sets encoding field to 'UTF-8' if it is not yet set.
     *
     * @param config - a filter configuration object used by a servlet container to pass information
     * to a filter during initialization.
     */
    public void init(FilterConfig config) {
        encoding = config.getInitParameter(REQUEST_ENCODING);
        if (encoding == null) {
            encoding = ENCODING_UTF_8;
        }
    }

    /**
     * Method {@code doFilter} sets encoding 'UTF-8' to both request and response.
     *
     * @param request - request to process.
     * @param response - response associated with the request.
     * @param chain Provides access to the next filter in the chain for this filter
     *              to pass the request and response to for further processing.
     *
     * @throws IOException if an I/O error occurs during this filter's processing of the request
     * @throws ServletException is thrown if if the processing fails for any other reason.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (null == request.getCharacterEncoding()) {
            request.setCharacterEncoding(encoding);
        }
        response.setContentType(CHARSET_UTF_8);
        response.setCharacterEncoding(ENCODING_UTF_8);
        chain.doFilter(request, response);
    }

    public void destroy() {}
}
