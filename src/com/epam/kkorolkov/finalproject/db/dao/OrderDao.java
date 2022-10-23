package com.epam.kkorolkov.finalproject.db.dao;

import com.epam.kkorolkov.finalproject.db.entity.Order;
import com.epam.kkorolkov.finalproject.exception.BadRequestException;
import com.epam.kkorolkov.finalproject.exception.DBException;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface OrderDao {
    int count(Connection connection, Map<String, String> parameters) throws DBException;

    int insert(Connection connection, Order order) throws DBException;

    void delete(Connection connection, int id) throws DBException;

    void updateStatus(Connection connection, int orderId, int statusId) throws DBException;

    List<Order> getAll(Connection connection, int limit, int offset, Map<String, String> parameters) throws DBException;

    List<Order> getAllByUser(Connection connection, int limit, int offset, int userId) throws DBException;

}
