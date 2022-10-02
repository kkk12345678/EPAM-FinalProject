package com.epam.kkorolkov.finalproject.db.dao;

import com.epam.kkorolkov.finalproject.db.entity.Publisher;
import com.epam.kkorolkov.finalproject.exception.DBException;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface PublisherDao {
    int count(Connection connection) throws DBException;
    Optional<Publisher> get(Connection connection, int id) throws DBException;
    Optional<Publisher> get(Connection connection, String tag) throws DBException;
    List<Publisher> getAll(Connection connection) throws DBException;
    void update(Connection connection, Publisher publisher) throws DBException;
    void delete(Connection connection, int id) throws DBException;
    int insert(Connection connection, Publisher publisher) throws DBException;
}
