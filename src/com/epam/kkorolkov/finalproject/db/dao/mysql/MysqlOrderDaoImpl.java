package com.epam.kkorolkov.finalproject.db.dao.mysql;

import com.epam.kkorolkov.finalproject.db.dao.OrderDao;
import com.epam.kkorolkov.finalproject.db.entity.Order;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.util.DBUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class MysqlOrderDaoImpl extends MysqlAbstractDao implements OrderDao {
    @Override
    public int count(Connection connection) throws DBException {
        int c;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_STATEMENTS.getProperty("mysql.orders.select.count"));
            resultSet.next();
            c = resultSet.getInt(1);
            LOGGER.info(String.format("There are %d orders in the table.", c));
        } catch (SQLException e) {
            LOGGER.info("Could not count orders.");
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(resultSet, statement);
        }
        return c;
    }

    @Override
    public List<Order> getAll(Connection connection) throws DBException {
        return null;
    }
}
