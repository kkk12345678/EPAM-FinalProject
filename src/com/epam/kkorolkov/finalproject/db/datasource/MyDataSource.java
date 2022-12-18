package com.epam.kkorolkov.finalproject.db.datasource;

import com.epam.kkorolkov.finalproject.exception.DbConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * An instance {@code MyDataSource} implements {@link DataSource}
 * with simple database connection pool realization.
 */
public class MyDataSource extends AbstractDataSource implements DataSource {
    /** Initial pool size */
    private static final int N = 10;

    /** {@link java.util.Collection} of {@link Connection} */
    private static final Queue<Connection> freeConnections = new LinkedList<>();

    /**
     * Constructs an instance of {@code MyDataSource} with necessary driver and credentials.
     *
     * @param dbDriver database driver.
     * @param dbUrl database URL.
     * @param dbUser database user.
     * @param dbPassword user's password.
     */
    public MyDataSource(String dbDriver, String dbUrl, String dbUser, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbDriver = dbDriver;
    }

    /**
     * Provides an instance of {@link Connection} from the {@code freeConnections}.
     * If there are no unused connections adds {@code N} new connections to the pool.
     *
     * @return an instance of {@link Connection}.
     *
     * @throws DbConnectionException is thrown if database could not be reached.
     */
    @Override
    public Connection getConnection() throws DbConnectionException {
        try {
            Class.forName(dbDriver);
            if (freeConnections.size() == 0) {
                for (int i = 0; i < N; i++) {
                    freeConnections.add(DriverManager
                            .getConnection(String.format(mySqlConnectionUrlFormat, dbUrl, dbUser, dbPassword)));
                }
            }
            return freeConnections.poll();
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.info(MESSAGE_ERROR);
            LOGGER.error(e.getMessage());
            throw new DbConnectionException();
        }
    }

    /**
     * Returns an instance of {@link Connection} to the pool.
     * Sets connection parameters to initial state.
     * If {@link SQLException} is thrown while invoking {@code setAutoCommit} method
     * connection is not returned to the pool.
     *
     * @param connection an instance of {@link Connection} to be closed.
     */
    @Override
    public void release(Connection connection) {
        try {
            connection.setAutoCommit(false);
            freeConnections.add(connection);
            LOGGER.info(MESSAGE_CLOSE);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
