package com.epam.kkorolkov.finalproject.admin;

import com.epam.kkorolkov.finalproject.db.dao.*;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.exception.BadRequestException;
import com.epam.kkorolkov.finalproject.exception.DBException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection connection = null;
        DataSource dataSource = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            UserDao userDao = AbstractDaoFactory.getInstance().getUserDao();
            CategoryDao categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();
            PublisherDao publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
            OrderDao orderDao = AbstractDaoFactory.getInstance().getOrderDao();
            BookDao bookDao = AbstractDaoFactory.getInstance().getBookDao();
            if (connection != null) {
                request.setAttribute("usersCount", userDao.count(connection));
                request.setAttribute("categoriesCount", categoryDao.count(connection));
                request.setAttribute("publishersCount", publisherDao.count(connection));
                request.setAttribute("booksCount", bookDao.count(connection, null));
                request.setAttribute("ordersCount", orderDao.count(connection, null));
                request.setAttribute("user", request.getSession().getAttribute("user"));
            }
            request.getRequestDispatcher("./jsp/admin/welcome.jsp").include(request, response);
        } catch (DBException e) {
            // TODO handle DBException
        } catch (BadRequestException e) {
            // TODO handle BadRequestException
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }
}
