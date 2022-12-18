package com.epam.kkorolkov.finalproject.admin.category;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.CategoryDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Category;
import com.epam.kkorolkov.finalproject.exception.DbConnectionException;
import com.epam.kkorolkov.finalproject.exception.DbException;
import com.epam.kkorolkov.finalproject.exception.DaoException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code ImportCategoriesFromCsvServlet} is a servlet which task is to insert
 * into the table <i>categories</i> data which is contained in a *.csv file.
 *
 * {@code doPost} method is overridden.
 */
@WebServlet("/admin/import-categories")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class ImportCategoriesFromCsvServlet extends HttpServlet {
    /** Page to redirect after successful import */
    private static final String REDIRECT_SUCCESS = "/admin/categories";

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
     * Data in each line is stored in an instance of {@link Category} and then
     * on {@link CategoryDao} the method {@link CategoryDao#insert(Connection, Category)} is invoked.
     *
     * @param request {@link HttpServletRequest} object provided by Tomcat.
     * @param response {@link HttpServletResponse} object provided by Tomcat.
     *
     * @throws ServletException is thrown if the request could not be handled.
     * @throws IOException is thrown if an input or output exception occurs.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String context = request.getServletContext().getContextPath();
        DataSource dataSource = null;
        Connection connection = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getPart(PARAM_FILE).getInputStream()))) {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            CategoryDao categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();
            reader.readLine();
            Category category = new Category();
            while (reader.ready()) {
                String[] data = reader.readLine().split(",");
                Map<Integer, String> descriptions = new HashMap<>();
                Map<Integer, String> names = new HashMap<>();
                category.setTag(data[0]);
                names.put(1, new String(data[1].getBytes(), StandardCharsets.UTF_8));
                names.put(2, new String(data[3].getBytes(), StandardCharsets.UTF_8));
                descriptions.put(1, new String(data[2].getBytes(), StandardCharsets.UTF_8));
                descriptions.put(2, new String(data[4].getBytes(), StandardCharsets.UTF_8));
                category.setDescriptions(descriptions);
                category.setNames(names);
                categoryDao.insert(connection, category);
            }
            response.sendRedirect(REDIRECT_SUCCESS);
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
