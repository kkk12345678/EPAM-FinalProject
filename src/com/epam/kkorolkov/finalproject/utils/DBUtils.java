package com.epam.kkorolkov.finalproject.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtils {
    private static void closeResultSet(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static void closeStatement(Statement statement){
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            statement = null;
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
