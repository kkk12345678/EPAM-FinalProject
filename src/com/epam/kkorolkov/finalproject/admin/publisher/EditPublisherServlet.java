package com.epam.kkorolkov.finalproject.admin.publisher;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.LanguageDao;
import com.epam.kkorolkov.finalproject.db.dao.PublisherDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Language;
import com.epam.kkorolkov.finalproject.db.entity.Publisher;
import com.epam.kkorolkov.finalproject.exception.DBException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;

@WebServlet("/admin/edit-publisher")
public class EditPublisherServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<Integer, String> names = new HashMap<>();
        Map<Integer, String> descriptions = new HashMap<>();
        String tag = request.getParameter("tag");
        int id = Integer.parseInt(request.getParameter("id"));
        Map<Integer, Language> languages = getLanguages();
        for (int languageId : languages.keySet()) {
            descriptions.put(languageId, request.getParameter("description" + languageId));
            names.put(languageId, request.getParameter("name" + languageId));
        }
        DataSource dataSource = null;
        Connection connection = null;
        try {
            PublisherDao publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            Publisher publisher = new Publisher();
            publisher.setId(id);
            publisher.setTag(tag.toLowerCase().replaceAll("[^a-z0-9]+", "-"));
            publisher.setDescriptions(descriptions);
            publisher.setNames(names);
            if (id == 0) {
                publisherDao.insert(connection, publisher);
            } else {
                publisherDao.update(connection, publisher);
            }
        } catch (DBException e) {
            // TODO handle DBException
            e.printStackTrace();
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
        response.sendRedirect("./publishers");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("publisher", getPublisher(request));
        request.setAttribute("languages", getLanguages());
        request.getRequestDispatcher("../jsp/admin/publishers/edit-publisher.jsp").include(request, response);
    }

    private Publisher getPublisher(HttpServletRequest request) {
        Publisher publisher;
        DataSource dataSource = null;
        Connection connection = null;
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            PublisherDao publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
            publisher = publisherDao.get(connection, id).get();
        } catch (Exception e) {
            publisher = Publisher.create();
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
        return publisher;
    }

    private Map<Integer, Language> getLanguages() {
        Map<Integer, Language> languages = new HashMap<>();
        DataSource dataSource = null;
        Connection connection = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            LanguageDao languageDao = AbstractDaoFactory.getInstance().getLanguageDao();
            languages = languageDao.getAll(connection);
        } catch (DBException e) {
            // TODO handle DBException
            e.printStackTrace();
        } catch (NumberFormatException e) {
            return null;
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
        return languages;
    }
}
