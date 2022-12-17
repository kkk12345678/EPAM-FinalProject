package com.epam.kkorolkov.finalproject.admin.publisher;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.LanguageDao;
import com.epam.kkorolkov.finalproject.db.dao.PublisherDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Language;
import com.epam.kkorolkov.finalproject.db.entity.Publisher;
import com.epam.kkorolkov.finalproject.exception.*;
import com.epam.kkorolkov.finalproject.util.CatalogueUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;

/**
 * The {@code EditPublisherServlet} is a servlet which task is to
 * edit a record in the table <i>publishers</i> if it exists or to create one otherwise.
 *
 * {@code doGet} and {@code doPost} methods are overridden.
 */
@WebServlet("/admin/edit-publisher")
public class EditPublisherServlet extends HttpServlet {
    protected static final Logger LOGGER = LogManager.getLogger("EDIT PUBLISHER");

    /** Page to redirect after successful edition or creation */
    private static final String REDIRECT_SUCCESS = "/admin/publishers";

    /** Page to redirect after exception is thrown */
    private static final String REDIRECT_ERROR_CONNECTION =
            "/error?code=500&message=Unable to connect to the database. Try again later.";
    private static final String REDIRECT_ERROR_DAO =
            "/error?code=500&message=Cannot instantiate DAO. See server logs for details.";
    private static final String REDIRECT_ERROR_DB =
            "/error?code=500&message=Database error occurred. See server logs for details.";
    private static final String REDIRECT_ERROR_VALIDATION =
            "/error?code=400&message=Publisher data are invalid. See server logs for details.";
    private static final String REDIRECT_ERROR_ID =
            "/error?code=400&message=ID parameter is incorrect. See server logs for details.";

    /** Logger messages */
    private static final String MESSAGE_ERROR_ID = "ID is not a valid integer.";

    /** Request attributes */
    private static final String ATTR_PUBLISHER = "publisher";
    private static final String ATTR_LANGUAGES = "languages";

    /** Request parameters */
    private static final String PARAM_ID = "id";

    /** JSP page to include */
    private static final String INCLUDE_JSP = "../jsp/admin/publishers/edit-publisher.jsp";

    /**
     * {@code doPost} method handles POST request. Creates or updates
     * a publisher. Field values are retrieved from request parameters.
     *
     * @param request - {@link HttpServletRequest} object provided by Tomcat.
     * @param response - {@link HttpServletResponse} object provided by Tomcat.
     *
     * @throws IOException is thrown if an input or output exception occurs.
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String context = request.getServletContext().getContextPath();
        DataSource dataSource = null;
        Connection connection = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            Publisher publisher = new Publisher();
            CatalogueUtils.setDetailsFromRequest(request, publisher, getLanguages(connection));
            CatalogueUtils.validate(publisher);
            PublisherDao publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
            if (publisher.getId() == 0) {
                publisherDao.insert(connection, publisher);
            } else {
                publisherDao.update(connection, publisher);
            }
            response.sendRedirect(context + REDIRECT_SUCCESS);
        } catch (DbConnectionException e) {
            response.sendRedirect(context + REDIRECT_ERROR_CONNECTION);
        } catch (DbException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DB);
        } catch (DaoException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DAO);
        } catch (ValidationException e) {
            response.sendRedirect(context + REDIRECT_ERROR_VALIDATION);
        } catch (BadRequestException e) {
            response.sendRedirect(context + REDIRECT_ERROR_ID);
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }

    /**
     * {@code doGet} method handles GET request.
     *
     * @param request - {@link HttpServletRequest} object provided by Tomcat.
     * @param response - {@link HttpServletResponse} object provided by Tomcat.
     *
     * @throws ServletException is thrown if the request for the GET could not be handled.
     * @throws IOException is thrown if an input or output exception occurs.
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String context = request.getServletContext().getContextPath();
        DataSource dataSource = null;
        Connection connection = null;
        try {
            int id = Integer.parseInt(request.getParameter(PARAM_ID));
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            if (id != 0) {
                PublisherDao publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
                Publisher publisher = publisherDao.get(connection, id).orElseThrow(NoSuchElementException::new);
                request.setAttribute(ATTR_PUBLISHER, publisher);
            }
            request.setAttribute(ATTR_LANGUAGES, getLanguages(connection));
            request.getRequestDispatcher(INCLUDE_JSP).include(request, response);
        } catch (DbConnectionException e) {
            response.sendRedirect(context + REDIRECT_ERROR_CONNECTION);
        } catch (DbException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DB);
        } catch (DaoException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DAO);
        } catch (NumberFormatException | NoSuchElementException e) {
            LOGGER.info(MESSAGE_ERROR_ID);
            LOGGER.error(e.getMessage());
            response.sendRedirect(context + REDIRECT_ERROR_ID);
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }

    /**
     * @return {@link Map} containing all records in the table <i>languages</i>.
     *
     * @param connection - an instance of {@link Connection}
     *                   which provides ability to connect to the database.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     * @throws DaoException is thrown if DAO cannot be instantiated.
     */
    private Map<Integer, Language> getLanguages(Connection connection) throws DbException, DaoException {
        LanguageDao languageDao = AbstractDaoFactory.getInstance().getLanguageDao();
        return languageDao.getAll(connection);
    }
}
