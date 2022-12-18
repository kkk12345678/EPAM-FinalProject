package com.epam.kkorolkov.finalproject.filter;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.LanguageDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Language;
import com.epam.kkorolkov.finalproject.exception.DbConnectionException;
import com.epam.kkorolkov.finalproject.exception.DbException;
import com.epam.kkorolkov.finalproject.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Class {@code SetSessionFilter} sets {@link HttpSession} parameters
 * 'cart' and 'languages' if they are not set previously.
 */
@WebFilter(filterName = "SetSessionFilter")
public class SetSessionFilter implements Filter {
    /** Logger */
    private static final Logger LOGGER = LogManager.getLogger("SESSION");

    /** Page to redirect after exception is thrown */
    private static final String REDIRECT_ERROR_CONNECTION =
            "/error?code=500&message=Unable to connect to the database. Try again later.";
    private static final String REDIRECT_ERROR_DAO =
            "/error?code=500&message=Cannot instantiate DAO. See server logs for details.";
    private static final String REDIRECT_ERROR_DB =
            "/error?code=500&message=Database error occurred. See server logs for details.";

    /** Session attributes */
    private static final String ATTR_CART = "cart";
    private static final String ATTR_LANGUAGES = "languages";

    /** Logger messages */
    private static final String MESSAGE_SUCCESS = "Session attribute 'languages' has been set.";

    /**
     * Sets 'cart' attribute to empty {@code Map} if it is null;
     * Sets 'languages' attribute to {@code Map} containing all
     * languages from the database, if the attribute is null;
     *
     * @param request request to process.
     * @param response response associated with the request.
     * @param chain provides access to the next filter in the chain for this filter
     *              to pass the request and response to for further processing.
     *
     * @throws IOException is thrown if an I/O error occurs
     * during this filter's processing of the request
     * @throws ServletException is thrown if if the processing fails for any other reason.
     */
    @SuppressWarnings("unchecked")
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String context = httpServletRequest.getServletContext().getContextPath();
        HttpSession session = httpServletRequest.getSession();
        if (session.getAttribute(ATTR_CART) == null) {
            session.setAttribute(ATTR_CART, new HashMap<>());
        }
        Map<Integer, Language> languages = (Map<Integer, Language>) session.getAttribute(ATTR_LANGUAGES);
        if (languages == null) {
            DataSource dataSource = null;
            Connection connection = null;
            try {
                dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
                connection = dataSource.getConnection();
                LanguageDao languageDao = AbstractDaoFactory.getInstance().getLanguageDao();
                session.setAttribute(ATTR_LANGUAGES, languageDao.getAll(connection));
                LOGGER.info(MESSAGE_SUCCESS);

            } catch (DbConnectionException e) {
                httpServletResponse.sendRedirect(context + REDIRECT_ERROR_CONNECTION);
            } catch (DbException e) {
                httpServletResponse.sendRedirect(context + REDIRECT_ERROR_DB);
            } catch (DaoException e) {
                httpServletResponse.sendRedirect(context + REDIRECT_ERROR_DAO);
            } finally {
                if (dataSource != null) {
                    dataSource.release(connection);
                }
            }
        }
        chain.doFilter(httpServletRequest, httpServletResponse);
    }

    public void destroy() {}

    public void init(FilterConfig config) {}
}
