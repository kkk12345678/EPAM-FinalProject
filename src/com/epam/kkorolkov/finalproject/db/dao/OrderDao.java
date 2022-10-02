package com.epam.kkorolkov.finalproject.db.dao;

import com.epam.kkorolkov.finalproject.db.entity.Order;
import com.epam.kkorolkov.finalproject.exception.DBException;

import java.sql.Connection;
import java.util.List;

public interface OrderDao {
    int count(Connection connection) throws DBException;

    List<Order> getAll(Connection connection) throws DBException;
}
