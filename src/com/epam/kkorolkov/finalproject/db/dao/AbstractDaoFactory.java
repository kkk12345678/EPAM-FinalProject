package com.epam.kkorolkov.finalproject.db.dao;

import com.epam.kkorolkov.finalproject.exception.DaoException;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Properties;

public abstract class AbstractDaoFactory {
    private static AbstractDaoFactory instance;

    public static synchronized AbstractDaoFactory getInstance() throws DaoException {
        if (instance == null) {
            try (InputStream dbSettings = Thread.currentThread().getContextClassLoader().getResourceAsStream("db/mysql/db.properties")) {
                Properties dbProperties = new Properties();
                dbProperties.load(dbSettings);
                Class<?> c = Class.forName(dbProperties.getProperty("dao.fqn"));
                Constructor<?> constructor = c.getDeclaredConstructor();
                instance = (AbstractDaoFactory) constructor.newInstance();
            } catch (IOException | ReflectiveOperationException e) {
                LogManager.getLogger("DAO").info("Could not instantiate DAO.");
                LogManager.getLogger("DAO").error(e.getMessage());
                throw new DaoException();
            }
        }
        return instance;
    }

    protected AbstractDaoFactory() {}

    public abstract BookDao getBookDao();
    public abstract CategoryDao getCategoryDao();
    public abstract LanguageDao getLanguageDao();
    public abstract OrderDao getOrderDao();
    public abstract PublisherDao getPublisherDao();
    public abstract StatusDao getStatusDao();
    public abstract UserDao getUserDao();
}
