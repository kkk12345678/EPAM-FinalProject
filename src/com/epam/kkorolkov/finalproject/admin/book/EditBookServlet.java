package com.epam.kkorolkov.finalproject.admin.book;

import com.epam.kkorolkov.finalproject.db.dao.*;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Book;
import com.epam.kkorolkov.finalproject.db.entity.Category;
import com.epam.kkorolkov.finalproject.db.entity.Language;
import com.epam.kkorolkov.finalproject.db.entity.Publisher;
import com.epam.kkorolkov.finalproject.exception.*;
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
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * The {@code EditBookServlet} is a servlet which task is to
 * edit product if it exists in a database or to create one otherwise.
 *
 * {@code doGet} and {@code doPost} methods are overridden.
 */
@WebServlet("/admin/edit-book")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class EditBookServlet extends HttpServlet {
    /** Logger */
    private static final Logger LOGGER = LogManager.getLogger("EDIT BOOK");

    /** Page to redirect after success */
    private static final String REDIRECT_SUCCESS = "/admin/books";

    /** Page to redirect after exception is thrown */
    private static final String REDIRECT_ERROR_CONNECTION =
            "/error?code=500&message=Unable to connect to the database. Try again later.";
    private static final String REDIRECT_ERROR_DAO =
            "/error?code=500&message=Cannot instantiate DAO. See server logs for details.";
    private static final String REDIRECT_ERROR_DB =
            "/error?code=500&message=Database error occurred. See server logs for details.";
    private static final String REDIRECT_ERROR_REQUEST =
            "/error?code=400&message=Incorrect ID parameter. Is not a valid integer.";
    private static final String REDIRECT_ERROR_VALIDATION =
            "/error?code=400&message=";
    private static final String REDIRECT_ERROR_PARAMS =
            "/error?code=400&message=Some POST parameters are incorrect. See server logs for details.";

    /** Logger messages */
    private static final String MESSAGE_ERROR_ID = "ID is not a valid integer.";
    private static final String MESSAGE_ERROR_PARAMS = "Some POST parameters are incorrect.";
    private static final String MESSAGE_ERROR_EMPTY = "No product found with specified ID.";

    /** Directory where uploaded images are stored */
    private static final String UPLOAD_DIRECTORY = "static/img/product/";

    /** Extension for uploaded images*/
    private static final String IMAGE_EXTENSION = ".jpg";

    /** Request attributes */
    private static final String ATTR_BOOK = "book";
    private static final String ATTR_CATEGORIES = "categories";
    private static final String ATTR_PUBLISHERS = "publishers";
    private static final String ATTR_LANGUAGES = "languages";
    private static final String ATTR_TODAY = "today";

    /** JSP page to include */
    private static final String INCLUDE_JSP = "../jsp/admin/books/edit-book.jsp";

    /** Request parameters */
    private static final String PARAM_ISBN = "isbn";
    private static final String PARAM_IMAGE = "image";
    private static final String PARAM_ID = "id";
    private static final String PARAM_CATEGORY = "category";
    private static final String PARAM_PUBLISHER = "publisher";
    private static final String PARAM_DATE = "date";
    private static final String PARAM_QUANTITY = "quantity";
    private static final String PARAM_PRICE = "price";

    /**
     * {@code doGet} method handles GET request.
     * Represents to an administrator a page with a form to edit or create a book.
     *
     * @param request {@link HttpServletRequest} object provided by Tomcat.
     * @param response {@link HttpServletResponse} object provided by Tomcat.
     *
     * @throws ServletException is thrown if the request for the GET could not be handled.
     * @throws IOException is thrown if an input or output exception occurs.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String context = request.getServletContext().getContextPath();
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
        } catch (DbConnectionException e) {
            response.sendRedirect(context + REDIRECT_ERROR_CONNECTION);
        } catch (DbException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DB);
        } catch (DaoException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DAO);
        } catch (BadRequestException e) {
            response.sendRedirect(context + REDIRECT_ERROR_REQUEST);
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }

    /**
     * {@code doPost} method handles POST request. Creates or updates
     * book. Field values are retrieved from request parameters.
     *
     * @param request {@link HttpServletRequest} object provided by Tomcat.
     * @param response {@link HttpServletResponse} object provided by Tomcat.
     *
     * @throws ServletException is thrown if the request for the POST could not be handled.
     * @throws IOException is thrown if an input or output exception occurs.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String context = request.getServletContext().getContextPath();
        DataSource dataSource = null;
        Connection connection = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            Book book = setBook(request, connection);
            CatalogueUtils.validate(book);
            saveImage(request);
            BookDao bookDao = AbstractDaoFactory.getInstance().getBookDao();
            if (book.getId() == 0) {
                bookDao.insert(connection, book);
            } else {
                bookDao.update(connection, book);
            }
            response.sendRedirect(context + REDIRECT_SUCCESS);
        } catch (DbConnectionException e) {
            response.sendRedirect(context + REDIRECT_ERROR_CONNECTION);
        } catch (DbException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DB);
        } catch (DaoException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DAO);
        } catch (BadRequestException e) {
            response.sendRedirect(context + REDIRECT_ERROR_PARAMS);
        } catch (ValidationException e) {
            response.sendRedirect(context + REDIRECT_ERROR_VALIDATION + e.getMessage());
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }

    /**
     * {@code saveImage} method saves image file retrieved from the request to disc drive.
     *
     * @param request {@link HttpServletRequest} object provided by Tomcat.
     *
     * @throws ServletException is thrown if the image cannot be retrieved from the request.
     * @throws IOException is thrown if an input or output exception occurs.
     */
    private void saveImage(HttpServletRequest request) throws IOException, ServletException {
        if (request.getPart(PARAM_IMAGE).getSize() == 0) {
            return;
        }
        String isbn = request.getParameter(PARAM_ISBN);
        String file = getServletContext().getRealPath("/") + UPLOAD_DIRECTORY + isbn + IMAGE_EXTENSION;
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream (new FileOutputStream(file))) {
            bufferedOutputStream.write(request.getPart(PARAM_IMAGE).getInputStream().readAllBytes());
            bufferedOutputStream.flush();
        }
    }

    /**
     * {@code setBook} method creates an instance of {@link Book} class with
     * field values retrieved from the request parameters.
     *
     * @param request {@link HttpServletRequest} object provided by Tomcat.
     * @param connection an instance of {@link Connection} to reach the database.
     *
     * @return {@code Book} instance.
     *
     * @throws DbException is thrown if query cannot be executed.
     * @throws DaoException is thrown if DAO instance cannot be instantiated.
     * @throws BadRequestException is thrown if request parameters are invalid.
     */
    private Book setBook(HttpServletRequest request, Connection connection) throws DbException, DaoException, BadRequestException {
        try {
            Date date = Date.valueOf(request.getParameter(PARAM_DATE));
            String isbn = request.getParameter(PARAM_ISBN);
            double price = Double.parseDouble(request.getParameter(PARAM_PRICE));
            int quantity = Integer.parseInt(request.getParameter(PARAM_QUANTITY));
            int categoryId = Integer.parseInt(request.getParameter(PARAM_CATEGORY));
            int publisherId = Integer.parseInt(request.getParameter(PARAM_PUBLISHER));
            Book book = new Book();
            CatalogueUtils.setDetailsFromRequest(request, book, getLanguages(connection));
            book.setIsbn(isbn);
            book.setDate(date);
            book.setPrice(price);
            book.setQuantity(quantity);
            book.setCategory(getCategory(connection, categoryId));
            book.setPublisher(getPublisher(connection, publisherId));
            return book;
        } catch (NumberFormatException e) {
            LOGGER.info(MESSAGE_ERROR_PARAMS);
            LOGGER.error(e.getMessage());
            throw new BadRequestException();
        }
    }

    /**
     * {@code getBook} method prepares instance of {@link Book}.
     * If there is a book in the database with {@code id} provided in request parameter
     * returns the corresponding instance
     * Otherwise returns book stub provided by {@link Book#create()} method
     *
     * @param request {@link HttpServletRequest} object provided by Tomcat.
     * @param connection {@link Connection} object to process query.
     *
     * @return instance of {@link Book}.
     *
     * @throws DbException is thrown if there {@link java.sql.SQLException} is thrown while querying database.
     * @throws BadRequestException is thrown if parameter {@code id} is not valid id.
     * @throws DaoException is thrown if an instance of DAO cannot be instantiated.
     *
     * @see Book#create()
     */
    private Book getBook(HttpServletRequest request, Connection connection) throws DbException, BadRequestException, DaoException {
        try {
            int id = Integer.parseInt(request.getParameter(PARAM_ID));
            if (id != 0) {
                BookDao bookDao = AbstractDaoFactory.getInstance().getBookDao();
                return bookDao.get(connection, id).orElseThrow(() -> new NoSuchElementException(MESSAGE_ERROR_EMPTY));
            }
            return Book.create();
        } catch (NumberFormatException | NoSuchElementException e) {
            LOGGER.info(MESSAGE_ERROR_ID);
            LOGGER.error(e.getMessage());
            throw new BadRequestException();
        }
    }

    /**
     * @return {@link List} containing all records in the table <i>categories</i>.
     *
     * @param connection an instance of {@link Connection}
     *                   which provides ability to connect to the database.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     * @throws DaoException is thrown if DAO cannot be instantiated.
     */
    private List<Category> getCategories(Connection connection) throws DbException, DaoException {
        CategoryDao categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();
        return categoryDao.getAll(connection);
    }

    /**
     * @return an instance of {@link Category} containing a record
     * in the table <i>categories</i> with the specified id.
     *
     * @param id id of a category which data is to be retrieved.
     * @param connection an instance of {@link Connection}
     *                   which provides ability to connect to the database.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     * @throws DaoException is thrown if DAO cannot be instantiated.
     */
    private Category getCategory(Connection connection, int id) throws DbException, DaoException {
        CategoryDao categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();
        return categoryDao.get(connection, id).orElseThrow(DbException::new);
    }

    /**
     * @return an instance of {@link Publisher} containing a record
     * in the table <i>publishers</i> with the specified id.
     *
     * @param id id of a publisher which data is to be retrieved.
     * @param connection an instance of {@link Connection}
     *                   which provides ability to connect to the database.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     * @throws DaoException is thrown if DAO cannot be instantiated.
     */
    private Publisher getPublisher(Connection connection, int id) throws DbException, DaoException {
        PublisherDao publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
        return publisherDao.get(connection, id).orElseThrow(DbException::new);
    }

    /**
     * @return {@link List} containing all records in the table <i>publishers</i>.
     *
     * @param connection an instance of {@link Connection}
     *                   which provides ability to connect to the database.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     * @throws DaoException is thrown if DAO cannot be instantiated.
     */
    private List<Publisher> getPublishers(Connection connection) throws DbException, DaoException {
        PublisherDao publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
        return publisherDao.getAll(connection);
    }

    /**
     * @return {@link Map} containing all records in the table <i>languages</i>.
     *
     * @param connection an instance of {@link Connection}
     *                   which provides ability to connect to the database.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     * @throws DaoException is thrown if DAO cannot be instantiated.
     */
    private Map<Integer, Language> getLanguages(Connection connection) throws DaoException, DbException {
        LanguageDao languageDao = AbstractDaoFactory.getInstance().getLanguageDao();
        return languageDao.getAll(connection);
    }
}