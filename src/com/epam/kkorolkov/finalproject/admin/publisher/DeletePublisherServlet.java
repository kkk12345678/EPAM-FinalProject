package com.epam.kkorolkov.finalproject.admin.publisher;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.PublisherDao;
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

@WebServlet("/admin/delete-publisher")
public class DeletePublisherServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Connection connection = null;
        DataSource dataSource = null;
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            if (connection != null) {
                PublisherDao publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
                publisherDao.delete(connection, id);
            }
        } catch (DBException e) {
            e.printStackTrace();
            //TODO handle exception
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
        response.sendRedirect("publishers");
    }
}