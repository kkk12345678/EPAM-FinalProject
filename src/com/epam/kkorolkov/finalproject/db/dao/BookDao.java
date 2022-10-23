package com.epam.kkorolkov.finalproject.db.dao;

import com.epam.kkorolkov.finalproject.db.entity.Book;
import com.epam.kkorolkov.finalproject.exception.BadRequestException;
import com.epam.kkorolkov.finalproject.exception.DBException;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BookDao {
    int count(Connection connection, Map<String, String> parameters) throws DBException, BadRequestException;

    Optional<Book> get(Connection connection, int id) throws DBException;
    Optional<Book> get(Connection connection, String tag) throws DBException;

    List<Book> getAll(Connection connection, int limit, int page, Map<String, String> parameters) throws DBException, BadRequestException;

    void update(Connection connection, Book book) throws DBException;
    void delete(Connection connection, int id) throws DBException;
    int insert(Connection connection, Book book) throws DBException;

    int getMaxPrice(Connection connection, Map<String, String> parameters) throws DBException, BadRequestException;
    int getMinPrice(Connection connection, Map<String, String> parameters) throws DBException, BadRequestException;
}
