package com.epam.kkorolkov.finalproject.client;

import com.epam.kkorolkov.finalproject.db.dao.*;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.User;
import com.epam.kkorolkov.finalproject.exception.DBException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/shop")
public class ShopFrontServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null && user.getIsAdmin()) {
            response.sendRedirect("./admin");
        }
        DataSource dataSource = null;
        Connection connection = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            LanguageDao languageDao = AbstractDaoFactory.getInstance().getLanguageDao();
            if (connection != null) {
                request.setAttribute("languages", languageDao.getAll(connection));
            }
        } catch (DBException e) {
            // TODO handle DBException
            e.printStackTrace();
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
        request.getRequestDispatcher( "./jsp/user/products.jsp").include(request, response);
    }
}
