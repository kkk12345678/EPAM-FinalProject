package com.epam.kkorolkov.finalproject.db.dao.mysql;

import com.epam.kkorolkov.finalproject.db.dao.UserDao;
import com.epam.kkorolkov.finalproject.db.entity.User;
import com.epam.kkorolkov.finalproject.exception.DbException;
import com.epam.kkorolkov.finalproject.exception.ValidationException;
import com.epam.kkorolkov.finalproject.util.DBUtils;
import com.epam.kkorolkov.finalproject.util.UserUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *  Provides MySQL specific implementation of {@link UserDao}.
 */
public class MysqlUserDaoImpl extends MysqlAbstractDao implements UserDao {
    /** SQL statements */
    private static final String SQL_SELECT_ALL = SQL_STATEMENTS.getProperty("mysql.users.select.all");
    private static final String SQL_UPDATE = SQL_STATEMENTS.getProperty("mysql.users.update");
    private static final String SQL_DELETE = SQL_STATEMENTS.getProperty("mysql.users.delete");
    private static final String SQL_INSERT = SQL_STATEMENTS.getProperty("mysql.users.insert");
    private static final String SQL_COUNT = SQL_STATEMENTS.getProperty("mysql.users.select.count");
    private static final String SQL_GET_ID = SQL_STATEMENTS.getProperty("mysql.users.select.by.id");
    private static final String SQL_GET_EMAIL = SQL_STATEMENTS.getProperty("mysql.users.select.by.email");

    /** Logger success messages */
    private static final String MESSAGE_USERS_COUNTED = "There are %d users in the table.";
    private static final String MESSAGE_USERS_LOADED = "All users were successfully loaded.";
    private static final String MESSAGE_USER_ID_FOUND = "User with id = %d was successfully found.";
    private static final String MESSAGE_USER_ID_NOT_FOUND = "No user with id = %d was found.";
    private static final String MESSAGE_BOOK_EMAIL_FOUND = "User with email = %s was successfully found.";
    private static final String MESSAGE_BOOK_EMAIL_NOT_FOUND = "No user with email = %s was found.";
    private static final String MESSAGE_USER_UPDATED = "User %s %s successfully updated.";
    private static final String MESSAGE_USER_DELETED = "User with id = %d successfully deleted.";
    private static final String MESSAGE_USER_INSERTED = "User %s %s successfully inserted (id = %d).";

    /** Logger error messages */
    private static final String ERROR_USERS_NOT_COUNTED = "Could not count users.";
    private static final String ERROR_USER_ID_NOT_LOADED = "User with id = %d could not be retrieved.";
    private static final String ERROR_USERS_NOT_LOADED = "Could not load users.";
    private static final String ERROR_USER_NOT_UPDATED = "Could not update user with id=";
    private static final String ERROR_USER_NOT_DELETED = "Could not delete user with id ";
    private static final String ERROR_USER_NOT_INSERTED = "Could not insert user with id ";

    /** Table fields */
    private static final String FIELD_ID = "id";
    private static final String FIELD_FIRST_NAME = "first_name";
    private static final String FIELD_LAST_NAME = "last_name";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_PASSWORD = "password";
    private static final String FIELD_ADMIN = "is_admin";
    private static final String FIELD_BLOCKED = "is_blocked";

