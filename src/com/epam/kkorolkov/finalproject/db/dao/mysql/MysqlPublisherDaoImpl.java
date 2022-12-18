package com.epam.kkorolkov.finalproject.db.dao.mysql;

import com.epam.kkorolkov.finalproject.db.dao.PublisherDao;
import com.epam.kkorolkov.finalproject.db.entity.Publisher;
import com.epam.kkorolkov.finalproject.exception.DbException;
import com.epam.kkorolkov.finalproject.util.CatalogueUtils;
import com.epam.kkorolkov.finalproject.util.DBUtils;

import java.sql.*;
import java.util.*;

/**
 *  Provides MySQL specific implementation of {@link PublisherDao}.
 */
public class MysqlPublisherDaoImpl extends MysqlAbstractDao implements PublisherDao {
    /** SQL statements */
    private static final String SQL_COUNT = SQL_STATEMENTS.getProperty("mysql.publishers.select.count");
    private static final String SQL_GET_ID = SQL_STATEMENTS.getProperty("mysql.publishers.select.one");
    private static final String SQL_GET_TAG = SQL_STATEMENTS.getProperty("mysql.publishers.select.one.by.tag");
    private static final String SQL_GET_ALL = SQL_STATEMENTS.getProperty("mysql.publishers.select.all");
    private static final String SQL_UPDATE = SQL_STATEMENTS.getProperty("mysql.publishers.update");
    private static final String SQL_DELETE = SQL_STATEMENTS.getProperty("mysql.publishers.delete");
    private static final String SQL_INSERT = SQL_STATEMENTS.getProperty("mysql.publishers.insert");
    private static final String SQL_GET_DETAILS = SQL_STATEMENTS.getProperty("mysql.publishers.descriptions.select");
    private static final String SQL_INSERT_DETAILS = SQL_STATEMENTS.getProperty("mysql.publishers.descriptions.insert");
    private static final String SQL_UPDATE_DETAILS = SQL_STATEMENTS.getProperty("mysql.publishers.descriptions.update");

    /** Logger success messages */
    private static final String MESSAGE_PUBLISHERS_COUNTED = "There are %d publishers in the table.";
    private static final String MESSAGE_PUBLISHERS_LOADED = "All publishers were successfully loaded.";
    private static final String MESSAGE_PUBLISHER_ID_FOUND = "Publisher was found with id = ";
    private static final String MESSAGE_PUBLISHER_ID_NOT_FOUND = "No publisher found with id = ";
    private static final String MESSAGE_PUBLISHER_TAG_FOUND = "Publisher with tag = %s is successfully found.";
    private static final String MESSAGE_PUBLISHER_TAG_NOT_FOUND = "Publisher with tag = %s is not found.";
    private static final String MESSAGE_PUBLISHER_UPDATED = "Publisher with id = %d successfully updated.";
    private static final String MESSAGE_PUBLISHER_DELETED = "Publisher with id = %d successfully deleted.";
    private static final String MESSAGE_PUBLISHER_INSERTED = "Publisher with id = %d successfully inserted.";

    /** Logger error messages */
    private static final String ERROR_PUBLISHERS_NOT_COUNTED = "Could not count publishers.";
    private static final String ERROR_PUBLISHER_ID_NOT_LOADED = "Could not load publisher with id = ";
    private static final String ERROR_PUBLISHER_TAG_NOT_LOADED = "Could not load publisher with tag = ";
    private static final String ERROR_PUBLISHERS_NOT_LOADED = "Could not load publishers.";
    private static final String ERROR_PUBLISHER_NOT_UPDATED = "Could not update publisher with id=";
    private static final String ERROR_PUBLISHER_NOT_DELETED = "Could not delete publisher with id ";
    private static final String ERROR_PUBLISHER_NOT_INSERTED = "Could not insert publisher ";
    private static final String ERROR_PUBLISHER_DETAILS_NOT_LOADED = "Could not load publisher details.";

