package com.epam.kkorolkov.finalproject.db.dao.mysql;

import com.epam.kkorolkov.finalproject.db.dao.CategoryDao;
import com.epam.kkorolkov.finalproject.db.entity.Category;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.utils.DBUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MysqlCategoryDaoImpl extends MysqlAbstractDao implements CategoryDao {

    @Override
    public Optional<Category> get(Connection connection, int id) throws DBException {
        Optional<Category> optional = Optional.empty();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.categories.select.one"));
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Category category = new Category();
                category.setId(id);
                category.setTag(resultSet.getString("tag"));
                category.setNames(new HashMap<>());
                category.setDescriptions(new HashMap<>());
                getCategoryDetails(connection, category);
                optional = Optional.of(category);
            }
        } catch (SQLException e) {
            LOGGER.info("Could not load category with id = " + id);
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
        return optional;
    }

    @Override
    public Optional<Category> get(Connection connection, String tag) throws DBException {
        Optional<Category> optional = Optional.empty();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.categories.select.one.by.tag"));
            preparedStatement.setString(1, tag);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Category category = new Category();
                category.setId(resultSet.getInt("id"));
                category.setTag(tag);
                category.setNames(new HashMap<>());
                category.setDescriptions(new HashMap<>());
                getCategoryDetails(connection, category);
                optional = Optional.of(category);
            }
        } catch (SQLException e) {
            LOGGER.info("Could not load category with tag = " + tag);
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
        return optional;
    }

    private void getCategoryDetails(Connection connection, Category category) throws DBException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.categories.descriptions.select"));
            preparedStatement.setInt(1, category.getId());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                category.getNames().put(resultSet.getInt("language_id"), resultSet.getString("name"));
                category.getDescriptions().put(resultSet.getInt("language_id"), resultSet.getString("description"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new DBException(e.getMessage(), e);
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
    }

    @Override
    public List<Category> getAll(Connection connection) throws DBException {
        List<Category> categories = new ArrayList<>();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_STATEMENTS.getProperty("mysql.categories.select.all"));
            while (resultSet.next()) {
                Category category = new Category();
                category.setId(resultSet.getInt("id"));
                category.setTag(resultSet.getString("tag"));
                category.setNames(new HashMap<>());
                category.setDescriptions(new HashMap<>());
                getCategoryDetails(connection, category);
                categories.add(category);
            }
            LOGGER.info("All categories were successfully loaded.");
        } catch (SQLException e) {
            LOGGER.info("Could not load categories.");
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(resultSet, statement);
        }
        return categories;
    }

    @Override
    public void update(Connection connection, Category category) throws DBException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(
                    SQL_STATEMENTS.getProperty("mysql.categories.update"));
            preparedStatement.setInt(2, category.getId());
            preparedStatement.setString(1, category.getTag());
            preparedStatement.execute();
            updateCategoryDetails(connection, category);
            LOGGER.info(String.format("Category with id=%d successfully updated.", category.getId()));
        } catch (SQLException e) {
            LOGGER.info("Could not update category with id=" + category.getId());
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(preparedStatement);
        }
    }

    private void updateCategoryDetails(Connection connection, Category category) throws SQLException {
        connection.setAutoCommit(false);
        PreparedStatement preparedStatement = null;
        try {
            for (int languageId : category.getDescriptions().keySet()) {
                preparedStatement = connection.prepareStatement(
                        SQL_STATEMENTS.getProperty("mysql.categories.descriptions.update"));
                preparedStatement.setInt(3, category.getId());
                preparedStatement.setInt(4, languageId);
                preparedStatement.setString(1, category.getNames().get(languageId));
                preparedStatement.setString(2, category.getDescriptions().get(languageId));
                preparedStatement.execute();
            }
            connection.commit();
            LOGGER.info(String.format("Category details with id=%d successfully updated.", category.getId()));
        } catch (SQLException e) {
            connection.rollback();
            LOGGER.error(e.getMessage());
        } finally {
            DBUtils.release(preparedStatement);
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void delete(Connection connection, int id) throws DBException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.categories.delete"));
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            LOGGER.info(String.format("Category with id = %d successfully deleted.", id));
        } catch (SQLException e) {
            LOGGER.info("Could not delete category with id " + id);
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(preparedStatement);
        }
    }

    @Override
    public int insert(Connection connection, Category category) throws DBException {
        int id;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(
                    SQL_STATEMENTS.getProperty("mysql.categories.insert"),
                    Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, category.getTag());
            preparedStatement.execute();
            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            id = resultSet.getInt(1);
            category.setId(id);
            try {
                insertCategoryDetails(connection, category);
            } catch (SQLException e) {
                LOGGER.info("Could not insert category details for category " + category.getTag());
                LOGGER.error(e.getMessage());
                throw new DBException(e);
            }
            LOGGER.info(String.format("Category with id = %d successfully inserted" , id));
        } catch (SQLException e) {
            LOGGER.info("Could not insert category " + category.getTag());
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
        return id;
    }

    private void insertCategoryDetails(Connection connection, Category category) throws SQLException {
        if (category.getId() != 0) {
            try {
                connection.setAutoCommit(false);
                for (int languageId : category.getDescriptions().keySet()) {
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            SQL_STATEMENTS.getProperty("mysql.categories.descriptions.insert"));
                    preparedStatement.setInt(1, category.getId());
                    preparedStatement.setInt(2, languageId);
                    preparedStatement.setString(3, category.getNames().get(languageId));
                    preparedStatement.setString(4, category.getDescriptions().get(languageId));
                    preparedStatement.execute();
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                LOGGER.error(e.getMessage());
                e.printStackTrace();
                throw new DBException();
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }
}
