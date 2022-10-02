package com.epam.kkorolkov.finalproject.servlet;

import com.epam.kkorolkov.finalproject.db.dao.*;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Language;
import com.epam.kkorolkov.finalproject.exception.DBException;

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
                    } catch (DBException e) {
                        writer.print(1);
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
