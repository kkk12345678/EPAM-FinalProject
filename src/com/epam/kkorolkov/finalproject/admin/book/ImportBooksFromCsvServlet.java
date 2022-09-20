package com.epam.kkorolkov.finalproject.admin.book;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.BookDao;
import com.epam.kkorolkov.finalproject.db.dao.CategoryDao;
import com.epam.kkorolkov.finalproject.db.dao.PublisherDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Book;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/admin/import-books")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class ImportBooksFromCsvServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DataSource dataSource = null;
        Connection connection = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getPart("file").getInputStream()))) {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            BookDao bookDao = AbstractDaoFactory.getInstance().getBookDao();
            CategoryDao categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();
            PublisherDao publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
            reader.readLine();
            while (reader.ready()) {
                String[] data = reader.readLine().split(",");
                Book book = new Book();
                Map<Integer, String> descriptions = new HashMap<>();
                Map<Integer, String> titles = new HashMap<>();
                descriptions.put(1, new String(data[8].getBytes(), StandardCharsets.UTF_8));
                descriptions.put(2, new String(data[10].getBytes(), StandardCharsets.UTF_8));
                titles.put(1, new String(data[7].getBytes(), StandardCharsets.UTF_8));
                titles.put(2, new String(data[9].getBytes(), StandardCharsets.UTF_8));
                book.setTag(data[0]);
                book.setIsbn(data[1]);
                book.setDate(Date.valueOf(data[2]));
                book.setPublisher(publisherDao.get(connection, Integer.parseInt(data[3])).get());
                book.setCategory(categoryDao.get(connection, Integer.parseInt(data[4])).get());
                book.setQuantity(Integer.parseInt(data[5]));
                book.setPrice(Double.parseDouble(data[6]));
                book.setDescriptions(descriptions);
                book.setTitles(titles);
                bookDao.insert(connection, book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
        response.sendRedirect("books");
    }
}
