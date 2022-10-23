package com.epam.kkorolkov.finalproject.client;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.BookDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Book;
import com.epam.kkorolkov.finalproject.exception.BadRequestException;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.util.CatalogueUtils;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

@WebServlet("/load-books")
public class BooksLazyLoadingServlet extends HttpServlet {
    private static final int LIMIT = 20;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int page = Integer.parseInt(request.getParameter("page"));
        Map<String, String> parameters = CatalogueUtils.setBookParameters(request);
        DataSource dataSource = null;
        Connection connection = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            BookDao bookDao = AbstractDaoFactory.getInstance().getBookDao();
            if (connection != null) {
                List<Book> books = bookDao.getAll(connection, LIMIT, LIMIT * (page - 1), parameters);
                String json = new Gson().toJson(books);
                try (PrintWriter writer = response.getWriter()) {
                    writer.println(json);
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
}
