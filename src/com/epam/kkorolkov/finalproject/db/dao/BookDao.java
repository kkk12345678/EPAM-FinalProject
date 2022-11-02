package com.epam.kkorolkov.finalproject.db.dao;

import com.epam.kkorolkov.finalproject.db.entity.Book;
import com.epam.kkorolkov.finalproject.exception.BadRequestException;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.exception.DaoException;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 */
public interface BookDao {
    /**
     *
     * @param connection
     * @param parameters
     * @return
     * @throws DBException
     * @throws BadRequestException
     */
    int count(Connection connection, Map<String, String> parameters) throws DBException, BadRequestException;

    Optional<Book> get(Connection connection, int id) throws DBException, DaoException;
    Optional<Book> get(Connection connection, String tag) throws DBException, DaoException;

    List<Book> getAll(Connection connection, int limit, int offset, Map<String, String> parameters) throws DBException, DaoException;

    void update(Connection connection, Book book) throws DBException;
    void delete(Connection connection, int id) throws DBException;
    int insert(Connection connection, Book book) throws DBException;

    int getMaxPrice(Connection connection, Map<String, String> parameters) throws DBException, BadRequestException;
    int getMinPrice(Connection connection, Map<String, String> parameters) throws DBException, BadRequestException;
}
