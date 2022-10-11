package com.epam.kkorolkov.finalproject.admin.book;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.BookDao;
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

@WebServlet("/admin/delete-book")
public class DeleteBookServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection connection = null;
        DataSource dataSource = null;
        String page = request.getParameter("page");
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            if (connection != null) {
                BookDao bookDao = AbstractDaoFactory.getInstance().getBookDao();
                bookDao.delete(connection, id);
            }
        } catch (DBException e) {
            //TODO handle exception
        } catch (NumberFormatException e) {
            //TODO Handle NumberFormatException
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
        System.out.println("redirection to books?page=" + page);
        response.sendRedirect("books?page=" + page);
    }
}
