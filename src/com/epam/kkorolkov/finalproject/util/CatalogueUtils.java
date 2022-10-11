package com.epam.kkorolkov.finalproject.util;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.CategoryDao;
import com.epam.kkorolkov.finalproject.db.dao.PublisherDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Book;
import com.epam.kkorolkov.finalproject.db.entity.Category;
import com.epam.kkorolkov.finalproject.db.entity.Publisher;
import com.epam.kkorolkov.finalproject.exception.DBException;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CatalogueUtils {
    public static boolean validate(Book book, String message) {
        return false;
    }

    public static boolean validate(Publisher publisher) throws DBException {
        DataSource dataSource = null;
        Connection connection = null;
        PublisherDao publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            return publisherDao.get(connection, publisher.getTag()).isEmpty();
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }

    public static boolean validate(Category category) throws DBException {
        DataSource dataSource = null;
        Connection connection = null;
        CategoryDao categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            Optional<Category> optional = categoryDao.get(connection, category.getTag());
            if (optional.isEmpty() || optional.get().getId() == category.getId()) {
                return true;
            }
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
        return false;
    }


    public static boolean validateIsbn(String isbn) {
        if (isbn.length() != 13) {
            return false;
        }
        int s = 0;
        for (int i = 0; i < 13; i++) {
            char ch = isbn.charAt(i);
            if (ch > '9' || ch < '0') {
                return false;
            }
            s += (i % 2 != 0) ? (ch - '0') * 3 : ch - '0';
        }
        return s % 10 == 0;
    }
    public static Map<String, String> setParameters(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();
        String categoryId = request.getParameter("category");
        String publisherId = request.getParameter("publisher");
        String isbn = request.getParameter("isbn");
        String tag = request.getParameter("tag");
        String sortBy = request.getParameter("sort_by");
        String sortType = request.getParameter("sort_type");
        String pricesMin = request.getParameter("price_min");
        String pricesMax = request.getParameter("price_max");
        if (categoryId != null && !"".equals(categoryId)) {
            parameters.put("category_id", categoryId);
        }
        if (publisherId != null && !"".equals(publisherId)) {
            parameters.put("publisher_id", publisherId);
        }
        if (isbn != null && !"".equals(isbn)) {
            parameters.put("isbn", isbn);
        }
        if (tag != null && !"".equals(tag)) {
            parameters.put("tag", tag);
        }
        if (sortBy != null) {
            parameters.put("sort_by", sortBy);
        }
        if (sortType != null) {
            parameters.put("sort_type", sortType);
        }
        if (pricesMin != null && !"".equals(pricesMin) && pricesMax != null && !"".equals(pricesMax)) {
            parameters.put("price_min", pricesMin);
            parameters.put("price_max", pricesMax);
        }
        return parameters;
    }
}
