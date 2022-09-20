package com.epam.kkorolkov.finalproject.db.dao;

import com.epam.kkorolkov.finalproject.db.entity.Category;
import com.epam.kkorolkov.finalproject.exception.DBException;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface CategoryDao {
    Optional<Category> get(Connection connection, int id) throws DBException;
    Optional<Category> get(Connection connection, String tag) throws DBException;
    List<Category> getAll(Connection connection) throws DBException;
    void update(Connection connection, Category category) throws DBException;
    void delete(Connection connection, int id) throws DBException;
    int insert(Connection connection, Category category) throws DBException;
}
