package com.epam.kkorolkov.finalproject.db.dao;

import com.epam.kkorolkov.finalproject.exception.DaoException;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Properties;

/**
 * A abstract factory class which ancestors provide an instance of
 * different DAOs to communicate with the database.
 * The type of datasource factory is specified in {@code dao.fqn} property.
 */
public abstract class AbstractDaoFactory {
    /**
     * An instance of a singleton {@code AbstractDaoFactory}.
     */
    private static AbstractDaoFactory instance;

    /**
     * Realization of a classical singleton.
     *
     * @return {@code instance} field if it has been instantiated, otherwise instantiates and then returns.
     *
     * @throws DaoException is thrown if DAO cannot be instantiated.
     */
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

    /**
     * Empty constructor. Needed for ancestors instantiation.
     */
    protected AbstractDaoFactory() {}

    /**
     * @return an instance of {@link BookDao}.
     */
    public abstract BookDao getBookDao();

    /**
     * @return an instance of {@link CategoryDao}.
     */
    public abstract CategoryDao getCategoryDao();

    /**
     * @return an instance of {@link LanguageDao}.
     */
    public abstract LanguageDao getLanguageDao();

    /**
     * @return an instance of {@link OrderDao}.
     */
    public abstract OrderDao getOrderDao();

    /**
     * @return an instance of {@link PublisherDao}.
     */
    public abstract PublisherDao getPublisherDao();

    /**
     * @return an instance of {@link CategoryDao}.
     */
    public abstract StatusDao getStatusDao();

    /**
     * @return an instance of {@link CategoryDao}.
     */
    public abstract UserDao getUserDao();
}
