package com.epam.kkorolkov.finalproject.db.dao.mysql;

import com.epam.kkorolkov.finalproject.db.dao.CategoryDao;
import com.epam.kkorolkov.finalproject.db.entity.Category;
import com.epam.kkorolkov.finalproject.exception.DbException;
import com.epam.kkorolkov.finalproject.util.CatalogueUtils;
import com.epam.kkorolkov.finalproject.util.DBUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 *  Provides MySQL specific implementation of {@link CategoryDao}.
 */
public class MysqlCategoryDaoImpl extends MysqlAbstractDao implements CategoryDao {
    /** SQL statements */
    private static final String SQL_COUNT = SQL_STATEMENTS.getProperty("mysql.categories.select.count");
    private static final String SQL_GET_ID = SQL_STATEMENTS.getProperty("mysql.categories.select.one");
    private static final String SQL_GET_TAG = SQL_STATEMENTS.getProperty("mysql.categories.select.one.by.tag");
    private static final String SQL_GET_ALL = SQL_STATEMENTS.getProperty("mysql.categories.select.all");
    private static final String SQL_UPDATE = SQL_STATEMENTS.getProperty("mysql.categories.update");
    private static final String SQL_DELETE = SQL_STATEMENTS.getProperty("mysql.categories.delete");
    private static final String SQL_INSERT = SQL_STATEMENTS.getProperty("mysql.categories.insert");
    private static final String SQL_GET_DETAILS = SQL_STATEMENTS.getProperty("mysql.categories.descriptions.select");
    private static final String SQL_INSERT_DETAILS = SQL_STATEMENTS.getProperty("mysql.categories.descriptions.insert");
    private static final String SQL_UPDATE_DETAILS = SQL_STATEMENTS.getProperty("mysql.categories.descriptions.update");

    /** Logger success messages */
    private static final String MESSAGE_CATEGORIES_COUNTED = "There are %d categories in the table.";
    private static final String MESSAGE_CATEGORIES_LOADED = "All categories were successfully loaded.";
    private static final String MESSAGE_CATEGORY_ID_FOUND = "Category was found with id = ";
    private static final String MESSAGE_CATEGORY_ID_NOT_FOUND = "No category found with id = ";
    private static final String MESSAGE_CATEGORY_TAG_FOUND = "Category with tag = %s is successfully found.";
    private static final String MESSAGE_CATEGORY_TAG_NOT_FOUND = "Category with tag = %s is not found.";
    private static final String MESSAGE_CATEGORY_UPDATED = "Category with id = %d successfully updated.";
    private static final String MESSAGE_CATEGORY_DELETED = "Category with id = %d successfully deleted.";
    private static final String MESSAGE_CATEGORY_INSERTED = "Category with id = %d successfully inserted.";

    /** Logger error messages */
    private static final String ERROR_CATEGORIES_NOT_COUNTED = "Could not count categories.";
    private static final String ERROR_CATEGORY_ID_NOT_LOADED = "Could not load category with id = ";
    private static final String ERROR_CATEGORY_TAG_NOT_LOADED = "Could not load category with tag = ";
    private static final String ERROR_CATEGORIES_NOT_LOADED = "Could not load categories.";
    private static final String ERROR_CATEGORY_NOT_UPDATED = "Could not update category with id=";
    private static final String ERROR_CATEGORY_NOT_DELETED = "Could not delete category with id ";
    private static final String ERROR_CATEGORY_NOT_INSERTED = "Could not insert category ";
    private static final String ERROR_CATEGORY_DETAILS_NOT_LOADED = "Could not load category details.";

    /** Table fields */
    private static final String FIELD_ID = "id";
    private static final String FIELD_TAG = "tag";
    private static final String FIELD_LANGUAGE = "language_id";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_DESCRIPTION = "description";

