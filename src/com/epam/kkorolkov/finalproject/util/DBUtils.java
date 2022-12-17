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
     * Close previously opened {@code ResultSet} and {@code Statement}.
     *
     * @param resultSet - {@code ResultSet} instance to close;
     * @param statement - {@code Statement} instance to close;
     */
    public static void release(ResultSet resultSet, Statement statement) {
        closeResultSet(resultSet);
        closeStatement(statement);
    }

    /**
     * Close previously opened {@code Statement}.
     *
     * @param statement - {@code Statement} instance to close;
     */
    public static void release(Statement statement) {
        closeStatement(statement);
    }

    /**
     * Close previously opened {@code Connection}.
     *
     * @param connection - {@code Connection} instance to close;
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

    private static void closeResultSet(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

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
