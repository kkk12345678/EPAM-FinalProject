package com.epam.kkorolkov.finalproject.db.dao.mysql;

import com.epam.kkorolkov.finalproject.db.dao.StatusDao;
import com.epam.kkorolkov.finalproject.db.entity.Status;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.util.DBUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MysqlStatusDaoImpl extends MysqlAbstractDao implements StatusDao {
    @Override
    public Optional<Status> get(Connection connection, int id) throws DBException {
        Optional<Status> optional = Optional.empty();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.statuses.select.by.id"));
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Status status = new Status();
                status.setId(resultSet.getInt("id"));
                status.setName(resultSet.getString("name"));
                optional = Optional.of(status);
            }
            if (optional.isEmpty()) {
                LOGGER.info("No status found with id = " + id);
            } else {
                LOGGER.info("Status was found with id = " + id);
            }
        } catch (SQLException e) {
            LOGGER.info("Could not load status with locale = " + id);
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
        return optional;
    }

    @Override
    public List<Status> getAll(Connection connection) throws DBException {
        List<Status> statuses = new ArrayList<>();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_STATEMENTS.getProperty("mysql.statuses.select.all"));
            while (resultSet.next()) {
                Status status = new Status();
                status.setId(resultSet.getInt("id"));
                status.setName(resultSet.getString("name"));
                statuses.add(status);
            }
            LOGGER.info("All statuses were successfully loaded.");
        } catch (SQLException e) {
            LOGGER.info("Could not load statuses.");
            LOGGER.error(e.getMessage());
            throw new DBException(e.getMessage(), e);
        } finally {
            DBUtils.release(resultSet, statement);
        }
        return statuses;
    }
}