    /**
     * MySQL specific realization of {@link CategoryDao#count(Connection)} method.
     * @return number of rows in the table <i>categories</i>.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
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
            LOGGER.info(String.format(MESSAGE_CATEGORIES_COUNTED, c));
            return c;
        } catch (SQLException e) {
            LOGGER.info(ERROR_CATEGORIES_NOT_COUNTED);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, statement);
        }
    }

    /**
     * MySQL specific realization of {@link CategoryDao#get(Connection, int)} method.
     * Retrieves a row from the table <i>categories</i>
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
    @Override
    public Optional<Category> get(Connection connection, int id) throws DbException {
        Optional<Category> optional = Optional.empty();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_GET_ID);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Category category = new Category();
                category.setId(id);
                category.setTag(resultSet.getString(FIELD_TAG));
                category.setNames(new HashMap<>());
                category.setDescriptions(new HashMap<>());
                setCategoryDetails(connection, category);
                optional = Optional.of(category);
                LOGGER.info(MESSAGE_CATEGORY_ID_FOUND + id);
            } else {
                LOGGER.info(MESSAGE_CATEGORY_ID_NOT_FOUND + id);
            }
            return optional;
        } catch (SQLException e) {
            LOGGER.info(ERROR_CATEGORY_ID_NOT_LOADED + id);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
    }

    /**
     * MySQL specific realization of {@link CategoryDao#get(Connection, String)} method.
     * Retrieves a row from the table <i>categories</i>
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
    @Override
    public Optional<Category> get(Connection connection, String tag) throws DbException {
        Optional<Category> optional = Optional.empty();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_GET_TAG);
            preparedStatement.setString(1, tag);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Category category = new Category();
                category.setId(resultSet.getInt(FIELD_ID));
                category.setTag(tag);
                category.setNames(new HashMap<>());
                category.setDescriptions(new HashMap<>());
                setCategoryDetails(connection, category);
                optional = Optional.of(category);
                LOGGER.info(String.format(MESSAGE_CATEGORY_TAG_FOUND, tag));
            } else {
                LOGGER.info(String.format(MESSAGE_CATEGORY_TAG_NOT_FOUND, tag));
            }
            return optional;
        } catch (SQLException e) {
            LOGGER.info(ERROR_CATEGORY_TAG_NOT_LOADED + tag);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }

    }

    /**
     * MySQL specific method which retrieves {@code names} and {@code descriptions}
     * from the table <i>category_descriptions</i> and sets them to
     * the corresponding instance of a {@code } for all languages.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param category - an instance of {@link Category} to set
     *                  {@code names} and {@code descriptions} to.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    private void setCategoryDetails(Connection connection, Category category) throws DbException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_GET_DETAILS);
            preparedStatement.setInt(1, category.getId());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                category.getNames().put(resultSet.getInt(FIELD_LANGUAGE), resultSet.getString(FIELD_NAME));
                category.getDescriptions().put(resultSet.getInt(FIELD_LANGUAGE), resultSet.getString(FIELD_DESCRIPTION));
            }
        } catch (SQLException e) {
            LOGGER.info(ERROR_CATEGORY_DETAILS_NOT_LOADED);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
    }


    /**
     * MySQL specific realization of {@link CategoryDao#getAll(Connection)} method.
     * Retrieves all rows from the table <i>categories</i>
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     *
     * @return {@link List<Category>} representing all rows from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    @Override
    public List<Category> getAll(Connection connection) throws DbException {
        List<Category> categories = new ArrayList<>();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_GET_ALL);
            while (resultSet.next()) {
                Category category = new Category();
                category.setId(resultSet.getInt(FIELD_ID));
                category.setTag(resultSet.getString(FIELD_TAG));
                category.setNames(new HashMap<>());
                category.setDescriptions(new HashMap<>());
                setCategoryDetails(connection, category);
                categories.add(category);
            }
            LOGGER.info(MESSAGE_CATEGORIES_LOADED);
            return categories;
        } catch (SQLException e) {
            LOGGER.info(ERROR_CATEGORIES_NOT_LOADED);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, statement);
        }
    }

    /**
     * MySQL specific realization of {@link CategoryDao#update(Connection, Category)} method.
     * Updates a row which represents a category in the table <i>categories</i>.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param category - a category to be updated.
     *
     * @throws DbException is thrown if data cannot be updated.
     */
    @Override
    public void update(Connection connection, Category category) throws DbException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_UPDATE);
            preparedStatement.setInt(2, category.getId());
            preparedStatement.setString(1, category.getTag());
            preparedStatement.execute();
            CatalogueUtils.processCatalogueEntityDetails(connection, category, SQL_UPDATE_DETAILS);
            LOGGER.info(String.format(MESSAGE_CATEGORY_UPDATED, category.getId()));
        } catch (SQLException e) {
            LOGGER.info(ERROR_CATEGORY_NOT_UPDATED + category.getId());
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(preparedStatement);
        }
    }

    /**
     * MySQL specific realization of {@link CategoryDao#delete(Connection, int)} method.
     * Deletes a row which represents a category in the table <i>categories</i>.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param id - id of a category to be deleted.
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
            LOGGER.info(String.format(MESSAGE_CATEGORY_DELETED, id));
        } catch (SQLException e) {
            LOGGER.info(ERROR_CATEGORY_NOT_DELETED + id);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(preparedStatement);
        }
    }

    /**
     * MySQL specific realization of {@link CategoryDao#insert(Connection, Category)} method.
     * Method {@code insert} inserts a row to the table <i>categories</i> with data specified in category.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param category - a category to be inserted.
     *
     * @throws DbException is thrown if data cannot be deleted.
     */
    @Override
    public int insert(Connection connection, Category category) throws DbException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, category.getTag());
            preparedStatement.execute();
            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            int id = resultSet.getInt(1);
            category.setId(id);
            CatalogueUtils.processCatalogueEntityDetails(connection, category, SQL_INSERT_DETAILS);
            LOGGER.info(String.format(MESSAGE_CATEGORY_INSERTED , id));
            return id;
        } catch (SQLException e) {
            LOGGER.info(ERROR_CATEGORY_NOT_INSERTED + category.getTag());
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
    }
}
