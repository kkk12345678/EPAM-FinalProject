package com.epam.kkorolkov.finalproject.db.dao.mysql;

import com.epam.kkorolkov.finalproject.db.dao.UserDao;
import com.epam.kkorolkov.finalproject.db.entity.User;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.utils.DBUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MysqlUserDaoImpl extends MysqlAbstractDao implements UserDao {
    @Override
    public List<User> getAll(Connection connection) throws DBException {
        List<User> users = new ArrayList<>();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_STATEMENTS.getProperty("mysql.users.select.all"));
            while (resultSet.next()) {
                User user = new User();
                setUser(resultSet, user);
                users.add(user);
            }
            LOGGER.info("All users were successfully loaded.");
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new DBException(e.getMessage(), e);
        } finally {
            DBUtils.release(resultSet, statement);
        }
        return users;
    }

    @Override
    public void update(Connection connection, User user) throws DBException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.users.update"));
            prepareStatement(preparedStatement, user);
            preparedStatement.setInt(7, user.getId());
            preparedStatement.execute();
            LOGGER.info(String.format("User %s %s successfully updated.",
                    user.getFirstName(), user.getLastName()));
        } catch (SQLException e) {
            LOGGER.info("Could not update user " + user.getFirstName() + " " + user.getLastName());
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(preparedStatement);
        }
    }

    @Override
    public void delete(Connection connection, int id) throws DBException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.users.delete"));
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            LOGGER.info(String.format("User with id = %d successfully deleted.", id));
        } catch (SQLException e) {
            LOGGER.info("Could not delete user with id " + id);
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(preparedStatement);
        }
    }

    @Override
    public int insert(Connection connection, User user) throws DBException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int id;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.users.insert"), Statement.RETURN_GENERATED_KEYS);
            prepareStatement(preparedStatement, user);
            preparedStatement.execute();
            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            id = resultSet.getInt(1);
            LOGGER.info(String.format("User %s %s successfully inserted (id = %d).",
                    user.getFirstName(), user.getLastName(), id));
        } catch (SQLException e) {
            LOGGER.info("Could not insert user " + user.getFirstName() + " " + user.getLastName());
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
        return id;
    }

    @Override
    public Optional<User> get(Connection connection, int id) throws DBException {
        User user = new User();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.users.select.one"));
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            setUser(resultSet, user);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
        return Optional.of(user);
    }

    private void prepareStatement(PreparedStatement preparedStatement, User user) throws SQLException {
        preparedStatement.setString(1, user.getFirstName());
        preparedStatement.setString(2, user.getLastName());
        preparedStatement.setString(3, user.getEmail());
        preparedStatement.setString(4, user.getPassword());
        preparedStatement.setBoolean(5, user.getIsAdmin());
        preparedStatement.setBoolean(6, user.getIsBlocked());
    }

    private void setUser(ResultSet resultSet, User user) throws SQLException {
        user.setId(resultSet.getInt("id"));
        user.setFirstName(resultSet.getString("first_name"));
        user.setLastName(resultSet.getString("last_name"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        user.setAdmin(resultSet.getBoolean("is_admin"));
        user.setBlocked(resultSet.getBoolean("is_blocked"));
    }
}
