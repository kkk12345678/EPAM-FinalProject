package com.epam.kkorolkov.finalproject.db.dao;

import com.epam.kkorolkov.finalproject.db.entity.Category;
import com.epam.kkorolkov.finalproject.exception.DbException;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Provides methods to deal with <i>categories</i> table in the database.
 */
public interface CategoryDao {
    /**
     * @return number of rows in the table <i>categories</i>.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    int count(Connection connection) throws DbException;

    /**
     * Method {@code get} retrieves a row from the table <i>categories</i>
     * by the specified {@code id}.
     * If table does not contain a row with specified {@code id}
     * returns empty {@link Optional}.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param id - id of a category to find.
     *
     * @return {@link Optional<Category>} representing a row from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    Optional<Category> get(Connection connection, int id) throws DbException;

    /**
     * Method {@code get} retrieves a row from the table <i>categories</i>
     * by the specified {@code tag}.
     * If table does not contain a row with specified {@code tag}
     * returns empty {@link Optional}.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param tag - tag of a category to find.
     *
     * @return {@link Optional<Category>} representing a row from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    Optional<Category> get(Connection connection, String tag) throws DbException;

    /**
     * Method {@code getAll} retrieves all rows from the table <i>categories</i>
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     *
     * @return {@link List<Category>} representing all rows from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    List<Category> getAll(Connection connection) throws DbException;

    /**
     * Method {@code update} updates a row which represents a category in the table <i>categories</i>.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param category - a category to be updated.
     *
     * @throws DbException is thrown if data cannot be updated.
     */
    void update(Connection connection, Category category) throws DbException;

    /**
     * Method {@code delete} deletes a row which represents a category in the table <i>categories</i>.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param id - id of a category to be deleted.
     *
     * @throws DbException is thrown if data cannot be deleted.
     */
    void delete(Connection connection, int id) throws DbException;

    /**
     * Method {@code insert} inserts a row to the table <i>categories</i> with data specified in category.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param category - a category to be inserted.
     *
     * @throws DbException is thrown if data cannot be deleted.
     */
    int insert(Connection connection, Category category) throws DbException;
}
