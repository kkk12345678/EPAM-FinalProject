package com.epam.kkorolkov.finalproject.admin.publisher;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.PublisherDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Publisher;
import com.epam.kkorolkov.finalproject.exception.DbConnectionException;
import com.epam.kkorolkov.finalproject.exception.DbException;
import com.epam.kkorolkov.finalproject.exception.DaoException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

/**
 * The {@code GetPublishersServlet} is a servlet which task is to retrieve
 * data corresponding all rows in the table <i>publishers</i> and
 * represent them to administrator's page.
 *
 * {@code doGet} method is overridden.
 */
@WebServlet("/admin/publishers")
public class GetPublishersServlet extends HttpServlet {

    /** Page to redirect after exception is thrown */
    private static final String REDIRECT_ERROR_CONNECTION =
            "/error?code=500&message=Unable to connect to the database. Try again later.";
    private static final String REDIRECT_ERROR_DAO =
            "/error?code=500&message=Cannot instantiate DAO. See server logs for details.";
    private static final String REDIRECT_ERROR_DB =
            "/error?code=500&message=Database error occurred. See server logs for details.";

    /** JSP page to include */
    private static final String INCLUDE_JSP = "../jsp/admin/publishers/publishers.jsp";

    /** Request attributes */
    private static final String ATTR_PUBLISHERS = "publishers";

    /**
     * {@code doGet} method handles GET request. Retrieves data from
     * {@link PublisherDao#getAll(Connection)}.
     *
     * @param request {@link HttpServletRequest} object provided by Tomcat.
     * @param response {@link HttpServletResponse} object provided by Tomcat.
     *
     * @throws ServletException is thrown if the request for the GET could not be handled.
     * @throws IOException is thrown if an input or output exception occurs.
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String context = request.getServletContext().getContextPath();
        DataSource dataSource = null;
        Connection connection = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            PublisherDao publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
            if (connection != null) {
                List<Publisher> publishers = publisherDao.getAll(connection);
                request.setAttribute(ATTR_PUBLISHERS, publishers);
            }
            request.getRequestDispatcher(INCLUDE_JSP).include(request, response);
        } catch (DbConnectionException e) {
            response.sendRedirect(context + REDIRECT_ERROR_CONNECTION);
        } catch (DbException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DB);
        } catch (DaoException e) {
            response.sendRedirect(context + REDIRECT_ERROR_DAO);
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }
}
