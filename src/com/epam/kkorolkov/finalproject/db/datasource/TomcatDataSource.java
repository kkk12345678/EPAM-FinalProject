package com.epam.kkorolkov.finalproject.db.datasource;

import com.epam.kkorolkov.finalproject.exception.DbConnectionException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * An instance {@code TomcatDataSource} implements {@link DataSource}
 * with database connection pool realization provided by Tomcat.
 */
public class TomcatDataSource extends AbstractDataSource implements DataSource {
    /** Tomcat database connection pool parameters */
    private static final String INIT_LOOKUP_OBJECT_NAME = "java:/comp/env";
    private static final String ENV_LOOKUP_OBJECT_NAME = "jdbc/ubd01_epamfinal";

    /**
     * Provides an instance of {@link Connection} from the connection pool
     * provided by Tomcat.
     *
     * @return an instance of {@link Connection}.
     *
     * @throws DbConnectionException is thrown if database could not be reached.
     */
    @Override
    public Connection getConnection() throws DbConnectionException {
        try {
            Context initContext = new InitialContext();
            Context envContext  = (Context) initContext.lookup(INIT_LOOKUP_OBJECT_NAME);
            javax.sql.DataSource ds = (javax.sql.DataSource) envContext.lookup(ENV_LOOKUP_OBJECT_NAME);
            LOGGER.info(MESSAGE_SUCCESS);
            return ds.getConnection();
        } catch (NamingException | SQLException e) {
            LOGGER.info(MESSAGE_ERROR);
            LOGGER.error(e.getMessage());
            throw new DbConnectionException();
        }
    }

    /**
     * Returns an instance of {@link Connection} to the pool.
     * Sets connection parameters to initial state.
     * If {@link SQLException} is thrown while invoking {@code close} method
     * connection is not returned to the pool.
     *
     * @param connection an instance of {@link Connection} to be closed.
     */
    @Override
    public void release(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
                LOGGER.info(MESSAGE_CLOSE);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
