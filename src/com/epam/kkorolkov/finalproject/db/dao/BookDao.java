package com.epam.kkorolkov.finalproject.db.dao;

import com.epam.kkorolkov.finalproject.db.entity.Book;
import com.epam.kkorolkov.finalproject.exception.BadRequestException;
import com.epam.kkorolkov.finalproject.exception.DbException;
import com.epam.kkorolkov.finalproject.exception.DaoException;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Provides methods to deal with <i>books</i> table in the database.
 */
public interface BookDao {
    /**
     * @return number of rows in the table <i>books</i> with
     * where clause specified by the filter given in {@code parameters}.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param parameters - parameters of where clause.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     * @throws BadRequestException is thrown if some of parameters are invalid.
     */
    int count(Connection connection, Map<String, String> parameters) throws DbException, BadRequestException;

    /**
     * Method {@code get} retrieves a row from the table <i>books</i>
     * by the specified {@code id}.
     * If table does not contain a row with specified {@code id}
     * returns empty {@link Optional}.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param id - id of a book to find.
     *
     * @return {@link Optional<Book>} representing a row from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     * @throws DaoException is thrown if DAO cannot be instantiated.
     */
    Optional<Book> get(Connection connection, int id) throws DbException, DaoException;

    /**
     * Method {@code get} retrieves a row from the table <i>books</i>
     * by the specified {@code tag}.
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
    Optional<Book> get(Connection connection, String tag) throws DbException, DaoException;

    /**
     * Method {@code getAll} retrieves rows from the table <i>books</i> with specified limit, offset,
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
    List<Book> getAll(Connection connection, int limit, int offset, Map<String, String> parameters) throws DbException, DaoException;

    /**
     * Method {@code update} update a row which represents a book in the table <i>books</i>.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param book - a book to be updated.
     *
     * @throws DbException is thrown if data cannot be updated.
     */
    void update(Connection connection, Book book) throws DbException;

    /**
     * Method {@code delete} deletes a row which represents a book in the table <i>books</i>.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param id - id of a publisher to be deleted.
     *
     * @throws DbException is thrown if data cannot be deleted.
     */
    void delete(Connection connection, int id) throws DbException;

    /**
     * Method {@code insert} inserts a row to the table <i>books</i> with data specified in book.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param book - a book to be inserted.
     *
     * @throws DbException is thrown if data cannot be deleted.
     */
    int insert(Connection connection, Book book) throws DbException;

    /**
     * Method {@code getMaxPrice} retrieves a maximum price among filtered rows in <i>books</i> table
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
    double getMaxPrice(Connection connection, Map<String, String> parameters) throws DbException, BadRequestException;

    /**
     * Method {@code getMinPrice} retrieves a minimum price among filtered rows in <i>books</i> table
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
    double getMinPrice(Connection connection, Map<String, String> parameters) throws DbException, BadRequestException;
}
