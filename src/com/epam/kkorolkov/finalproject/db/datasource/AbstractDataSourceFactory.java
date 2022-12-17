package com.epam.kkorolkov.finalproject.db.datasource;

import com.epam.kkorolkov.finalproject.exception.DbConnectionException;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Properties;

/**
 * A abstract factory class which ancestors provide an instance of {@link DataSource}.
 * The type of {@link DataSource} is specified in {@code datasource.fqn} property.
 */
public abstract class AbstractDataSourceFactory {
    /** Database credentials */
    protected static String dbUrl;
    protected static String dbUser;
    protected static String dbPassword;

    /** Database driver */
    protected static String dbDriver;

    /** Classical singleton with static field instance */
    private static AbstractDataSourceFactory instance;

    /** Classical singleton with protected empty constructor */
    protected AbstractDataSourceFactory() {}

    /**
     * A singleton that reads connection parameters and {@link DataSource} class
     * from properties file.
     *
     * @return an instance of {@link AbstractDataSourceFactory}
     * which provides {@code getDataSource} method with corresponding {@link DataSource}.
     *
     * @throws DbConnectionException is thrown if database could not be reached.
     */
    public static synchronized AbstractDataSourceFactory getInstance() throws DbConnectionException {
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
                throw new DbConnectionException();
            }
        }
        return instance;
    }

    /**
     * Abstract method {@code getDataSource} must be overridden to get specific {@link DataSource} provider.
     *
     * @return an instance of {@link DataSource} containing parameters necessary connect to the database.
     */
    public abstract DataSource getDataSource();
}
