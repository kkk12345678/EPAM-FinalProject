package com.epam.kkorolkov.finalproject.db.dao;

import com.epam.kkorolkov.finalproject.db.entity.Status;
import com.epam.kkorolkov.finalproject.exception.DBException;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface StatusDao {
    Optional<Status> get(Connection connection, int id) throws DBException;
    List<Status> getAll(Connection connection) throws DBException;
}
