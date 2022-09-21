package com.epam.kkorolkov.finalproject.test;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.CategoryDao;
import com.epam.kkorolkov.finalproject.db.dao.LanguageDao;
import com.epam.kkorolkov.finalproject.db.dao.PublisherDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Category;
import com.epam.kkorolkov.finalproject.db.entity.Language;
import com.epam.kkorolkov.finalproject.db.entity.Publisher;
import com.epam.kkorolkov.finalproject.exception.DBException;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class CatalogueTest {

    @Test
    void CategoryTest() throws DBException {
        DataSource dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
        Connection connection = dataSource.getConnection();
        LanguageDao languageDao = AbstractDaoFactory.getInstance().getLanguageDao();
        CategoryDao categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();

        Category category = Category.create();
        Map<Integer, Language> languages = languageDao.getAll(connection);
        category.setTag("some-tag");
        for (int languageId : languages.keySet()) {
            category.getNames().put(languageId, "some name");
            category.getDescriptions().put(languageId, "some description");
        }
        Optional<Category> optional;
        Set<Category> categories;
        int id = categoryDao.insert(connection, category);
        optional = categoryDao.get(connection, id);
        assertTrue(optional.isPresent());
        assertEquals(category, optional.get());
        assertThrows(DBException.class, () -> categoryDao.insert(connection, category));
        categories = new HashSet<>(categoryDao.getAll(connection));
        assertTrue(categories.contains(category));
        optional = categoryDao.get(connection, category.getTag());
        assertTrue(optional.isPresent());
        assertEquals(category, optional.get());
        category.setTag("new-tag");
        categoryDao.update(connection, category);
        optional = categoryDao.get(connection, id);
        assertTrue(optional.isPresent());
        assertEquals(category, optional.get());
        assertThrows(DBException.class, () -> categoryDao.insert(connection, category));
        categories = new HashSet<>(categoryDao.getAll(connection));
        assertTrue(categories.contains(category));
        optional = categoryDao.get(connection, category.getTag());
        assertTrue(optional.isPresent());
        assertEquals(category, optional.get());
        categoryDao.delete(connection, id);
        optional = categoryDao.get(connection, id);
        assertTrue(optional.isEmpty());
        dataSource.release(connection);
    }
    @Test
    void PublisherTest() throws DBException {
        DataSource dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
        Connection connection = dataSource.getConnection();
        LanguageDao languageDao = AbstractDaoFactory.getInstance().getLanguageDao();
        PublisherDao publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();

        Publisher publisher = Publisher.create();
        Map<Integer, Language> languages = languageDao.getAll(connection);
        publisher.setTag("some-tag");
        for (int languageId : languages.keySet()) {
            publisher.getNames().put(languageId, "some name");
            publisher.getDescriptions().put(languageId, "some description");
        }
        Optional<Publisher> optional;
        Set<Publisher> publishers;
        int id = publisherDao.insert(connection, publisher);
        optional = publisherDao.get(connection, id);
        assertTrue(optional.isPresent());
        assertEquals(publisher, optional.get());
        assertThrows(DBException.class, () -> publisherDao.insert(connection, publisher));
        publishers = new HashSet<>(publisherDao.getAll(connection));
        assertTrue(publishers.contains(publisher));
        optional = publisherDao.get(connection, publisher.getTag());
        assertTrue(optional.isPresent());
        assertEquals(publisher, optional.get());
        publisher.setTag("new-tag");
        publisherDao.update(connection, publisher);
        optional = publisherDao.get(connection, id);
        assertTrue(optional.isPresent());
        assertEquals(publisher, optional.get());
        assertThrows(DBException.class, () -> publisherDao.insert(connection, publisher));
        publishers = new HashSet<>(publisherDao.getAll(connection));
        assertTrue(publishers.contains(publisher));
        optional = publisherDao.get(connection, publisher.getTag());
        assertTrue(optional.isPresent());
        assertEquals(publisher, optional.get());
        publisherDao.delete(connection, id);
        optional = publisherDao.get(connection, id);
        assertTrue(optional.isEmpty());
        dataSource.release(connection);
    }
}
