package com.epam.kkorolkov.finalproject.admin.book;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.BookDao;
import com.epam.kkorolkov.finalproject.db.dao.CategoryDao;
import com.epam.kkorolkov.finalproject.db.dao.PublisherDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Book;
import com.epam.kkorolkov.finalproject.db.entity.Category;
import com.epam.kkorolkov.finalproject.db.entity.Publisher;
import com.epam.kkorolkov.finalproject.exception.DBException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet("/admin/books")
public class GetBooksServlet extends HttpServlet {
    private static final int LIMIT = 20;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pageParameter = request.getParameter("page");
        if (pageParameter == null || "".equals(pageParameter)) {
            response.sendRedirect("./books?page=1");
        } else {
            int page = Integer.parseInt(pageParameter);
            DataSource dataSource = null;
            Connection connection = null;
            try {
                dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
                connection = dataSource.getConnection();
                BookDao bookDao = AbstractDaoFactory.getInstance().getBookDao();
                CategoryDao categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();
                PublisherDao publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
                if (connection != null) {
                    int totalPages = (bookDao.count(connection) - 1) / LIMIT + 1;
                    if (page > totalPages) {
                        page = totalPages;
                    }
                    request.setAttribute("books", bookDao.getAll(connection, LIMIT, page));
                    request.setAttribute("categories", categoryDao.getAll(connection));
                    request.setAttribute("publishers", publisherDao.getAll(connection));
                    request.setAttribute("totalPages", totalPages);
                    request.setAttribute("currentPage", page);
                }
                request.getRequestDispatcher("../jsp/admin/books/books.jsp").include(request, response);
            } catch (DBException e) {
                // TODO handle DBException
                e.printStackTrace();
            } finally {
                if (dataSource != null) {
                    dataSource.release(connection);
                }
            }
        }
    }

    private void getContent(HttpServletRequest request, HttpServletResponse response) {

    }
}
