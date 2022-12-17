package com.epam.kkorolkov.finalproject.db.dao.mysql;

import com.epam.kkorolkov.finalproject.db.dao.*;
import com.epam.kkorolkov.finalproject.db.entity.Book;
import com.epam.kkorolkov.finalproject.db.entity.Category;
import com.epam.kkorolkov.finalproject.db.entity.Publisher;
import com.epam.kkorolkov.finalproject.exception.BadRequestException;
import com.epam.kkorolkov.finalproject.exception.DbException;
import com.epam.kkorolkov.finalproject.exception.DaoException;
import com.epam.kkorolkov.finalproject.util.CatalogueUtils;
import com.epam.kkorolkov.finalproject.util.DBUtils;

import java.sql.*;
import java.util.*;

/**
 *  Provides MySQL specific implementation of {@link BookDao}.
 */
public class MysqlBookDaoImpl extends MysqlAbstractDao implements BookDao {
    /** SQL statements */
    private static final String SQL_COUNT = SQL_STATEMENTS.getProperty("mysql.books.select.count");
    private static final String SQL_GET_ID = SQL_STATEMENTS.getProperty("mysql.books.select.by.id");
    private static final String SQL_GET_TAG = SQL_STATEMENTS.getProperty("mysql.books.select.by.tag");
    private static final String SQL_GET_ALL = SQL_STATEMENTS.getProperty("mysql.books.select.all");
    private static final String SQL_UPDATE = SQL_STATEMENTS.getProperty("mysql.books.update");
    private static final String SQL_DELETE = SQL_STATEMENTS.getProperty("mysql.books.delete");
    private static final String SQL_INSERT = SQL_STATEMENTS.getProperty("mysql.books.insert");
    private static final String SQL_MAX_PRICE = SQL_STATEMENTS.getProperty("mysql.books.select.max.price");
    private static final String SQL_MIN_PRICE = SQL_STATEMENTS.getProperty("mysql.books.select.min.price");
    private static final String SQL_GET_DETAILS = SQL_STATEMENTS.getProperty("mysql.books.descriptions.select");
    private static final String SQL_INSERT_DETAILS = SQL_STATEMENTS.getProperty("mysql.books.descriptions.insert");
    private static final String SQL_UPDATE_DETAILS = SQL_STATEMENTS.getProperty("mysql.books.descriptions.update");

    /** Logger success messages */
    private static final String MESSAGE_BOOKS_COUNTED = "Found %d books.";
    private static final String MESSAGE_BOOKS_LOADED = "Books were successfully loaded.";
    private static final String MESSAGE_BOOK_ID_FOUND = "Book with id = %d is successfully found.";
    private static final String MESSAGE_BOOK_ID_NOT_FOUND = "Book with id = %d is not found.";
    private static final String MESSAGE_BOOK_TAG_FOUND = "Book with tag = %s is successfully found.";
    private static final String MESSAGE_BOOK_TAG_NOT_FOUND = "Book with tag = %s is not found.";
    private static final String MESSAGE_BOOK_UPDATED = "Successfully updated book with id=";
    private static final String MESSAGE_BOOK_DELETED = "Successfully deleted book with id=";
    private static final String MESSAGE_BOOK_INSERTED = "Successfully inserted book with id=";
    private static final String MESSAGE_MAX_PRICE = "Max price is ";
    private static final String MESSAGE_MIN_PRICE = "Min price is ";

    /** Logger error messages */
    private static final String ERROR_BOOKS_NOT_COUNTED = "Could not count books.";
    private static final String ERROR_BOOK_ID_NOT_LOADED = "Book with id = %d could not be retrieved.";
    private static final String ERROR_BOOK_TAG_NOT_LOADED = "Book with tag = %s could not be retrieved.";
    private static final String ERROR_BOOKS_NOT_LOADED = "Books could not be retrieved.";
    private static final String ERROR_BOOK_NOT_UPDATED = "Could not update book with id=";
    private static final String ERROR_BOOK_NOT_DELETED = "Could not delete book with id=";
    private static final String ERROR_BOOK_NOT_INSERTED = "Could not insert book ";
    private static final String ERROR_PRICE_NOT_LOADED = "Could not load price.";

