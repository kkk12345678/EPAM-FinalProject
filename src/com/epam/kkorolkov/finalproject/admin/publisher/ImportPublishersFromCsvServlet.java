package com.epam.kkorolkov.finalproject.admin.publisher;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.PublisherDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Publisher;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.util.DBUtils;

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

@WebServlet("/admin/import-publishers")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class ImportPublishersFromCsvServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DataSource dataSource = null;
        Connection connection = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getPart("file").getInputStream())))  {
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
        } catch (DBException e) {
            // TODO handle DBException
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
        response.sendRedirect("publishers");
    }
}
