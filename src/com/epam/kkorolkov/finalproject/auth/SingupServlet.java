package com.epam.kkorolkov.finalproject.auth;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.UserDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.User;
import com.epam.kkorolkov.finalproject.exception.DBException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.Optional;

@WebServlet("/signup")
public class SingupServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = new User();
        String email = request.getParameter("email");
        user.setFirstName(request.getParameter("firstname"));
        user.setLastName(request.getParameter("lastname"));
        user.setEmail(email);
        user.setPassword(request.getParameter("password"));
        user.setBlocked(false);
        user.setAdmin(false);
        Connection connection = null;
        DataSource dataSource = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            UserDao userDao = AbstractDaoFactory.getInstance().getUserDao();
            if (connection != null) {
                Optional<User> optional = userDao.get(connection, email);
                if (optional.isPresent()) {
                    response.sendRedirect(request.getServletContext().getContextPath() + "/error?code=400&message=User with email " + email + " already exists.");
                } else {
                    user.setId(userDao.insert(connection, user));
                    request.getSession().setAttribute("user", user);
                    response.sendRedirect(request.getServletContext().getContextPath() + "/shop");
                }
            }
        } catch (DBException e) {
            // TODO handle DBException
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("./jsp/auth/signup.jsp").include(request, response);
    }
}
