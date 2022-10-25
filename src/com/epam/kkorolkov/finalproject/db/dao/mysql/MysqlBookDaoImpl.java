package com.epam.kkorolkov.finalproject.db.dao.mysql;

import com.epam.kkorolkov.finalproject.db.dao.*;
import com.epam.kkorolkov.finalproject.db.entity.Book;
import com.epam.kkorolkov.finalproject.exception.BadRequestException;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.util.DBUtils;

import java.sql.*;
import java.util.*;


public class MysqlBookDaoImpl extends MysqlAbstractDao implements BookDao {
    @Override
    public int count(Connection connection, Map<String, String> parameters) throws DBException, BadRequestException {
        String query = SQL_STATEMENTS.getProperty("mysql.books.select.count");
        query += setClause(parameters);
        return count(connection, query);
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
        return optional;
    }

    @Override
    public List<Book> getAll(Connection connection, int limit, int offset, Map<String, String> parameters) throws DBException {
        PreparedStatement preparedStatement = null;
        String query = SQL_STATEMENTS.getProperty("mysql.books.select.all") + setClause(parameters) + " limit ? offset ?";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, limit);
            preparedStatement.setInt(2, offset);
            return getAll(connection, preparedStatement);
        } catch (SQLException e) {
            LOGGER.info("Could not load books.");
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(preparedStatement);
        }
    }

    @Override
    public void update(Connection connection, Book book) throws DBException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.books.update"));
            prepareStatement(book, preparedStatement);
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
            prepareStatement(book, preparedStatement);
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

    @Override
    public int getMaxPrice(Connection connection, Map<String, String> parameters) throws DBException, BadRequestException {
        String query = SQL_STATEMENTS.getProperty("mysql.books.select.max.price");
        query += setClause(parameters);
        int maxPrice = getPrice(connection, query);
        LOGGER.info("Max price is " + maxPrice);
        return maxPrice;    }

    @Override
    public int getMinPrice(Connection connection, Map<String, String> parameters) throws DBException, BadRequestException {
        String query = SQL_STATEMENTS.getProperty("mysql.books.select.min.price");
        query += setClause(parameters);
        int minPrice = getPrice(connection, query);
        LOGGER.info("Min price is " + minPrice);
        return minPrice;
    }

    private int getPrice(Connection connection, String query) throws DBException, BadRequestException {
        int price;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            resultSet.next();
            price = resultSet.getInt(1);
        } catch (SQLSyntaxErrorException e) {
            LOGGER.info("Could not find price.");
            LOGGER.error(e.getMessage());
            throw new BadRequestException(e);
        } catch (SQLException e) {
            LOGGER.info("Could not find price.");
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(resultSet, statement);
        }
        return price;
    }

    private int count(Connection connection, String query) throws DBException, BadRequestException {
        int c;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            resultSet.next();
            c = resultSet.getInt(1);
            LOGGER.info(String.format("Found %d books.", c));
        } catch (SQLSyntaxErrorException e) {
            LOGGER.info("Could not count books.");
            LOGGER.error(e.getMessage());
            throw new BadRequestException(e);
        } catch (SQLException e) {
            LOGGER.info("Could not count books.");
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(resultSet, statement);
        }
        return c;
    }

    private void prepareStatement(Book book, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, book.getTag());
        preparedStatement.setString(2, book.getIsbn());
        preparedStatement.setInt(3, book.getQuantity());
        preparedStatement.setInt(4, book.getPublisher().getId());
        preparedStatement.setInt(5, book.getCategory().getId());
        preparedStatement.setDouble(6, book.getPrice());
        preparedStatement.setDate(7, book.getDate());
    }

    private List<Book> getAll(Connection connection, PreparedStatement preparedStatement) throws DBException {
        List<Book> books = new ArrayList<>();
        ResultSet resultSet = null;
        try {
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book();
                setBook(connection, resultSet, book);
                setBookDetails(connection, book);
                books.add(book);
            }
            LOGGER.info("Books were successfully loaded.");
        } catch (SQLException e) {
            LOGGER.info("Could not load books.");
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
        return books;
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
        book.setNames(new HashMap<>());
        book.setDescriptions(new HashMap<>());
    }

    private void setBookDetails(Connection connection, Book book) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.books.descriptions.select"));
            preparedStatement.setInt(1, book.getId());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                book.getNames().put(resultSet.getInt("language_id"), resultSet.getString("title"));
                book.getDescriptions().put(resultSet.getInt("language_id"), resultSet.getString("description"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new SQLException(e);
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
    }

    private void insertBookDetails(Connection connection, Book book) throws SQLException {
        if (book.getId() != 0) {
            try {
                connection.setAutoCommit(false);
                PreparedStatement preparedStatement = connection.prepareStatement(
                        SQL_STATEMENTS.getProperty("mysql.books.descriptions.insert"));
                for (int languageId : book.getDescriptions().keySet()) {

                    preparedStatement.setInt(1, book.getId());
                    preparedStatement.setInt(2, languageId);
                    preparedStatement.setString(3, book.getNames().get(languageId));
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

    private void updateBooksDetails(Connection connection, Book book) throws SQLException {
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    SQL_STATEMENTS.getProperty("mysql.books.descriptions.update"));
            for (int languageId : book.getDescriptions().keySet()) {
                preparedStatement.setInt(3, book.getId());
                preparedStatement.setInt(4, languageId);
                preparedStatement.setString(1, book.getNames().get(languageId));
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

    private String setClause(Map<String, String> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return "";
        }

        String categoryId = parameters.get("category_id");
        String publisherId = parameters.get("publisher_id");
        String tag = parameters.get("tag");
        String isbn = parameters.get("isbn");
        String sortBy = parameters.get("sort_by");
        String sortType = parameters.get("sort_type");
        String priceMin = parameters.get("price_min");
        String priceMax = parameters.get("price_max");
        final boolean isPricesSet = priceMin != null && priceMax != null && !"".equals(priceMin) && !"".equals(priceMax);
        StringBuilder stringBuilder = new StringBuilder("");
        if ((categoryId != null && !"".equals(categoryId)) ||
                (publisherId != null && !"".equals(publisherId)) ||
                (tag != null && !"".equals(tag)) ||
                (isbn != null && !"".equals(isbn)) ||
                isPricesSet) {
            stringBuilder.append(" where ");
            List<String> parts = new ArrayList<>();
            if (categoryId != null && !"".equals(categoryId)) {
                parts.add("category_id = " + categoryId);
            }
            if (publisherId != null && !"".equals(publisherId)) {
                parts.add("publisher_id = " + publisherId);
            }
            if (tag != null && !"".equals(tag)) {
                parts.add("tag like '%" + tag + "%'");
            }
            if (isbn != null && !"".equals(isbn)) {
                parts.add("isbn like '%" + isbn + "%'");
            }
            if (isPricesSet) {
                parts.add("price >= " + priceMin + " and price <= " + priceMax);
            }
            stringBuilder.append(String.join(" and ", parts));
        }
        if (sortBy != null && sortType != null) {
            stringBuilder.append(" order by ").append(sortBy).append(" ").append(sortType);
        }
        return stringBuilder.toString();
    }
}