    /**
     * MySQL specific realization of {@link UserDao#getAll(Connection)} method.
     * Retrieves rows from the table <i>users</i>.
     *
     * @param connection an instance of {@link Connection} to reach the database.
     *
     * @return {@link List} representing all rows from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    @Override
    public List<User> getAll(Connection connection) throws DbException {
        List<User> users = new ArrayList<>();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_SELECT_ALL);
            while (resultSet.next()) {
                users.add(extractUser(resultSet));
            }
            LOGGER.info(MESSAGE_USERS_LOADED);
        } catch (SQLException e) {
            LOGGER.info(ERROR_USERS_NOT_LOADED);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, statement);
        }
        return users;
    }

    /**
     * MySQL specific realization of {@link UserDao#update(Connection, User)} method.
     * Updates a row which represents a user in the table <i>users</i>.
     *
     * @param connection an instance of {@link Connection} to reach the database.
     * @param user- a user to be updated.
     *
     * @throws DbException is thrown if data cannot be updated.
     */
    @Override
    public void update(Connection connection, User user) throws DbException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_UPDATE);
            prepareStatement(preparedStatement, user);
            preparedStatement.setInt(7, user.getId());
            preparedStatement.execute();
            LOGGER.info(String.format(MESSAGE_USER_UPDATED,
                    user.getFirstName(), user.getLastName()));
        } catch (SQLException e) {
            LOGGER.info(ERROR_USER_NOT_UPDATED + user.getId());
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(preparedStatement);
        }
    }

    /**
     * MySQL specific realization of {@link UserDao#delete(Connection, int)} method.
     * Deletes a row which represents a user in the table <i>users</i>.
     *
     * @param connection an  of {@link Connection} to reach the database.
     * @param id id of a user to be deleted.
     *
     * @throws DbException is thrown if data cannot be deleted.
     */
    @Override
    public void delete(Connection connection, int id) throws DbException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_DELETE);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            LOGGER.info(String.format(MESSAGE_USER_DELETED, id));
        } catch (SQLException e) {
            LOGGER.info(ERROR_USER_NOT_DELETED + id);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(preparedStatement);
        }
    }

    /**
     * MySQL specific realization of {@link UserDao#insert(Connection, User)} method.
     * Inserts a row to the table <i>users</i> with data specified in user.
     *
     * @param connection an instance of {@link Connection} to reach the database.
     * @param user a user to be inserted.
     *
     * @return the id of the inserted {@link User}.
     *
     * @throws DbException is thrown if data cannot be deleted.
     * @throws ValidationException is thrown if user data is incorrect or
     * user with same id or email is already exists in the database
     */
    @Override
    public int insert(Connection connection, User user) throws DbException, ValidationException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            UserUtils.validateEmail(user.getEmail());
            preparedStatement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            prepareStatement(preparedStatement, user);
            preparedStatement.execute();
            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            int id = resultSet.getInt(1);
            LOGGER.info(String.format(MESSAGE_USER_INSERTED,
                    user.getFirstName(), user.getLastName(), id));
            return id;
        } catch (SQLException e) {
            LOGGER.info(ERROR_USER_NOT_INSERTED + user.getId());
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }

    }

    /**
     * MySQL specific realization of {@link UserDao#count(Connection)} method.
     *
     * @return number of rows in the table <i>users</i>.
     *
     * @param connection an instance of {@link Connection} to reach the database.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    @Override
    public int count(Connection connection) throws DbException {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_COUNT);
            resultSet.next();
            int c = resultSet.getInt(1);
            LOGGER.info(String.format(MESSAGE_USERS_COUNTED, c));
            return c;
        } catch (SQLException e) {
            LOGGER.info(ERROR_USERS_NOT_COUNTED);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, statement);
        }
    }

    /**
     * MySQL specific realization of {@link UserDao#get(Connection, int)} method.
     * Method {@code get} retrieves a row from the table <i>users</i>
     * by the specified {@code id}.
     * If table does not contain a row with specified {@code id}
     * returns empty {@link Optional}.
     *
     * @param connection an instance of {@link Connection} to reach the database.
     * @param id id of a user to find.
     *
     * @return {@link Optional} representing a row from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    @Override
    public Optional<User> get(Connection connection, int id) throws DbException {
        Optional<User> optional = Optional.empty();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_GET_ID);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                optional = Optional.of(extractUser(resultSet));
                LOGGER.info(String.format(MESSAGE_USER_ID_FOUND, id));
            } else {
                LOGGER.info(String.format(MESSAGE_USER_ID_NOT_FOUND, id));
            }
        } catch (SQLException e) {
            LOGGER.info(ERROR_USER_ID_NOT_LOADED);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
        return optional;
    }

    /**
     * MySQL specific realization of {@link UserDao#get(Connection, String)} method.
     * Retrieves a row from the table <i>users</i> by the specified {@code email}.
     * If table does not contain a row with specified {@code email}
     * returns empty {@link Optional}.
     *
     * @param connection an instance of {@link Connection} to reach the database.
     * @param email email of a user to find.
     *
     * @return {@link Optional} representing a row from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    @Override
    public Optional<User> get(Connection connection, String email) throws DbException {
        Optional<User> optional = Optional.empty();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_GET_EMAIL);
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                optional = Optional.of(extractUser(resultSet));
                LOGGER.info(String.format(MESSAGE_BOOK_EMAIL_FOUND, email));
            } else {
                LOGGER.info(String.format(MESSAGE_BOOK_EMAIL_NOT_FOUND, email));
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
        return optional;
    }

    /**
     * A utility method that prepares {@link PreparedStatement} with data from the {@link User} instance.
     *
     * @param preparedStatement an instance of {@link PreparedStatement} to be prepared.
     * @param user an instance of {@link User} with data to set.
     *
     * @throws SQLException is thrown if {@link SQLException} is thrown while preparing statement.
     */
    private void prepareStatement(PreparedStatement preparedStatement, User user) throws SQLException {
        preparedStatement.setString(1, user.getFirstName());
        preparedStatement.setString(2, user.getLastName());
        preparedStatement.setString(3, user.getEmail());
        preparedStatement.setString(4, user.getPassword());
        preparedStatement.setBoolean(5, user.getIsAdmin());
        preparedStatement.setBoolean(6, user.getIsBlocked());
    }

    /**
     * A utility method that extracts an instance of {@link User}
     * from the {@link ResultSet} instance.
     *
     * @param resultSet an instance of {@link ResultSet} containing necessary data.
     *
     * @return an instance of {@link User} containing all necessary data.
     *
     * @throws SQLException is thrown if {@link SQLException} is thrown
     * while processing {@link ResultSet} instance.
     */
    private User extractUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt(FIELD_ID));
        user.setFirstName(resultSet.getString(FIELD_FIRST_NAME));
        user.setLastName(resultSet.getString(FIELD_LAST_NAME));
        user.setEmail(resultSet.getString(FIELD_EMAIL));
        user.setPassword(resultSet.getString(FIELD_PASSWORD));
        user.setAdmin(resultSet.getBoolean(FIELD_ADMIN));
        user.setBlocked(resultSet.getBoolean(FIELD_BLOCKED));
        return user;
    }
}
