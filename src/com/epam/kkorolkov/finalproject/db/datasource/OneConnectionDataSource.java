package com.epam.kkorolkov.finalproject.db.datasource;

import com.epam.kkorolkov.finalproject.exception.DbConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * An instance {@code OneConnectionDataSource} implements {@link DataSource}
 * without any database connection pool realization.
 */
public class OneConnectionDataSource extends AbstractDataSource implements DataSource {
    public OneConnectionDataSource(String dbDriver, String dbUrl, String dbUser, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbDriver = dbDriver;
    }

    /**
     * @return an instance of {@link Connection} using
     * {@link DriverManager#getConnection(String)} method.
     *
     * @throws DbConnectionException is thrown if
     * it is not possible connect to the database.
     */
    @Override
    public Connection getConnection() throws DbConnectionException {
        try {
            Class.forName(dbDriver);
            Connection connection = DriverManager
                    .getConnection(String.format(mySqlConnectionUrlFormat, dbUrl, dbUser, dbPassword));
            LOGGER.info(MESSAGE_SUCCESS);
            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.info(MESSAGE_ERROR);
            LOGGER.error(e.getMessage());
            throw new DbConnectionException();
        }
    }

    /**
     * Closes an instance of {@link Connection}.
     * If {@link SQLException} is thrown while invoking {@code close} method
     * it is ignored.
     *
     * @param connection an instance of {@link Connection} to be closed.
     */
    @Override
    public void release(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info(MESSAGE_CLOSE);
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }
}
