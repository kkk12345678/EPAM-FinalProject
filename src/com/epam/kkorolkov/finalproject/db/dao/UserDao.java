package com.epam.kkorolkov.finalproject.db.dao;

import com.epam.kkorolkov.finalproject.db.entity.User;
import com.epam.kkorolkov.finalproject.exception.DbException;
import com.epam.kkorolkov.finalproject.exception.ValidationException;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Provides methods to deal with <i>users</i> table in the database.
 */
public interface UserDao {
    /**
     * Method {@code get} retrieves a row from the table <i>users</i>
     * by the specified {@code id}.
     * If table does not contain a row with specified {@code id}
     * returns empty {@link Optional}.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param id - id of a user to find.
     *
     * @return {@link Optional<User>} representing a row from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    Optional<User> get(Connection connection, int id) throws DbException;

    /**
     * Method {@code get} retrieves a row from the table <i>users</i>
     * by the specified {@code email}.
     * If table does not contain a row with specified {@code email}
     * returns empty {@link Optional}.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param email - email of a user to find.
     *
     * @return {@link Optional<User>} representing a row from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    Optional<User> get(Connection connection, String email) throws DbException;

    /**
     * Method {@code getAll} retrieves all rows from the table <i>users</i>
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     *
     * @return {@link List<User>} representing all rows from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    List<User> getAll(Connection connection) throws DbException;

    /**
     * Method {@code update} updates a row which represents a user in the table <i>users</i>.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param user- a user to be updated.
     *
     * @throws DbException is thrown if data cannot be updated.
     */
    void update(Connection connection, User user) throws DbException;

    /**
     * Method {@code delete} deletes a row which represents a user in the table <i>users</i>.
     *
     * @param connection - an  of {@link Connection} to reach the database.
     * @param id - id of a user to be deleted.
     *
     * @throws DbException is thrown if data cannot be deleted.
     */
    void delete(Connection connection, int id) throws DbException;

    /**
     * Method {@code insert} inserts a row to the table <i>users</i> with data specified in user.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param user - a user to be inserted.
     *
     * @throws DbException is thrown if data cannot be deleted.
     * @throws ValidationException is thrown if user data is incorrect or
     * user with same id or email is already exists in the database
     */
    int insert(Connection connection, User user) throws DbException, ValidationException;

    /**
     * @return number of rows in the table <i>users</i>.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    int count(Connection connection) throws DbException;
}
