package com.epam.kkorolkov.finalproject.controller.book;

import com.epam.kkorolkov.finalproject.db.dao.*;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Book;
import com.epam.kkorolkov.finalproject.db.entity.Category;
import com.epam.kkorolkov.finalproject.db.entity.Language;
import com.epam.kkorolkov.finalproject.db.entity.Publisher;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.utils.CatalogueUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/edit-book")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class EditBookServlet extends HttpServlet {
    protected static final Logger LOGGER = LogManager.getLogger("Edit book");
    private static final String UPLOAD_DIRECTORY = "static/img/product/";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DataSource dataSource = null;
        Connection connection = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            Book book = getBook(request, connection);
            request.setAttribute("book", book);
            request.setAttribute("categories", getCategories(connection));
            request.setAttribute("publishers", getPublishers( connection));
            request.setAttribute("languages", getLanguages(connection));
        } catch (DBException e) {
            //TODO Handle DBException
            request.setAttribute("book", Book.create());
        } catch (NumberFormatException e) {
            //TODO Handle NumberFormatException
            e.printStackTrace();
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }

        request.getRequestDispatcher("../jsp/admin/books/edit-book.jsp").include(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DataSource dataSource = null;
        Connection connection = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            saveImage(request);
            Book book = setBook(request, connection);
            BookDao bookDao = AbstractDaoFactory.getInstance().getBookDao();
            if (book.getId() == 0) {
                bookDao.insert(connection, book);
            } else {
                bookDao.update(connection, book);
            }
        } catch (DBException e) {
            // TODO handle DBException
            e.printStackTrace();
        } catch (NullPointerException e) {
            // TODO handle NullPointerException
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }

        }
        response.sendRedirect("./books");
    }

    private void saveImage(HttpServletRequest request) throws IOException, ServletException {
        String isbn = request.getParameter("isbn");
        if (!CatalogueUtils.validateIsbn(isbn)) {
            throw new NumberFormatException("Incorrect ISBN.");
        }
        String file = getServletContext().getRealPath("/") + UPLOAD_DIRECTORY + isbn + ".jpg";
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream (new FileOutputStream(file))) {
            bufferedOutputStream.write(request.getPart("image").getInputStream().readAllBytes());
            bufferedOutputStream.flush();
        }
    }

    private Book setBook(HttpServletRequest request, Connection connection) throws ParseException, DBException {
        int id = Integer.parseInt(request.getParameter("id"));
        String tag = request.getParameter("tag");
        Date date = Date.valueOf(request.getParameter("date"));
        String isbn = request.getParameter("isbn");
        double price = Double.parseDouble(request.getParameter("price"));
        int quantity = Integer.parseInt(request.getParameter("price"));
        int categoryId = Integer.parseInt(request.getParameter("category"));
        int publisherId = Integer.parseInt(request.getParameter("publisher"));
        Map<Integer, Language> languages = getLanguages(connection);
        Map<Integer, String> descriptions = new HashMap<>();
        Map<Integer, String> titles = new HashMap<>();
        for (int languageId : languages.keySet()) {
            descriptions.put(languageId, request.getParameter("description" + languageId));
            titles.put(languageId, request.getParameter("name" + languageId));
        }
        Book book = new Book();
        book.setId(id);
        book.setTag(tag.toLowerCase().replaceAll("[^a-z0-9]+", "-"));
        book.setIsbn(isbn);
        book.setDate(date);
        book.setPrice(price);
        book.setQuantity(quantity);
        book.setCategory(getCategory(connection, categoryId));
        book.setPublisher(getPublisher(connection, publisherId));
        book.setTitles(titles);
        book.setDescriptions(descriptions);
        return book;
    }

    private Book getBook(HttpServletRequest request, Connection connection) throws DBException {
        Book book;
        int id = Integer.parseInt(request.getParameter("id"));
        if (id != 0) {
            BookDao bookDao = AbstractDaoFactory.getInstance().getBookDao();
            book = bookDao.get(connection, id).get();
        } else {
            book = Book.create();
        }
        return book;
    }

    private List<Category> getCategories(Connection connection) throws DBException {
        CategoryDao categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();
        return categoryDao.getAll(connection);
    }

    private Category getCategory(Connection connection, int id) throws DBException {
        CategoryDao categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();
        return categoryDao.get(connection, id).orElseThrow(DBException::new);
    }

    private Publisher getPublisher(Connection connection, int id) throws DBException {
        PublisherDao publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
        return publisherDao.get(connection, id).orElseThrow(DBException::new);
    }
    private Map<Integer, Language> getLanguages(Connection connection) throws DBException {
        LanguageDao languageDao = AbstractDaoFactory.getInstance().getLanguageDao();
        return languageDao.getAll(connection);
    }

    private List<Publisher> getPublishers(Connection connection) throws DBException {
        PublisherDao publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
        return publisherDao.getAll(connection);
    }
}
