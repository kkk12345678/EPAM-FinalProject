package com.epam.kkorolkov.finalproject.admin.order;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.OrderDao;
import com.epam.kkorolkov.finalproject.db.dao.StatusDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.exception.BadRequestException;
import com.epam.kkorolkov.finalproject.exception.DBConnectionException;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.util.CatalogueUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

@WebServlet("/admin/orders")
public class GetOrdersServlet extends HttpServlet {
    private static final int LIMIT = 20;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pageParameter = request.getParameter("page");
        Map<String, String> parameters = CatalogueUtils.setOrderParameters(request);
        if (pageParameter == null || "".equals(pageParameter)) {
            response.sendRedirect("./orders?page=1");
            return;
        }
        DataSource dataSource = null;
        Connection connection = null;
        try {
            int page = Integer.parseInt(pageParameter);
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            OrderDao orderDao = AbstractDaoFactory.getInstance().getOrderDao();
            StatusDao statusDao = AbstractDaoFactory.getInstance().getStatusDao();
            int totalPages = (orderDao.count(connection, parameters) - 1) / LIMIT + 1;
            if (page > totalPages) {
                page = totalPages;
            }
            request.setAttribute("orders", orderDao.getAll(connection, LIMIT, LIMIT * (page - 1), parameters));
            request.setAttribute("statuses", statusDao.getAll(connection));
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("currentPage", page);

            request.getRequestDispatcher("../jsp/admin/orders/orders.jsp").include(request, response);
        } catch (DBConnectionException e) {
            // TODO handle DBConnectionException
        }catch (DBException e) {
            // TODO handle DBException
        } catch (NumberFormatException e) {
            // TODO handle BadRequestException
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }
}
