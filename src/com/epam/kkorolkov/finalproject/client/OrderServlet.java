package com.epam.kkorolkov.finalproject.client;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.BookDao;
import com.epam.kkorolkov.finalproject.db.dao.LanguageDao;
import com.epam.kkorolkov.finalproject.db.dao.OrderDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Book;
import com.epam.kkorolkov.finalproject.db.entity.Order;
import com.epam.kkorolkov.finalproject.db.entity.User;
import com.epam.kkorolkov.finalproject.exception.DBConnectionException;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.util.DBUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@WebServlet("/order")
public class OrderServlet extends HttpServlet {
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DataSource dataSource = null;
        Connection connection = null;
        try {
            HttpSession session = request.getSession();
            Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
            User user = (User) session.getAttribute("user");
            double total = Double.parseDouble(request.getParameter("total"));
            if (cart != null & user != null) {
                dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
                connection = dataSource.getConnection();
                OrderDao orderDao = AbstractDaoFactory.getInstance().getOrderDao();
                int id = orderDao.insert(connection, getOrder(connection, cart, user, total));
                session.removeAttribute("cart");
                response.sendRedirect(request.getServletContext().getContextPath() + "/order?success=1&id=" + id);
            } else {
                response.sendRedirect(request.getServletContext().getContextPath() + "/error?code=500");
            }
        } catch (ClassCastException e) {
            response.sendRedirect(request.getServletContext().getContextPath() + "/error?code=500");
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

    private Order getOrder(Connection connection, Map<Integer, Integer> cart, User user, double total) throws DBException {
        Order order = new Order();
        order.setUser(user);
        order.setTotal(total);
        Map<Book, Integer> details = new HashMap<>();
        BookDao bookDao = AbstractDaoFactory.getInstance().getBookDao();
        Optional<Book> optional;
        for (int bookId : cart.keySet()) {
            optional = bookDao.get(connection, bookId);
            details.put(optional.orElseThrow(DBException::new), cart.get(bookId));
        }
        order.setDetails(details);
        return order;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if (Integer.parseInt(request.getParameter("success")) == 1) {
                request.setAttribute("id", request.getParameter("id"));
                request.getRequestDispatcher("./jsp/client/success.jsp").include(request, response);
            }
        } catch (NumberFormatException e) {
            //TODO handle NumberFormatException
        }
    }
}
