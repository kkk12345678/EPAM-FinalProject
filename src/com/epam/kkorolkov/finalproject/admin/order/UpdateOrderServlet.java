package com.epam.kkorolkov.finalproject.admin.order;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.OrderDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.exception.DBException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/update-status")
public class UpdateOrderServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DataSource dataSource = null;
        Connection connection = null;
        try {
            int orderId = Integer.parseInt(request.getParameter("order_id"));
            int statusId = Integer.parseInt(request.getParameter("status_id"));
            String page = request.getParameter("page");
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            OrderDao orderDao = AbstractDaoFactory.getInstance().getOrderDao();
            orderDao.updateStatus(connection, orderId, statusId);
            response.sendRedirect(page);
        } catch (DBException e) {
            // TODO handle DBException
        } catch (NumberFormatException e) {
            // TODO handle NumberFormatException
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }
}
