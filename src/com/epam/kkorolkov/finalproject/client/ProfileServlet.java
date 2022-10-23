package com.epam.kkorolkov.finalproject.client;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.OrderDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.User;
import com.epam.kkorolkov.finalproject.exception.DBConnectionException;
import com.epam.kkorolkov.finalproject.exception.DBException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private static final int LIMIT = 20;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        String pageParameter = request.getParameter("page");
        if (pageParameter == null || "".equals(pageParameter)) {
            response.sendRedirect("./profile?page=1");
            return;
        }
        if (user == null) {
            response.sendRedirect("./shop?page=1");
            return;
        }
        DataSource dataSource = null;
        Connection connection = null;
        try {
            int page = Integer.parseInt(pageParameter);
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            OrderDao orderDao = AbstractDaoFactory.getInstance().getOrderDao();
            request.setAttribute("orders", orderDao.getAllByUser(connection, LIMIT, LIMIT * (page - 1), user.getId()));
            Map<String, String> parameters = new HashMap<>();
            parameters.put("user_id", Integer.toString(user.getId()));
            int totalPages = (orderDao.count(connection, parameters) - 1) / LIMIT + 1;
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("currentPage", page);
            request.getRequestDispatcher("./jsp/client/profile.jsp").include(request, response);
        } catch (DBConnectionException e) {
            //TODO handle DBConnectionException
        } catch (DBException e) {
            //TODO handle DBException
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }
}
