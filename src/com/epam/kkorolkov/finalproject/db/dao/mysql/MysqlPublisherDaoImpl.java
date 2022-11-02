package com.epam.kkorolkov.finalproject.db.dao.mysql;

import com.epam.kkorolkov.finalproject.db.dao.PublisherDao;
import com.epam.kkorolkov.finalproject.db.entity.Publisher;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.util.CatalogueUtils;
import com.epam.kkorolkov.finalproject.util.DBUtils;

import java.sql.*;
import java.util.*;

public class MysqlPublisherDaoImpl extends MysqlAbstractDao implements PublisherDao {
    @Override
    public int count(Connection connection) throws DBException {
        int c;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_STATEMENTS.getProperty("mysql.publishers.select.count"));
            resultSet.next();
            c = resultSet.getInt(1);
            LOGGER.info(String.format("There are %d publishers in the table.", c));
        } catch (SQLException e) {
            LOGGER.info("Could not count publishers.");
            LOGGER.error(e.getMessage());
            throw new DBException();
        } finally {
            DBUtils.release(resultSet, statement);
        }
        return c;
    }

    @Override
    public Optional<Publisher> get(Connection connection, int id) throws DBException {
        Optional<Publisher> optional = Optional.empty();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.publishers.select.one"));
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Publisher publisher = new Publisher();
                publisher.setId(id);
                publisher.setTag(resultSet.getString("tag"));
                publisher.setNames(new HashMap<>());
                publisher.setDescriptions(new HashMap<>());
                getPublisherDetails(connection, publisher);
                optional = Optional.of(publisher);
            }
            if (optional.isEmpty()) {
                LOGGER.info("No publisher found with id = " + id);
            } else {
                LOGGER.info("Publisher was found with id = " + id);
            }
        } catch (SQLException e) {
            LOGGER.info("Could not load publisher with id = " + id);
            LOGGER.error(e.getMessage());
            throw new DBException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
        return optional;
    }

    @Override
    public Optional<Publisher> get(Connection connection, String tag) throws DBException {
        Optional<Publisher> optional = Optional.empty();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.publishers.select.one.by.tag"));
            preparedStatement.setString(1, tag);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Publisher publisher = new Publisher();
                publisher.setId(resultSet.getInt("id"));
                publisher.setTag(tag);
                publisher.setNames(new HashMap<>());
                publisher.setDescriptions(new HashMap<>());
                getPublisherDetails(connection, publisher);
                optional = Optional.of(publisher);
            }
        } catch (SQLException e) {
            LOGGER.info("Could not load publisher with tag = " + tag);
            LOGGER.error(e.getMessage());
            throw new DBException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
        return optional;
    }

    @Override
    public List<Publisher> getAll(Connection connection) throws DBException {
        List<Publisher> publishers = new ArrayList<>();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_STATEMENTS.getProperty("mysql.publishers.select.all"));
            while (resultSet.next()) {
                Publisher publisher = new Publisher();
                publisher.setId(resultSet.getInt("id"));
                publisher.setTag(resultSet.getString("tag"));
                publisher.setNames(new HashMap<>());
                publisher.setDescriptions(new HashMap<>());
                getPublisherDetails(connection, publisher);
                publishers.add(publisher);
            }
            LOGGER.info("All publishers were successfully loaded.");
        } catch (SQLException e) {
            LOGGER.info("Could not load publishers.");
            LOGGER.error(e.getMessage());
            throw new DBException();
        } finally {
            DBUtils.release(resultSet, statement);
        }
        return publishers;
    }

    @Override
    public void update(Connection connection, Publisher publisher) throws DBException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(
                    SQL_STATEMENTS.getProperty("mysql.publishers.update"));
            preparedStatement.setString(1, publisher.getTag());
            preparedStatement.setInt(2, publisher.getId());
            preparedStatement.execute();
            updatePublisherDetails(connection, publisher);
        } catch (SQLException e) {
            LOGGER.info("Could not update publisher with id=" + publisher.getId());
            LOGGER.error(e.getMessage());
            throw new DBException();
        } finally {
            DBUtils.release(preparedStatement);
        }
    }

    private void updatePublisherDetails(Connection connection, Publisher publisher) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(
                    SQL_STATEMENTS.getProperty("mysql.publishers.descriptions.update"));
            CatalogueUtils.setDetailsFromEntity(preparedStatement, publisher);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            DBUtils.release(preparedStatement);
            DBUtils.release(connection);
        }
    }

    @Override
    public void delete(Connection connection, int id) throws DBException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.publishers.delete"));
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            LOGGER.info(String.format("Publisher with id = %d successfully deleted.", id));
        } catch (SQLException e) {
            LOGGER.info("Could not delete publisher with id " + id);
            LOGGER.error(e.getMessage());
            throw new DBException();
        } finally {
            DBUtils.release(preparedStatement);
        }
    }

    @Override
    public int insert(Connection connection, Publisher publisher) throws DBException {
        int id;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(
                    SQL_STATEMENTS.getProperty("mysql.publishers.insert"),
                    Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, publisher.getTag());
            preparedStatement.execute();
            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            id = resultSet.getInt(1);
            publisher.setId(id);
            insertPublisherDetails(connection, publisher);
        } catch (SQLException e) {
            LOGGER.info("Could not insert publisher " + publisher.getTag());
            LOGGER.error(e.getMessage());
            throw new DBException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
        return id;
    }

    private void insertPublisherDetails(Connection connection, Publisher publisher) throws SQLException {
        if (publisher.getId() != 0) {
            PreparedStatement preparedStatement = null;
            try {
                connection.setAutoCommit(false);
                preparedStatement = connection.prepareStatement(
                        SQL_STATEMENTS.getProperty("mysql.publishers.descriptions.insert"));
                CatalogueUtils.setDetailsFromEntity(preparedStatement, publisher);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                DBUtils.release(connection);
                DBUtils.release(preparedStatement);
            }
        }
    }

    private void getPublisherDetails(Connection connection, Publisher publisher) throws DBException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.publishers.descriptions.select"));
            preparedStatement.setInt(1, publisher.getId());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                publisher.getNames().put(resultSet.getInt("language_id"), resultSet.getString("name"));
                publisher.getDescriptions().put(resultSet.getInt("language_id"), resultSet.getString("description"));
            }
        } catch (SQLException e) {
            LOGGER.info("Could not load publisher details.");
            LOGGER.error(e.getMessage());
            throw new DBException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
    }
}
