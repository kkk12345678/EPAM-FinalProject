package com.epam.kkorolkov.finalproject.admin.user;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.UserDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.User;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.util.UserUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/admin/add-user")
public class AddUserServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstName = request.getParameter("firstname");
        String lastName = request.getParameter("lastname");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        // TODO validation
        User user = new User();
        user.setEmail(email);
        user.setPassword(UserUtils.hash(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAdmin(false);
        user.setBlocked(false);
        Connection connection = null;
        DataSource dataSource = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            if (connection != null) {
                UserDao userDao = AbstractDaoFactory.getInstance().getUserDao();
                userDao.insert(connection, user);
            }
        } catch (DBException e) {
            //TODO handle exception
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
        response.sendRedirect("users");
    }
}
