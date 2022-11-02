package com.epam.kkorolkov.finalproject.filter;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.LanguageDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Language;
import com.epam.kkorolkov.finalproject.exception.DBConnectionException;
import com.epam.kkorolkov.finalproject.exception.DBException;
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

@WebFilter(filterName = "SetSessionFilter")
public class SetSessionFilter implements Filter {
    private static final Logger LOGGER = LogManager.getLogger("SESSION");

    /* Page to redirect after exception is thrown */
    private static final String REDIRECT_ERROR_CONNECTION =
            "/error?code=500&message=Unable to connect to the database. Try again later.";
    private static final String REDIRECT_ERROR_DAO =
            "/error?code=500&message=Cannot instantiate DAO. See server logs for details.";
    private static final String REDIRECT_ERROR_DB =
            "/error?code=500&message=Database error occurred. See server logs for details.";


    public void destroy() {
    }

    @SuppressWarnings("unchecked")
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String context = request.getServletContext().getContextPath();
        HttpSession session = request.getSession();

        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) {
            session.setAttribute("cart", new HashMap<>());
        }

        Map<Integer, Language> languages = (Map<Integer, Language>) session.getAttribute("languages");
        if (languages == null) {
            DataSource dataSource = null;
            Connection connection = null;
            try {
                dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
                connection = dataSource.getConnection();
                LanguageDao languageDao = AbstractDaoFactory.getInstance().getLanguageDao();
                session.setAttribute("languages", languageDao.getAll(connection));
                LOGGER.info("Session attribute 'languages' has been set.");
            } catch (DBConnectionException e) {
                response.sendRedirect(context + REDIRECT_ERROR_CONNECTION);
            } catch (DBException e) {
                response.sendRedirect(context + REDIRECT_ERROR_DB);
            } catch (DaoException e) {
                response.sendRedirect(context + REDIRECT_ERROR_DAO);
            } finally {
                if (dataSource != null) {
                    dataSource.release(connection);
                }
            }
        }
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {

    }
}
