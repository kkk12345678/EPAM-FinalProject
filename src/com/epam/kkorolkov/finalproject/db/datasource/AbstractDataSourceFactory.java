package com.epam.kkorolkov.finalproject.db.datasource;

import com.epam.kkorolkov.finalproject.exception.DBConnectionException;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Properties;

public abstract class AbstractDataSourceFactory {
    protected static String dbUrl;
    protected static String dbUser;
    protected static String dbPassword;
    protected static String dbDriver;

    private static AbstractDataSourceFactory instance;

    protected AbstractDataSourceFactory() {}

    public static synchronized AbstractDataSourceFactory getInstance() throws DBConnectionException {
        if (instance == null) {
            try (InputStream dbSettings = Thread.currentThread().getContextClassLoader().getResourceAsStream("db/mysql/db.properties")) {
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
                LogManager.getLogger("DB").info("Cannot obtain an instance of datasource.");
                LogManager.getLogger("DB").error(e.getMessage());
                throw new DBConnectionException();
            }
        }
        return instance;
    }

    public abstract DataSource getDataSource();
}
