package com.epam.kkorolkov.finalproject.db.dao;

import com.epam.kkorolkov.finalproject.db.entity.Publisher;
import com.epam.kkorolkov.finalproject.exception.DbException;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Provides methods to deal with <i>publishers</i> table in the database.
 */
public interface PublisherDao {
    /**
     * @return number of rows in the table <i>publishers</i>.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    int count(Connection connection) throws DbException;

    /**
     * Method {@code get} retrieves a row from the table <i>publishers</i>
     * by the specified {@code id}.
     * If table does not contain a row with specified {@code id}
     * returns empty {@link Optional}.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param id - id of a publisher to find.
     *
     * @return {@link Optional<Publisher>} representing a row from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    Optional<Publisher> get(Connection connection, int id) throws DbException;

    /**
     * Method {@code get} retrieves a row from the table <i>publishers</i>
     * by the specified {@code tag}.
     * If table does not contain a row with specified {@code tag}
     * returns empty {@link Optional}.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param tag - tag of a publisher to find.
     *
     * @return {@link Optional<Publisher>} representing a row from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    Optional<Publisher> get(Connection connection, String tag) throws DbException;

    /**
     * Method {@code getAll} retrieves all rows from the table <i>publishers</i>
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     *
     * @return {@link List<Publisher>} representing all rows from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    List<Publisher> getAll(Connection connection) throws DbException;

    /**
     * Method {@code update} updates a row which represents a publisher in the table <i>publishers</i>.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param publisher - a publisher to be updated.
     *
     * @throws DbException is thrown if data cannot be updated.
     */
    void update(Connection connection, Publisher publisher) throws DbException;

    /**
     * Method {@code delete} deletes a row which represents a publisher in the table <i>publishers</i>.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param id - id of a publisher to be deleted.
     *
     * @throws DbException is thrown if data cannot be deleted.
     */
    void delete(Connection connection, int id) throws DbException;

    /**
     * Method {@code insert} inserts a row to the table <i>publishers</i> with data specified in publisher.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param publisher - a publisher to be inserted.
     *
     * @throws DbException is thrown if data cannot be deleted.
     */
    int insert(Connection connection, Publisher publisher) throws DbException;
}
