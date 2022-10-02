package com.epam.kkorolkov.finalproject.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtils {
    private static final Logger LOGGER = LogManager.getLogger("DBUtils");

    private static void closeResultSet(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
                //LOGGER.info("Result is closed.");
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void closeStatement(Statement statement){
        try {
            if (statement != null) {
                statement.close();
                //LOGGER.info("Statement is closed.");
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void release(ResultSet resultSet, Statement statement) {
        closeResultSet(resultSet);
        closeStatement(statement);
    }

    public static void release(Statement statement) {
        closeStatement(statement);
    }
}