    /** Table fields */
    private static final String FIELD_ID = "id";
    private static final String FIELD_TAG = "tag";
    private static final String FIELD_ISBN = "isbn";
    private static final String FIELD_DATE = "publishing_date";
    private static final String FIELD_QUANTITY = "quantity";
    private static final String FIELD_PUBLISHER_ID = "publisher_id";
    private static final String FIELD_CATEGORY_ID = "category_id";
    private static final String FIELD_PRICE = "price";
    private static final String FIELD_LANGUAGE_ID = "language_id";
    private static final String FIELD_TITlE = "title";
    private static final String FIELD_DESCRIPTION = "description";

    /** Request parameters */
    private static final String PARAM_CATEGORY = "category";
    private static final String PARAM_PUBLISHER = "publisher";
    private static final String PARAM_TAG = "tag";
    private static final String PARAM_ISBN = "isbn";
    private static final String PARAM_SORT_BY = "sort_by";
    private static final String PARAM_SORT_TYPE = "sort_type";
    private static final String PARAM_PRICE_MIN = "price_min";
    private static final String PARAM_PRICE_MAX = "price_max";

    /** Where clause parts */
    private static final String CLAUSE_WHERE = " where ";
    private static final String CLAUSE_CATEGORY = "category_id = ";
    private static final String CLAUSE_PUBLISHER = "publisher_id = ";
    private static final String CLAUSE_TAG = "tag like '%%%s%%'";
    private static final String CLAUSE_ISBN = "isbn like '%%%s%%'";
    private static final String CLAUSE_PRICE = "price >= %s and price <= %s";
    private static final String CLAUSE_AND = " and ";
    private static final String CLAUSE_ORDER = " order by ";

    /**
     * MySQL specific realization of {@link BookDao#count(Connection, Map)} method.
     *
     * @return number of rows in the table <i>books</i> with
     * where clause specified by the filter given in {@code parameters}.
     *
     * @param connection - instance of {@link Connection} to reach the database.
     * @param parameters - parameters of where clause.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     * @throws BadRequestException is thrown if some of parameters are invalid.
     */
    @Override
    public int count(Connection connection, Map<String, String> parameters) throws DbException, BadRequestException {
        return count(connection, SQL_COUNT + setClause(parameters));
    }

