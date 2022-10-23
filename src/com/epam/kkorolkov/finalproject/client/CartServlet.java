package com.epam.kkorolkov.finalproject.client;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.BookDao;
import com.epam.kkorolkov.finalproject.db.dao.LanguageDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Book;
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
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        try {
            if (action == null) {
                prepareRequest(request, cart);
                request.getRequestDispatcher("./jsp/client/cart.jsp").include(request, response);
                return;
            }
            int id = Integer.parseInt(request.getParameter("id"));
            if ("add".equals(action)) {
                if (cart == null) {
                    cart = new HashMap<>();
                }
                int quantity = Integer.parseInt(request.getParameter("quantity"));
                cart.put(id, cart.containsKey(id) ? quantity + cart.get(id) : quantity);
                session.setAttribute("cart", cart);
                try (PrintWriter printWriter = response.getWriter()) {
                    printWriter.print(cart.keySet().size());
                }
                return;
            }
            if ("delete".equals(action)) {
                cart.remove(id);
            }
            if ("increase".equals(action)) {
                cart.put(id, cart.get(id) + 1);
            }
            if ("decrease".equals(action)) {
                int n = cart.get(id) - 1;
                if (n > 0) {
                    cart.put(id, n);
                } else {
                    cart.remove(id);
                }
            }
            if (cart.keySet().size() > 0) {
                session.setAttribute("cart", cart);
            } else {
                session.removeAttribute("cart");
            }
            response.sendRedirect(request.getServletContext().getContextPath() + "/cart");
        } catch (IOException e) {
            //TODO handle exception
        } catch (NullPointerException e) {
            //TODO handle BadRequestException
        }
    }

    private void prepareRequest(HttpServletRequest request, Map<Integer, Integer> cart) {
        Map<Book, Integer> detailedCart = new HashMap<>();
        DataSource dataSource = null;
        Connection connection = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            BookDao bookDao = AbstractDaoFactory.getInstance().getBookDao();
            //LanguageDao languageDao = AbstractDaoFactory.getInstance().getLanguageDao();
            Optional<Book> optional;
            for (int id : cart.keySet()) {
                optional = bookDao.get(connection, id);
                if (optional.isPresent()) {
                    detailedCart.put(optional.get(), cart.get(id));
                } else {
                    throw new IllegalArgumentException();
                }
            }
            request.setAttribute("cart", detailedCart);
            //request.setAttribute("languages", languageDao.getAll(connection));
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