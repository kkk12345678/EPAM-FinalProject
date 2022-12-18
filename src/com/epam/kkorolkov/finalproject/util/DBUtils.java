package com.epam.kkorolkov.finalproject.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class {@code DBUtils} contains static utility methods to
 * work with (@code java.sql.*} package. Used to minimize
 * number of code lines in servlets.
 */
public class DBUtils {
    /** Logger */
    private static final Logger LOGGER = LogManager.getLogger("UTILS");

    /**
     * Close previously opened {@link ResultSet} and {@link Statement}.
     *
     * @param resultSet {@link ResultSet} instance to be closed;
     * @param statement {@link Statement} instance to be closed;
     */
    public static void release(ResultSet resultSet, Statement statement) {
        closeResultSet(resultSet);
        closeStatement(statement);
    }

    /**
     * Closes previously opened {@link Statement}.
     *
     * @param statement an instance {@link Statement} instance to be closed;
     */
    public static void release(Statement statement) {
        closeStatement(statement);
    }

    /**
     * Closes previously opened {@link Connection}.
     *
     * @param connection {@link Connection} instance to be closed;
     */
    public static void release(Connection connection) {
        try {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Closes previously opened {@link ResultSet}.
     *
     * @param resultSet {@link ResultSet} instance to be closed;
     */
    private static void closeResultSet(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Closes previously opened {@link Statement}.
     *
     * @param statement {@link Statement} instance to be closed;
     */
    private static void closeStatement(Statement statement){
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
