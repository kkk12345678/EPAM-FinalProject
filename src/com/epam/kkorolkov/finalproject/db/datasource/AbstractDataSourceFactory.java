package com.epam.kkorolkov.finalproject.db.datasource;

import com.epam.kkorolkov.finalproject.exception.DBException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Properties;

public abstract class AbstractDataSourceFactory {
    protected static final String DB_PROPERTIES_FILE = "db/mysql/db.properties";
    protected static String dbUrl;
    protected static String dbUser;
    protected static String dbPassword;
    protected static String dbDriver;

    private static AbstractDataSourceFactory instance;

    public static synchronized AbstractDataSourceFactory getInstance() throws DBException {
        if (instance == null) {
            try (InputStream dbSettings = Thread.currentThread().getContextClassLoader().getResourceAsStream(DB_PROPERTIES_FILE)) {
                Properties dbProperties = new Properties();
                dbProperties.load(dbSettings);
                String datasourceFQN = dbProperties.getProperty("datasource.fqn");
                dbDriver = dbProperties.getProperty("db.driver");
                dbUrl = dbProperties.getProperty("db.url");
                dbUser = dbProperties.getProperty("db.user");
                dbPassword = dbProperties.getProperty("db.password");
                Class<?> c = Class.forName(datasourceFQN);
                Constructor<?> constr = c.getDeclaredConstructor();
                instance = (AbstractDataSourceFactory) constr.newInstance();
            } catch (IOException | ReflectiveOperationException e) {
                throw new DBException("Cannot obtain an instance of datasource", e);
            }
        }
        return instance;
    }

    protected AbstractDataSourceFactory() {}

    public abstract DataSource getDataSource();
}
