package com.epam.kkorolkov.finalproject.admin.category;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.CategoryDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Category;

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

@WebServlet("/admin/import-categories")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class ImportCategoriesFromCsvServlet extends HttpServlet {
     protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
         DataSource dataSource = null;
         Connection connection = null;

         try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getPart("file").getInputStream())))  {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            CategoryDao categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();
            reader.readLine();
            while (reader.ready()) {
                String[] data = reader.readLine().split(",");
                Category category = new Category();
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
             if (dataSource != null) {
                 dataSource.release(connection);
             }
        }
        response.sendRedirect("categories");
    }
}