    /**
     * MySQL specific realization of {@link BookDao#get(Connection, int)} method.
     * Retrieves a row from the table <i>books</i> by the specified {@code id}.
     * If table does not contain a row with specified {@code id}
     * returns empty {@link Optional}.
     *
     * @param connection - instance of {@link Connection} to reach the database.
     * @param id - id of a book to find.
     *
     * @return {@link Optional<Book>} representing a row from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     * @throws DaoException is thrown if DAO cannot be instantiated.
     */
    @Override
    public Optional<Book> get(Connection connection, int id) throws DbException, DaoException {
        Optional<Book> optional = Optional.empty();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_GET_ID);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Book book = new Book();
                setBook(connection, resultSet, book);
                setBookDetails(connection, book);
                optional = Optional.of(book);
                LOGGER.info(String.format(MESSAGE_BOOK_ID_FOUND, id));
            } else {
                LOGGER.info(String.format(MESSAGE_BOOK_ID_NOT_FOUND, id));
            }
            return optional;
        } catch (SQLException e) {
            LOGGER.info(String.format(ERROR_BOOK_ID_NOT_LOADED, id));
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
    }

    /**
     * MySQL specific realization of {@link BookDao#get(Connection, String)} method.
     * Retrieves a row from the table <i>books</i> by the specified {@code tag}.
     * If table does not contain a row with specified {@code tag}
     * returns empty {@link Optional}.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param tag - tag of a book to find.
     *
     * @return {@link Optional<Book>} representing a row from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     * @throws DaoException is thrown if DAO cannot be instantiated.
     */
    @Override
    public Optional<Book> get(Connection connection, String tag) throws DbException, DaoException {
        Optional<Book> optional = Optional.empty();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_GET_TAG);
            preparedStatement.setString(1, tag);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Book book = new Book();
                setBook(connection, resultSet, book);
                setBookDetails(connection, book);
                LOGGER.info(String.format(MESSAGE_BOOK_TAG_FOUND, tag));
                optional = Optional.of(book);
            } else {
                LOGGER.info(String.format(MESSAGE_BOOK_TAG_NOT_FOUND, tag));
            }
            return optional;
        } catch (SQLException e) {
            LOGGER.info(String.format(ERROR_BOOK_TAG_NOT_LOADED, tag));
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
    }

    /**
     * MySQL specific realization of {@link BookDao#getAll(Connection, int, int, Map)} method.
     * Retrieves rows from the table <i>books</i> with specified limit, offset,
     * and where clause specified by the filter given in {@code parameters}.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param limit - number of rows to retrieve.
     * @param offset - number of a row to begin with.
     * @param parameters - parameters of where clause.
     *
     * @return {@link List<Book>} representing all rows from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     * @throws DaoException is thrown if DAO cannot be instantiated.
     */
    @Override
    public List<Book> getAll(Connection connection, int limit, int offset, Map<String, String> parameters) throws DbException, DaoException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(String.format(SQL_GET_ALL, setClause(parameters)));
            preparedStatement.setInt(1, limit);
            preparedStatement.setInt(2, offset);
            return getAll(connection, preparedStatement);
        } catch (SQLException e) {
            LOGGER.info(ERROR_BOOKS_NOT_LOADED);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(preparedStatement);
        }
    }

    /**
     * MySQL specific realization of {@link BookDao#update(Connection, Book)} method.
     * Updates a row which represents a book in the table <i>books</i>.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param book - a book to be updated.
     *
     * @throws DbException is thrown if data cannot be updated.
     */
    @Override
    public void update(Connection connection, Book book) throws DbException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_UPDATE);
            prepareStatement(book, preparedStatement);
            preparedStatement.setInt(8, book.getId());
            preparedStatement.execute();
            processDetails(connection, SQL_UPDATE_DETAILS, book);
            LOGGER.info(MESSAGE_BOOK_UPDATED + book.getId());
        } catch (SQLException e) {
            LOGGER.info(ERROR_BOOK_NOT_UPDATED + book.getId());
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(connection);
            DBUtils.release(preparedStatement);
        }
    }

    /**
     * MySQL specific realization of {@link BookDao#delete(Connection, int)} method.
     * Deletes a row which represents a book in the table <i>books</i>.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param id - id of a book to be deleted.
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
            LOGGER.info(MESSAGE_BOOK_DELETED + id);
        } catch (SQLException e) {
            LOGGER.info(ERROR_BOOK_NOT_DELETED + id);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(preparedStatement);
        }
    }

    /**
     * MySQL specific realization of {@link BookDao#insert(Connection, Book)} method.
     * Inserts a row to the table <i>books</i> with data specified in book.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param book - a book to be inserted.
     *
     * @throws DbException is thrown if data cannot be deleted.
     */
    @Override
    public int insert(Connection connection, Book book) throws DbException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            prepareStatement(book, preparedStatement);
            preparedStatement.execute();
            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            int id = resultSet.getInt(1);
            book.setId(id);
            processDetails(connection, SQL_INSERT_DETAILS, book);
            LOGGER.info(MESSAGE_BOOK_INSERTED + id);
            return id;
        } catch (SQLException e) {
            LOGGER.info(ERROR_BOOK_NOT_INSERTED + book.getTag());
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
    }

    /**
     * MySQL specific realization of {@link BookDao#getMaxPrice(Connection, Map)} method.
     * Retrieves a maximum price among filtered rows in <i>books</i> table
     * with where clause specified in {@code parameters}.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param parameters - parameters of where clause.
     *
     * @return a maximum price among filtered <i>books</i> table by where clause
     * specified in {@code parameters}.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     * @throws BadRequestException is thrown if some of parameters are invalid.
     */
    @Override
    public double getMaxPrice(Connection connection, Map<String, String> parameters) throws DbException, BadRequestException {
        double maxPrice = getPrice(connection, SQL_MAX_PRICE + setClause(parameters));
        LOGGER.info(MESSAGE_MAX_PRICE + maxPrice);
        return maxPrice;
    }

    /**
     * MySQL specific realization of {@link BookDao#getMinPrice(Connection, Map)} method.
     * Retrieves a minimum price among filtered rows in <i>books</i> table
     * with where clause specified in {@code parameters}.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param parameters - parameters of where clause.
     *
     * @return a minimum price among filtered <i>books</i> table by where clause
     * specified in {@code parameters}.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     * @throws BadRequestException is thrown if some of parameters are invalid.
     */
    @Override
    public double getMinPrice(Connection connection, Map<String, String> parameters) throws DbException, BadRequestException {
        double minPrice = getPrice(connection, SQL_MIN_PRICE + setClause(parameters));
        LOGGER.info(MESSAGE_MIN_PRICE + minPrice);
        return minPrice;
    }

    /**
     * Common method for finding maximum and minimum price. Retrieves data specified by {@code query} parameter.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param query - an SQL query to execute.
     *
     * @return a maximum or minimum price depending on the query.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     * @throws BadRequestException is thrown if some of parameters are invalid.
     */
    private double getPrice(Connection connection, String query) throws DbException, BadRequestException {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            resultSet.next();
            return resultSet.getDouble(1);
        } catch (SQLSyntaxErrorException e) {
            LOGGER.info(ERROR_PRICE_NOT_LOADED);
            LOGGER.error(e.getMessage());
            throw new BadRequestException();
        } catch (SQLException e) {
            LOGGER.info(ERROR_PRICE_NOT_LOADED);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, statement);
        }
    }

    /**
     * Common method for counting rows with specified SQL query.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param query - an SQL query to execute.
     *
     * @return number of rows in the table satisfying parameters..
     *
     * @throws DbException is thrown if data cannot be retrieved.
     * @throws BadRequestException is thrown if some of parameters are invalid.
     */
    private int count(Connection connection, String query) throws DbException, BadRequestException {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            resultSet.next();
            int c = resultSet.getInt(1);
            LOGGER.info(String.format(MESSAGE_BOOKS_COUNTED, c));
            return c;
        } catch (SQLSyntaxErrorException e) {
            LOGGER.info(ERROR_BOOKS_NOT_COUNTED);
            LOGGER.error(e.getMessage());
            throw new BadRequestException();
        } catch (SQLException e) {
            LOGGER.info(ERROR_BOOKS_NOT_COUNTED);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, statement);
        }
    }

    /**
     * An utility method that prepares {@link PreparedStatement} with data from the {@link Book} instance.
     *
     * @param book - an instance of {@link Book} with data to set.
     * @param preparedStatement - an instance of {@link PreparedStatement} to be prepared.
     *
     * @throws SQLException is thrown if {@link SQLException} is thrown while preparing statement.
     */
    private void prepareStatement(Book book, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, book.getTag());
        preparedStatement.setString(2, book.getIsbn());
        preparedStatement.setInt(3, book.getQuantity());
        preparedStatement.setInt(4, book.getPublisher().getId());
        preparedStatement.setInt(5, book.getCategory().getId());
        preparedStatement.setDouble(6, book.getPrice());
        preparedStatement.setDate(7, book.getDate());
    }

    /**
     * Common method for retrieving rows with parameters specified in {@code preparedStatement}.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param preparedStatement - a statement to be executed.
     *
     * @return {@link List<Book>} representing all rows from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     * @throws DaoException is thrown if DAO cannot be instantiated.
     */
    private List<Book> getAll(Connection connection, PreparedStatement preparedStatement) throws DbException, DaoException {
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
            LOGGER.info(MESSAGE_BOOKS_LOADED);
        } catch (SQLException e) {
            LOGGER.info(ERROR_BOOKS_NOT_LOADED);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
        return books;
    }

    /**
     * An utility method that sets data from {@code resultSet} to the {@link Book} instance.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param resultSet an instance of {@link ResultSet} containing necessary data.
     * @param book - an instance of {@link Book} to save data to.
     *
     * @throws SQLException is thrown if {@link SQLException} is thrown while processing {@code resultSet}.
     * @throws DaoException is thrown if DAO cannot be instantiated.
     */
    private void setBook(Connection connection, ResultSet resultSet, Book book) throws SQLException, DaoException {
        PublisherDao publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
        CategoryDao categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();
        book.setId(resultSet.getInt(FIELD_ID));
        book.setTag(resultSet.getString(FIELD_TAG));
        book.setIsbn(resultSet.getString(FIELD_ISBN));
        book.setDate(resultSet.getDate(FIELD_DATE));
        book.setQuantity(resultSet.getInt(FIELD_QUANTITY));
        Optional<Publisher> optionalPublisher = publisherDao.get(connection, resultSet.getInt(FIELD_PUBLISHER_ID));
        optionalPublisher.ifPresent(book::setPublisher);
        Optional<Category> optionalCategory = categoryDao.get(connection, resultSet.getInt(FIELD_CATEGORY_ID));
        optionalCategory.ifPresent(book::setCategory);
        book.setPrice(resultSet.getDouble(FIELD_PRICE));
        book.setNames(new HashMap<>());
        book.setDescriptions(new HashMap<>());
    }

    /**
     * An utility method that sets data which is retrieved from
     * <i>book_descriptions</i> table to the {@link Book} instance.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param book - an instance of {@link Book} to store data to.
     *
     * @throws SQLException is thrown if {@link SQLException} is thrown while processing the query.
     */
    private void setBookDetails(Connection connection, Book book) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_GET_DETAILS);
            preparedStatement.setInt(1, book.getId());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                book.getNames().put(resultSet.getInt(FIELD_LANGUAGE_ID), resultSet.getString(FIELD_TITlE));
                book.getDescriptions().put(resultSet.getInt(FIELD_LANGUAGE_ID), resultSet.getString(FIELD_DESCRIPTION));
            }
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
    }

    /**
     * An utility method that depending on {@code query} inserts or updates data
     * from an instance of {@link Book} to the table <i>book_descriptions</i>.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param query - MySQL insert or update query for the table <i>book_descriptions</i>.
     * @param book - an instance of {@link Book} to get data from.
     *
     * @throws SQLException is thrown if {@link SQLException} is thrown while processing the query.
     */
    private void processDetails(Connection connection, String query, Book book) throws SQLException {
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            CatalogueUtils.setDetailsFromEntity(preparedStatement, book);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            DBUtils.release(connection);
        }
    }

    /**
     * An utility method that depending on {@code parameters} sets where clause for MySQL select query.
     *
     * @param parameters - an instance of {@link Map} representing filter parameters.
     *
     * @return where clause string representing the filter set by user.
     */
    private String setClause(Map<String, String> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return "";
        }
        String categoryId = parameters.get(PARAM_CATEGORY);
        String publisherId = parameters.get(PARAM_PUBLISHER);
        String tag = parameters.get(PARAM_TAG);
        String isbn = parameters.get(PARAM_ISBN);
        String sortBy = parameters.get(PARAM_SORT_BY);
        String sortType = parameters.get(PARAM_SORT_TYPE);
        String priceMin = parameters.get(PARAM_PRICE_MIN);
        String priceMax = parameters.get(PARAM_PRICE_MAX);
        boolean isPricesSet = priceMin != null && priceMax != null && !"".equals(priceMin) && !"".equals(priceMax);
        StringBuilder stringBuilder = new StringBuilder();
        if ((categoryId != null && !"".equals(categoryId)) ||
                (publisherId != null && !"".equals(publisherId)) ||
                (tag != null && !"".equals(tag)) ||
                (isbn != null && !"".equals(isbn)) ||
                isPricesSet) {
            stringBuilder.append(CLAUSE_WHERE);
            List<String> parts = new ArrayList<>();
            if (categoryId != null && !"".equals(categoryId)) {
                parts.add(CLAUSE_CATEGORY + categoryId);
            }
            if (publisherId != null && !"".equals(publisherId)) {
                parts.add(CLAUSE_PUBLISHER + publisherId);
            }
            if (tag != null && !"".equals(tag)) {
                parts.add(String.format(CLAUSE_TAG, tag));
            }
            if (isbn != null && !"".equals(isbn)) {
                parts.add(String.format(CLAUSE_ISBN, isbn));
            }
            if (isPricesSet) {
                parts.add(String.format(CLAUSE_PRICE, priceMin, priceMax));
            }
            stringBuilder.append(String.join(CLAUSE_AND, parts));
        }
        if (sortBy != null && sortType != null) {
            stringBuilder.append(CLAUSE_ORDER).append(sortBy).append(' ').append(sortType);
        }
        return stringBuilder.toString();
    }
}
