package com.epam.kkorolkov.finalproject.db.datasource;

import com.epam.kkorolkov.finalproject.exception.DBConnectionException;

import java.sql.Connection;

public interface DataSource {
    Connection getConnection() throws DBConnectionException;
    void release(Connection connection);
}
