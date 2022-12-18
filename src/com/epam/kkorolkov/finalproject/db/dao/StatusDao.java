package com.epam.kkorolkov.finalproject.db.dao;

import com.epam.kkorolkov.finalproject.db.entity.Status;
import com.epam.kkorolkov.finalproject.exception.DbException;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Provides methods to deal with <i>statuses</i> table in the database.
 */
public interface StatusDao {
    /**
     * Method {@code get} retrieves a row from the table <i>statuses</i>
     * by the specified {@code id}.
     * If table does not contain a row with specified {@code id}
     * returns empty {@link Optional}.
     *
     * @param connection an instance of {@link Connection} to reach the database.
     * @param id id of a status to find.
     *
     * @return {@link Optional} representing a row from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    Optional<Status> get(Connection connection, int id) throws DbException;

    /**
     * Method {@code getAll} retrieves all rows from the table <i>statuses</i>
     *
     * @param connection an instance of {@link Connection} to reach the database.
     *
     * @return {@link List} representing all rows from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    List<Status> getAll(Connection connection) throws DbException;
}
