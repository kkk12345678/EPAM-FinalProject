package com.epam.kkorolkov.finalproject.db.datasource;


import com.epam.kkorolkov.finalproject.exception.DBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OneConnectionDataSource implements DataSource {
    private static final Logger LOGGER = LogManager.getLogger("DB");
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;
    private final String dbDriver;

    public OneConnectionDataSource(String dbDriver, String dbUrl, String dbUser, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbDriver = dbDriver;
    }

    @Override
    public Connection getConnection() throws DBException {
        try {
            Class.forName(dbDriver);
            String connectionUrl = dbUrl + "?user=" + dbUser +"&password=" + dbPassword;
            Connection connection = DriverManager.getConnection(connectionUrl);
            LOGGER.info("Connection successful.");
            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            throw new DBException("Unable to connect to the database", e);
        }
    }

    @Override
    public void release(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        LOGGER.info("Connection closed.");
    }
}
