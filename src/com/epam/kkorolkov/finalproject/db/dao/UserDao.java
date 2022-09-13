package com.epam.kkorolkov.finalproject.db.dao;

import com.epam.kkorolkov.finalproject.db.entity.User;
import com.epam.kkorolkov.finalproject.exception.DBException;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> get(Connection connection, int id) throws DBException;
    List<User> getAll(Connection connection) throws DBException;
    void update(Connection connection, User user) throws DBException;
    void delete(Connection connection, int id) throws DBException;
    int insert(Connection connection, User user) throws DBException;
}
