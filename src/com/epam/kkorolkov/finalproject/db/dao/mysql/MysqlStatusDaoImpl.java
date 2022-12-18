package com.epam.kkorolkov.finalproject.db.dao.mysql;

import com.epam.kkorolkov.finalproject.db.dao.StatusDao;
import com.epam.kkorolkov.finalproject.db.entity.Status;
import com.epam.kkorolkov.finalproject.exception.DbException;
import com.epam.kkorolkov.finalproject.util.DBUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *  Provides MySQL specific implementation of {@link StatusDao}.
 */
public class MysqlStatusDaoImpl extends MysqlAbstractDao implements StatusDao {
    /** SQL statements */
    private static final String SQL_GET_ID = SQL_STATEMENTS.getProperty("mysql.statuses.select.by.id");
    private static final String SQL_GET_ALL = SQL_STATEMENTS.getProperty("mysql.statuses.select.all");

    /** Logger success messages */
    private static final String MESSAGE_STATUSES_LOADED = "All statuses were successfully loaded.";
    private static final String MESSAGE_STATUS_ID_FOUND = "Status was found with id = ";
    private static final String MESSAGE_STATUS_ID_NOT_FOUND = "No status found with id = ";

    /** Logger error messages */
    private static final String ERROR_STATUS_ID_NOT_LOADED = "Could not load status with id = ";
    private static final String ERROR_STATUSES_NOT_LOADED = "Could not load statuses.";

    /** Table fields */
    private static final String FIELD_ID = "id";
    private static final String FIELD_NAME = "name";

    /**
     * MySQL specific realization of {@link StatusDao#get(Connection, int)} method.
     * Retrieves a row from the table <i>statuses</i> by the specified {@code id}.
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
    @Override
    public Optional<Status> get(Connection connection, int id) throws DbException {
        Optional<Status> optional = Optional.empty();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_GET_ID);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Status status = new Status();
                status.setId(resultSet.getInt(FIELD_ID));
                status.setName(resultSet.getString(FIELD_NAME));
                optional = Optional.of(status);
            }
            if (optional.isEmpty()) {
                LOGGER.info(MESSAGE_STATUS_ID_NOT_FOUND + id);
            } else {
                LOGGER.info(MESSAGE_STATUS_ID_FOUND + id);
            }
        } catch (SQLException e) {
            LOGGER.info(ERROR_STATUS_ID_NOT_LOADED + id);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
        return optional;
    }
    /**
     * MySQL specific realization of {@link StatusDao#getAll(Connection)} )} method.
     * Retrieves all rows from the table <i>statuses</i>
     *
     * @param connection an instance of {@link Connection} to reach the database.
     *
     * @return {@link List} representing all rows from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    @Override
    public List<Status> getAll(Connection connection) throws DbException {
        List<Status> statuses = new ArrayList<>();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_GET_ALL);
            while (resultSet.next()) {
                Status status = new Status();
                status.setId(resultSet.getInt(FIELD_ID));
                status.setName(resultSet.getString(FIELD_NAME));
                statuses.add(status);
            }
            LOGGER.info(MESSAGE_STATUSES_LOADED);
        } catch (SQLException e) {
            LOGGER.info(ERROR_STATUSES_NOT_LOADED);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, statement);
        }
        return statuses;
    }
}
