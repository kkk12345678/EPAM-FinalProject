package com.epam.kkorolkov.finalproject.db.datasource;

import com.epam.kkorolkov.finalproject.exception.DBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.SQLException;

public class TomcatDataSource implements com.epam.kkorolkov.finalproject.db.datasource.DataSource {
    private static final Logger LOGGER = LogManager.getLogger("DB");

    @Override
    public Connection getConnection() throws DBException {
        try {
            Context initContext = new InitialContext();
            Context envContext  = (Context) initContext.lookup("java:/comp/env");
            javax.sql.DataSource ds = (javax.sql.DataSource) envContext.lookup("jdbc/ubd01_epamfinal");
            LOGGER.info("Connection successful.");
            return ds.getConnection();
        } catch (NamingException | SQLException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
            throw new DBException("Unable to connect to the database", e);
        }


    }

    @Override
    public void release(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
                LOGGER.info("Connection closed.");
            }

        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
