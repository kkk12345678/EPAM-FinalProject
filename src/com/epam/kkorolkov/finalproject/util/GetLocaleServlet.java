package com.epam.kkorolkov.finalproject.util;

import com.epam.kkorolkov.finalproject.db.dao.*;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Language;
import com.epam.kkorolkov.finalproject.exception.DBConnectionException;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.exception.DaoException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Optional;

@WebServlet("/locale")
public class GetLocaleServlet extends HttpServlet {
    /* Page to redirect after exception is thrown */
    private static final String REDIRECT_ERROR_CONNECTION =
            "/error?code=500&message=Unable to connect to the database. Try again later.";
    private static final String REDIRECT_ERROR_DAO =
            "/error?code=500&message=Cannot instantiate DAO. See server logs for details.";
    private static final String REDIRECT_ERROR_DB =
            "/error?code=500&message=Database error occurred. See server logs for details.";


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String context = request.getServletContext().getContextPath();
        HttpSession session = request.getSession(false);
        try (PrintWriter writer = response.getWriter()) {
            if (session == null) {
                writer.print(1);
            } else {
                String locale = (String) session.getAttribute("locale");
                if (locale == null || "".equals(locale)) {
                    writer.print(1);
                } else {
                    DataSource dataSource = null;
                    Connection connection = null;
                    try {
                        dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
                        connection = dataSource.getConnection();
                        LanguageDao languageDao = AbstractDaoFactory.getInstance().getLanguageDao();
                        if (connection != null) {
                            Optional<Language> optional = languageDao.getByLocale(connection, locale);
                            if (optional.isEmpty()) {
                                writer.print(1);
                            } else {
                                writer.print(optional.get().getId());
                            }
                        }
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
            }
        }
    }
}
