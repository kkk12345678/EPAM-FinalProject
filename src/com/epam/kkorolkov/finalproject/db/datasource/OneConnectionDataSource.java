package com.epam.kkorolkov.finalproject.db.datasource;


import com.epam.kkorolkov.finalproject.exception.DBConnectionException;
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
    public Connection getConnection() throws DBConnectionException {
        try {
            Class.forName(dbDriver);
            String connectionUrl = dbUrl + "?user=" + dbUser +"&password=" + dbPassword;
            Connection connection = DriverManager.getConnection(connectionUrl);
            LOGGER.info("Connection successful.");
            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.info("Unable to connect to the database.");
            LOGGER.error(e.getMessage());
            throw new DBConnectionException();
        }
    }

    @Override
    public void release(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Connection closed.");
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }
}
