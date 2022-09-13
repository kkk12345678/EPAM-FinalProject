package com.epam.kkorolkov.finalproject.db.dao;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Properties;

public abstract class AbstractDaoFactory {
    private static final String DB_PROPERTIES_FILE = "db/mysql/db.properties";

    private static AbstractDaoFactory instance;


    public static synchronized AbstractDaoFactory getInstance() {
        if (instance == null) {
            try (InputStream dbSettings = Thread.currentThread().getContextClassLoader().getResourceAsStream(DB_PROPERTIES_FILE)) {
                Properties dbProperties = new Properties();
                dbProperties.load(dbSettings);
                String daoFQN = dbProperties.getProperty("dao.fqn");
                Class<?> c = Class.forName(daoFQN);
                Constructor<?> constr = c.getDeclaredConstructor();
                instance = (AbstractDaoFactory) constr.newInstance();
            } catch (IOException | ReflectiveOperationException e) {
                throw new IllegalStateException("Cannot obtain an instance of dao", e);
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