    /** Table fields */
    private static final String FIELD_ID = "id";
    private static final String FIELD_TAG = "tag";
    private static final String FIELD_LANGUAGE = "language_id";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_DESCRIPTION = "description";

    /**
     * MySQL specific realization of {@link PublisherDao#count(Connection)} method.
     * @return number of rows in the table <i>publishers</i>.
     *
     * @param connection an instance of {@link Connection} to reach the database.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    @Override
    public int count(Connection connection) throws DbException {
        int c;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_COUNT);
            resultSet.next();
            c = resultSet.getInt(1);
            LOGGER.info(String.format(MESSAGE_PUBLISHERS_COUNTED, c));
        } catch (SQLException e) {
            LOGGER.info(ERROR_PUBLISHERS_NOT_COUNTED);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, statement);
        }
        return c;
    }

    /**
     * MySQL specific realization of {@link PublisherDao#get(Connection, int)} method.
     * Retrieves a row from the table <i>publishers</i>
     * by the specified {@code id}.
     * If table does not contain a row with specified {@code id}
     * returns empty {@link Optional}.
     *
     * @param connection an instance of {@link Connection} to reach the database.
     * @param id id of a publisher to find.
     *
     * @return {@link Optional} representing a row from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    @Override
    public Optional<Publisher> get(Connection connection, int id) throws DbException {
        Optional<Publisher> optional = Optional.empty();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_GET_ID);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Publisher publisher = new Publisher();
                publisher.setId(id);
                publisher.setTag(resultSet.getString(FIELD_TAG));
                publisher.setNames(new HashMap<>());
                publisher.setDescriptions(new HashMap<>());
                setPublisherDetails(connection, publisher);
                optional = Optional.of(publisher);
            }
            if (optional.isEmpty()) {
                LOGGER.info(MESSAGE_PUBLISHER_ID_NOT_FOUND + id);
            } else {
                LOGGER.info(MESSAGE_PUBLISHER_ID_FOUND + id);
            }
        } catch (SQLException e) {
            LOGGER.info(ERROR_PUBLISHER_ID_NOT_LOADED + id);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
        return optional;
    }

    /**
     * MySQL specific realization of {@link PublisherDao#get(Connection, String)} method.
     * Retrieves a row from the table <i>publishers</i>
     * by the specified {@code tag}.
     * If table does not contain a row with specified {@code tag}
     * returns empty {@link Optional}.
     *
     * @param connection an instance of {@link Connection} to reach the database.
     * @param tag tag of a publisher to find.
     *
     * @return {@link Optional} representing a row from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    @Override
    public Optional<Publisher> get(Connection connection, String tag) throws DbException {
        Optional<Publisher> optional = Optional.empty();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_GET_TAG);
            preparedStatement.setString(1, tag);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Publisher publisher = new Publisher();
                publisher.setId(resultSet.getInt(FIELD_ID));
                publisher.setTag(tag);
                publisher.setNames(new HashMap<>());
                publisher.setDescriptions(new HashMap<>());
                setPublisherDetails(connection, publisher);
                optional = Optional.of(publisher);
            }
            if (optional.isEmpty()) {
                LOGGER.info(String.format(MESSAGE_PUBLISHER_TAG_FOUND, tag));
            } else {
                LOGGER.info(String.format(MESSAGE_PUBLISHER_TAG_NOT_FOUND, tag));
            }
        } catch (SQLException e) {
            LOGGER.info(ERROR_PUBLISHER_TAG_NOT_LOADED + tag);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
        return optional;
    }

    /**
     * MySQL specific realization of {@link PublisherDao#getAll(Connection)} method.
     * Retrieves all rows from the table <i>publishers</i>
     *
     * @param connection an instance of {@link Connection} to reach the database.
     *
     * @return {@link List} representing all rows from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    @Override
    public List<Publisher> getAll(Connection connection) throws DbException {
        List<Publisher> publishers = new ArrayList<>();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_GET_ALL);
            while (resultSet.next()) {
                Publisher publisher = new Publisher();
                publisher.setId(resultSet.getInt(FIELD_ID));
                publisher.setTag(resultSet.getString(FIELD_TAG));
                publisher.setNames(new HashMap<>());
                publisher.setDescriptions(new HashMap<>());
                setPublisherDetails(connection, publisher);
                publishers.add(publisher);
            }
            LOGGER.info(MESSAGE_PUBLISHERS_LOADED);
        } catch (SQLException e) {
            LOGGER.info(ERROR_PUBLISHERS_NOT_LOADED);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, statement);
        }
        return publishers;
    }

    /**
     * MySQL specific realization of {@link PublisherDao#update(Connection, Publisher)} method.
     * Updates a row which represents a publisher in the table <i>publishers</i>.
     *
     * @param connection an instance of {@link Connection} to reach the database.
     * @param publisher a publisher to be updated.
     *
     * @throws DbException is thrown if data cannot be updated.
     */
    @Override
    public void update(Connection connection, Publisher publisher) throws DbException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_UPDATE);
            preparedStatement.setString(1, publisher.getTag());
            preparedStatement.setInt(2, publisher.getId());
            preparedStatement.execute();
            CatalogueUtils.processCatalogueEntityDetails(connection, publisher, SQL_UPDATE_DETAILS);
            LOGGER.info(String.format(MESSAGE_PUBLISHER_UPDATED, publisher.getId()));
        } catch (SQLException e) {
            LOGGER.info(ERROR_PUBLISHER_NOT_UPDATED + publisher.getId());
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(preparedStatement);
        }
    }

    /**
     * MySQL specific realization of {@link PublisherDao#delete(Connection, int)} method.
     * Deletes a row which represents a publisher in the table <i>publishers</i>.
     *
     * @param connection an instance of {@link Connection} to reach the database.
     * @param id id of a publisher to be deleted.
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
            LOGGER.info(String.format(MESSAGE_PUBLISHER_DELETED, id));
        } catch (SQLException e) {
            LOGGER.info(ERROR_PUBLISHER_NOT_DELETED + id);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(preparedStatement);
        }
    }

    /**
     * MySQL specific realization of {@link PublisherDao#insert(Connection, Publisher)} method.
     * Method {@code insert} inserts a row to the table <i>publishers</i> with data specified in publisher.
     *
     * @param connection an instance of {@link Connection} to reach the database.
     * @param publisher a publisher to be inserted.
     *
     * @throws DbException is thrown if data cannot be deleted.
     */
    @Override
    public int insert(Connection connection, Publisher publisher) throws DbException {
        int id;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, publisher.getTag());
            preparedStatement.execute();
            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            id = resultSet.getInt(1);
            publisher.setId(id);
            CatalogueUtils.processCatalogueEntityDetails(connection, publisher, SQL_INSERT_DETAILS);
            LOGGER.info(String.format(MESSAGE_PUBLISHER_INSERTED, id));
            return id;
        } catch (SQLException e) {
            LOGGER.info(ERROR_PUBLISHER_NOT_INSERTED + publisher.getTag());
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
    }

    /**
     * MySQL specific method which retrieves {@code names} and {@code descriptions}
     * from the table <i>publisher_descriptions</i> and sets them to
     * the corresponding instance of a {@link Publisher} for all languages.
     *
     * @param connection an instance of {@link Connection} to reach the database.
     * @param publisher an instance of {@link Publisher} to set
     *                  {@code names} and {@code descriptions} to.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    private void setPublisherDetails(Connection connection, Publisher publisher) throws DbException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_GET_DETAILS);
            preparedStatement.setInt(1, publisher.getId());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                publisher.getNames().put(resultSet.getInt(FIELD_LANGUAGE), resultSet.getString(FIELD_NAME));
                publisher.getDescriptions().put(resultSet.getInt(FIELD_LANGUAGE), resultSet.getString(FIELD_DESCRIPTION));
            }
        } catch (SQLException e) {
            LOGGER.info(ERROR_PUBLISHER_DETAILS_NOT_LOADED);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
    }
}
