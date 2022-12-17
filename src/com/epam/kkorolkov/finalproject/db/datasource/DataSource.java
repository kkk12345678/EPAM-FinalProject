package com.epam.kkorolkov.finalproject.db.datasource;

import com.epam.kkorolkov.finalproject.exception.DbConnectionException;

import java.sql.Connection;

/**
 * Interface {@code DataSource} provides methods to work with database {@link java.sql.Connection}.
 */
public interface DataSource {
    /**
     * Method {@code getConnection} provides an instance of {@link Connection} needed to execute queries.
     *
     * @return instance of {@link Connection}.
     *
     * @throws DbConnectionException is thrown if database could not be reached.
     */
    Connection getConnection() throws DbConnectionException;

    /**
     * {@link DataSource} specific method which closes {@link Connection}.
     *
     * @param connection an instance of {@link Connection} to be closed.
     */
    void release(Connection connection);
}
