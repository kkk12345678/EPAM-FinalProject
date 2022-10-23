package com.epam.kkorolkov.finalproject.filter;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.BookDao;
import com.epam.kkorolkov.finalproject.db.dao.LanguageDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Book;
import com.epam.kkorolkov.finalproject.db.entity.Language;
import com.epam.kkorolkov.finalproject.exception.DBConnectionException;
import com.epam.kkorolkov.finalproject.exception.DBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@WebFilter(filterName = "SetSessionFilter")
public class SetSessionFilter implements Filter {
    private static final Logger LOGGER = LogManager.getLogger("SESSION");

    public void destroy() {
    }

    @SuppressWarnings("unchecked")
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) req;
        HttpSession session = httpServletRequest.getSession();
        Map<Integer, Language> languages = (Map<Integer, Language>) session.getAttribute("languages");
        if (languages == null) {
            session.setAttribute("languages", getLanguages());
            LOGGER.info("Session attribute 'languages' set.");
        }
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) {
            session.setAttribute("cart", new HashMap<>());
        }
        chain.doFilter(req, resp);
    }

    private Map<Integer, Language> getLanguages() {
        DataSource dataSource = null;
        Connection connection = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            LanguageDao languageDao = AbstractDaoFactory.getInstance().getLanguageDao();
            return languageDao.getAll(connection);
        } catch (DBException e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
        return null;
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
