package com.epam.kkorolkov.finalproject.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtils {
    private static final Logger LOGGER = LogManager.getLogger("UTILS");

    public static void release(ResultSet resultSet, Statement statement) {
        closeResultSet(resultSet);
        closeStatement(statement);
    }

    public static void release(Statement statement) {
        closeStatement(statement);
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

    public static void release(Connection connection) {
        try {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
