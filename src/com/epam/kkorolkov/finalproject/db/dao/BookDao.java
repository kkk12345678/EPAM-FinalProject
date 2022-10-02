package com.epam.kkorolkov.finalproject.db.dao;

import com.epam.kkorolkov.finalproject.db.entity.Book;
import com.epam.kkorolkov.finalproject.exception.DBException;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface BookDao {
    int count(Connection connection) throws DBException;
    Optional<Book> get(Connection connection, int id) throws DBException;
    Optional<Book> get(Connection connection, String tag) throws DBException;
    List<Book> getAll(Connection connection) throws DBException;
    List<Book> getAll(Connection connection, int limit, int page) throws DBException;
    List<Book> getAll(Connection connection, int limit, int page, int categoryId, int publisherId) throws DBException;
    void update(Connection connection, Book book) throws DBException;
    void delete(Connection connection, int id) throws DBException;
    int insert(Connection connection, Book book) throws DBException;
}
