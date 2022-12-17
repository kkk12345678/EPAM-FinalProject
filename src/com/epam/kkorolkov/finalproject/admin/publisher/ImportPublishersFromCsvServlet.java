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
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code ImportPublishersFromCsvServlet} is a servlet which task is to insert
 * into the table <i>publishers</i> data which is contained in a *.csv file.
 *
 * {@code doPost} method is overridden.
 */
@WebServlet("/admin/import-publishers")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class ImportPublishersFromCsvServlet extends HttpServlet {

    /** Page to redirect after successful import */
    private static final String REDIRECT_SUCCESS = "/admin/publishers";

    /** Page to redirect after exception is thrown */
    private static final String REDIRECT_ERROR_CONNECTION =
            "/error?code=500&message=Unable to connect to the database. Try again later.";
    private static final String REDIRECT_ERROR_DAO =
            "/error?code=500&message=Cannot instantiate DAO. See server logs for details.";
    private static final String REDIRECT_ERROR_DB =
            "/error?code=500&message=Database error occurred. See server logs for details.";

    /** Request parameters */
    private static final String PARAM_FILE = "file";

    /**
     * Method {@code doPost} reads uploaded *.csv file line by line.
     * Data in each line is stored in an instance of {@link Publisher} and then
     * on {@link PublisherDao} the method {@link PublisherDao#insert(Connection, Publisher)} is invoked.
     *
     * @param request - {@link HttpServletRequest} object provided by Tomcat.
     * @param response - {@link HttpServletResponse} object provided by Tomcat.
     *
     * @throws ServletException is thrown if the request could not be handled.
     * @throws IOException is thrown if an input or output exception occurs.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String context = request.getServletContext().getContextPath();
        DataSource dataSource = null;
        Connection connection = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getPart(PARAM_FILE).getInputStream())))  {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            PublisherDao publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
            reader.readLine();
            while (reader.ready()) {
                String[] data = reader.readLine().split(",");
                Publisher publisher = new Publisher();
                Map<Integer, String> descriptions = new HashMap<>();
                Map<Integer, String> names = new HashMap<>();
                publisher.setTag(data[0]);
                names.put(1, data[1]);
                names.put(2, new String(data[2].getBytes(), StandardCharsets.UTF_8));
                descriptions.put(1, data[3]);
                descriptions.put(2, new String(data[4].getBytes(), StandardCharsets.UTF_8));
                publisher.setDescriptions(descriptions);
                publisher.setNames(names);
                publisherDao.insert(connection, publisher);
            }
            response.sendRedirect(context + REDIRECT_SUCCESS);
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
