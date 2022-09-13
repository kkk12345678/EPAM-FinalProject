package com.epam.kkorolkov.finalproject.db.datasource;

import com.epam.kkorolkov.finalproject.exception.DBException;

import java.sql.Connection;

public interface DataSource {
    Connection getConnection() throws DBException;
    void release(Connection connection);
}
