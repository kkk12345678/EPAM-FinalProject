package com.epam.kkorolkov.finalproject.admin.book;

import com.epam.kkorolkov.finalproject.db.dao.*;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Book;
import com.epam.kkorolkov.finalproject.db.entity.Category;
import com.epam.kkorolkov.finalproject.db.entity.Language;
import com.epam.kkorolkov.finalproject.db.entity.Publisher;
import com.epam.kkorolkov.finalproject.exception.BadRequestException;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.util.CatalogueUtils;
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
import java.util.NoSuchElementException;
import java.util.function.Supplier;

@WebServlet("/admin/edit-book")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class EditBookServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger("EDIT BOOK");
    private static final String MESSAGE_ERROR_ID = "ID is not a valid integer.";

    private static final String REGEX_TAG = "[^a-z0-9]+";
    private static final String REPLACE_TAG = "-";

    private static final String UPLOAD_DIRECTORY = "static/img/product/";

    private static final String ATTR_BOOK = "book";
    private static final String ATTR_CATEGORIES = "categories";
    private static final String ATTR_PUBLISHERS = "publishers";
    private static final String ATTR_LANGUAGES = "languages";
    private static final String ATTR_TODAY = "today";

    private static final String INCLUDE_JSP = "../jsp/admin/books/edit-book.jsp";
    private static final String REDIRECT_PAGE = "./books";

    private static final String PARAM_ISBN = "isbn";
    private static final String PARAM_ID = "id";
    private static final String PARAM_TAG = "tag";
    private static final String PARAM_CATEGORY = "category";
    private static final String PARAM_PUBLISHER = "publisher";

    private static final String PARAM_DATE = "date";
    private static final String PARAM_QUANTITY = "quantity";
    private static final String PARAM_PRICE = "price";
    private static final String PARAM_NAME = "name";
    private static final String PARAM_DESCRIPTION = "description";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DataSource dataSource = null;
        Connection connection = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            Book book = getBook(request, connection);
            request.setAttribute(ATTR_BOOK, book);
            request.setAttribute(ATTR_CATEGORIES, getCategories(connection));
            request.setAttribute(ATTR_PUBLISHERS, getPublishers( connection));
            request.setAttribute(ATTR_LANGUAGES, getLanguages(connection));
            request.setAttribute(ATTR_TODAY, new Date(System.currentTimeMillis()));
            request.getRequestDispatcher(INCLUDE_JSP).include(request, response);
        } catch (DBException e) {
            //TODO handle DBException
        } catch (BadRequestException e) {
            //TODO handle BadRequestException
        }finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
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
            response.sendRedirect(REDIRECT_PAGE);
        } catch (DBException e) {
            // TODO handle DBException
        } catch (NullPointerException e) {
            // TODO handle NullPointerException
        } catch (NumberFormatException e) {
            // TODO handle NumberFormatException
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }

    private void saveImage(HttpServletRequest request) throws IOException, ServletException {
        if (request.getPart("image").getSize() == 0) {
            return;
        }
        String isbn = request.getParameter(PARAM_ISBN);
        if (!CatalogueUtils.validateIsbn(isbn)) {
            throw new NumberFormatException("Incorrect ISBN.");
        }
        String file = getServletContext().getRealPath("/") + UPLOAD_DIRECTORY + isbn + ".jpg";
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream (new FileOutputStream(file))) {
            bufferedOutputStream.write(request.getPart("image").getInputStream().readAllBytes());
            bufferedOutputStream.flush();
        }
    }

    private Book setBook(HttpServletRequest request, Connection connection) throws DBException, NumberFormatException {
        int id = Integer.parseInt(request.getParameter(PARAM_ID));
        String tag = request.getParameter(PARAM_TAG);
        Date date = Date.valueOf(request.getParameter(PARAM_DATE));
        String isbn = request.getParameter(PARAM_ISBN);
        double price = Double.parseDouble(request.getParameter(PARAM_PRICE));
        int quantity = Integer.parseInt(request.getParameter(PARAM_QUANTITY));
        int categoryId = Integer.parseInt(request.getParameter(PARAM_CATEGORY));
        int publisherId = Integer.parseInt(request.getParameter(PARAM_PUBLISHER));
        Map<Integer, Language> languages = getLanguages(connection);
        Map<Integer, String> descriptions = new HashMap<>();
        Map<Integer, String> titles = new HashMap<>();
        for (int languageId : languages.keySet()) {
            descriptions.put(languageId, request.getParameter(PARAM_DESCRIPTION + languageId));
            titles.put(languageId, request.getParameter(PARAM_NAME + languageId));
        }
        Book book = new Book();
        book.setId(id);
        book.setTag(tag.toLowerCase().replaceAll(REGEX_TAG, REPLACE_TAG));
        book.setIsbn(isbn);
        book.setDate(date);
        book.setPrice(price);
        book.setQuantity(quantity);
        book.setCategory(getCategory(connection, categoryId));
        book.setPublisher(getPublisher(connection, publisherId));
        book.setNames(titles);
        book.setDescriptions(descriptions);
        return book;
    }

    private Book getBook(HttpServletRequest request, Connection connection) throws DBException, BadRequestException {
        try {
            int id = Integer.parseInt(request.getParameter(PARAM_ID));
            if (id != 0) {
                BookDao bookDao = AbstractDaoFactory.getInstance().getBookDao();
                return bookDao.get(connection, id).orElseThrow(NoSuchElementException::new);
            }
            return Book.create();
        } catch (NumberFormatException | NoSuchElementException e) {
            LOGGER.info(MESSAGE_ERROR_ID);
            LOGGER.error(e.getMessage());
            throw new BadRequestException();
        }
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
