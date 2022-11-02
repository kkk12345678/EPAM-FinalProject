package com.epam.kkorolkov.finalproject.db.dao.mysql;

import com.epam.kkorolkov.finalproject.db.dao.CategoryDao;
import com.epam.kkorolkov.finalproject.db.entity.Category;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.util.CatalogueUtils;
import com.epam.kkorolkov.finalproject.util.DBUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MysqlCategoryDaoImpl extends MysqlAbstractDao implements CategoryDao {

    @Override
    public int count(Connection connection) throws DBException {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_STATEMENTS.getProperty("mysql.categories.select.count"));
            resultSet.next();
            int c = resultSet.getInt(1);
            LOGGER.info(String.format("There are %d categories in the table.", c));
            return c;
        } catch (SQLException e) {
            LOGGER.info("Could not count categories.");
            LOGGER.error(e.getMessage());
            throw new DBException();
        } finally {
            DBUtils.release(resultSet, statement);
        }
    }

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
                LOGGER.info("Category was found with id = " + id);
            } else {
                LOGGER.info("No category found with id = " + id);
            }
            return optional;
        } catch (SQLException e) {
            LOGGER.info("Could not load category with id = " + id);
            LOGGER.error(e.getMessage());
            throw new DBException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
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
                LOGGER.info("No category found with tag = " + tag);
            } else {
                LOGGER.info("Category was found with tag = " + tag);
            }
            return optional;
        } catch (SQLException e) {
            LOGGER.info("Could not load category with tag = " + tag);
            LOGGER.error(e.getMessage());
            throw new DBException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }

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
            LOGGER.info("Could not load category details.");
            LOGGER.error(e.getMessage());
            throw new DBException();
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
            return categories;
        } catch (SQLException e) {
            LOGGER.info("Could not load categories.");
            LOGGER.error(e.getMessage());
            throw new DBException();
        } finally {
            DBUtils.release(resultSet, statement);
        }
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
            throw new DBException();
        } finally {
            DBUtils.release(preparedStatement);
        }
    }

    private void updateCategoryDetails(Connection connection, Category category) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(
                    SQL_STATEMENTS.getProperty("mysql.categories.descriptions.update"));
            CatalogueUtils.setDetailsFromEntity(preparedStatement, category);
            connection.commit();
            LOGGER.info(String.format("Category details with id=%d successfully updated.", category.getId()));
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
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.categories.delete"));
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            LOGGER.info(String.format("Category with id = %d successfully deleted.", id));
        } catch (SQLException e) {
            LOGGER.info("Could not delete category with id " + id);
            LOGGER.error(e.getMessage());
            throw new DBException();
        } finally {
            DBUtils.release(preparedStatement);
        }
    }

    @Override
    public int insert(Connection connection, Category category) throws DBException {
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
            int id = resultSet.getInt(1);
            category.setId(id);
            try {
                insertCategoryDetails(connection, category);
            } catch (SQLException e) {
                LOGGER.info("Could not insert category details for category " + category.getTag());
                LOGGER.error(e.getMessage());
                throw new DBException();
            }
            LOGGER.info(String.format("Category with id = %d successfully inserted" , id));
            return id;
        } catch (SQLException e) {
            LOGGER.info("Could not insert category " + category.getTag());
            LOGGER.error(e.getMessage());
            throw new DBException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }

    }

    private void insertCategoryDetails(Connection connection, Category category) throws SQLException {
        if (category.getId() != 0) {
            PreparedStatement preparedStatement = null;
            try {
                connection.setAutoCommit(false);
                preparedStatement = connection.prepareStatement(
                        SQL_STATEMENTS.getProperty("mysql.categories.descriptions.insert"));
                CatalogueUtils.setDetailsFromEntity(preparedStatement, category);
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
}
