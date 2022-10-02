package com.epam.kkorolkov.finalproject.db.dao.mysql;

import com.epam.kkorolkov.finalproject.db.dao.*;
import com.epam.kkorolkov.finalproject.db.entity.Book;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.util.DBUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


public class MysqlBookDaoImpl extends MysqlAbstractDao implements BookDao {
    @Override
    public int count(Connection connection) throws DBException {
        int c;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_STATEMENTS.getProperty("mysql.books.select.count"));
            resultSet.next();
            c = resultSet.getInt(1);
            LOGGER.info(String.format("There are %d books in the table.", c));
        } catch (SQLException e) {
            LOGGER.info("Could not count books.");
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(resultSet, statement);
        }
        return c;
    }

    @Override
    public Optional<Book> get(Connection connection, int id) throws DBException {
        Optional<Book> optional = Optional.empty();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.books.select.by.id"));
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Book book = new Book();
                setBook(connection, resultSet, book);
                setBookDetails(connection, book);
                optional = Optional.of(book);
            }
            LOGGER.info("Book with id = " + id + " successfully loaded.");
        } catch (SQLException e) {
            LOGGER.info("Could not load book with id = " + id);
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
        return optional;
    }

    @Override
    public Optional<Book> get(Connection connection, String tag) throws DBException {
        Optional<Book> optional = Optional.empty();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.books.select.by.tag"));
            preparedStatement.setString(1, tag);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Book book = new Book();
                setBook(connection, resultSet, book);
                setBookDetails(connection, book);
                optional = Optional.of(book);
            }
            if (optional.isPresent()) {
                LOGGER.info("Book with tag = " + tag + " successfully loaded.");
            } else {
                LOGGER.info("Book with tag = " + tag + " was not found.");
            }
        } catch (SQLException e) {
            LOGGER.info("Could not load book with tag = " + tag);
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
        return optional;    }

    @Override
    public List<Book> getAll(Connection connection) throws DBException {
        List<Book> books = new ArrayList<>();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_STATEMENTS.getProperty("mysql.books.select.all"));
            while (resultSet.next()) {
                Book book = new Book();
                setBook(connection, resultSet, book);
                setBookDetails(connection, book);
                books.add(book);
            }
            LOGGER.info("All books were successfully loaded.");
        } catch (SQLException e) {
            LOGGER.info("Could not load books.");
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(resultSet, statement);
        }
        return books;
    }

    @Override
    public List<Book> getAll(Connection connection, int limit, int page) throws DBException {
        List<Book> books = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.books.select.all.with.limit"));
            preparedStatement.setInt(1, limit);
            preparedStatement.setInt(2, (page - 1) * limit);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book();
                setBook(connection, resultSet, book);
                setBookDetails(connection, book);
                books.add(book);
            }
            LOGGER.info(limit + " books were successfully loaded.");
        } catch (SQLException e) {
            LOGGER.info("Could not load books.");
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
        return books;
    }

    @Override
    public List<Book> getAll(Connection connection, int limit, int page, int categoryId, int publisherId) throws DBException {
        //TODO filtered select
        return null;
    }

    private void setBookDetails(Connection connection, Book book) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.books.descriptions.select"));
            preparedStatement.setInt(1, book.getId());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                book.getTitles().put(resultSet.getInt("language_id"), resultSet.getString("title"));
                book.getDescriptions().put(resultSet.getInt("language_id"), resultSet.getString("description"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new SQLException(e);
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
    }

    private void setBook(Connection connection, ResultSet resultSet, Book book) throws SQLException {
        PublisherDao publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
        CategoryDao categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();
        book.setId(resultSet.getInt("id"));
        book.setTag(resultSet.getString("tag"));
        book.setIsbn(resultSet.getString("isbn"));
        book.setDate(resultSet.getDate("publishing_date"));
        book.setQuantity(resultSet.getInt("quantity"));
        book.setPublisher(publisherDao.get(connection, resultSet.getInt("publisher_id")).get());
        book.setCategory(categoryDao.get(connection, resultSet.getInt("category_id")).get());
        book.setPrice(resultSet.getDouble("price"));
        book.setTitles(new HashMap<>());
        book.setDescriptions(new HashMap<>());
    }

    @Override
    public void update(Connection connection, Book book) throws DBException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.books.update"));
            preparedStatement.setString(1, book.getTag());
            preparedStatement.setString(2, book.getIsbn());
            preparedStatement.setInt(3, book.getQuantity());
            preparedStatement.setInt(4, book.getPublisher().getId());
            preparedStatement.setInt(5, book.getCategory().getId());
            preparedStatement.setDouble(6, book.getPrice());
            preparedStatement.setDate(7, book.getDate());
            preparedStatement.setInt(8, book.getId());
            preparedStatement.execute();
            updateBooksDetails(connection, book);
            LOGGER.info("Successfully updated book with id=" + book.getId());
        } catch (SQLException e) {
            LOGGER.info("Could not update book with id=" + book.getId());
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBUtils.release(preparedStatement);
        }
    }

    private void updateBooksDetails(Connection connection, Book book) throws SQLException {
        connection.setAutoCommit(false);
        try {
            for (int languageId : book.getDescriptions().keySet()) {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        SQL_STATEMENTS.getProperty("mysql.books.descriptions.update"));
                preparedStatement.setInt(3, book.getId());
                preparedStatement.setInt(4, languageId);
                preparedStatement.setString(1, book.getTitles().get(languageId));
                preparedStatement.setString(2, book.getDescriptions().get(languageId));
                preparedStatement.execute();
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            LOGGER.error(e.getMessage());
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void delete(Connection connection, int id) throws DBException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.books.delete"));
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            LOGGER.info(String.format("Book with id = %d successfully deleted.", id));
        } catch (SQLException e) {
            LOGGER.info("Could not delete book with id " + id);
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(preparedStatement);
        }
    }
    @Override
    public int insert(Connection connection, Book book) throws DBException {
        int id;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(
                    SQL_STATEMENTS.getProperty("mysql.books.insert"),
                    Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, book.getTag());
            preparedStatement.setString(2, book.getIsbn());
            preparedStatement.setInt(3, book.getQuantity());
            preparedStatement.setInt(4, book.getPublisher().getId());
            preparedStatement.setInt(5, book.getCategory().getId());
            preparedStatement.setDouble(6, book.getPrice());
            preparedStatement.setDate(7, book.getDate());
            preparedStatement.execute();
            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            id = resultSet.getInt(1);
            book.setId(id);
            insertBookDetails(connection, book);
            LOGGER.info(String.format("Book with id = %d successfully inserted.", id));
        } catch (SQLException e) {
            LOGGER.info("Could not insert book " + book.getTag());
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
        return id;
    }

    private void insertBookDetails(Connection connection, Book book) throws SQLException {
        if (book.getId() != 0) {
            try {
                connection.setAutoCommit(false);
                for (int languageId : book.getDescriptions().keySet()) {
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            SQL_STATEMENTS.getProperty("mysql.books.descriptions.insert"));
                    preparedStatement.setInt(1, book.getId());
                    preparedStatement.setInt(2, languageId);
                    preparedStatement.setString(3, book.getTitles().get(languageId));
                    preparedStatement.setString(4, book.getDescriptions().get(languageId));
                    preparedStatement.execute();
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                LOGGER.error(e.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }
}
