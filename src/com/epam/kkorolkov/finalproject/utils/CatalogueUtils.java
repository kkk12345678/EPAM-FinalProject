package com.epam.kkorolkov.finalproject.utils;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.CategoryDao;
import com.epam.kkorolkov.finalproject.db.dao.PublisherDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Book;
import com.epam.kkorolkov.finalproject.db.entity.Category;
import com.epam.kkorolkov.finalproject.db.entity.Publisher;
import com.epam.kkorolkov.finalproject.exception.DBException;

import java.sql.Connection;
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
}
