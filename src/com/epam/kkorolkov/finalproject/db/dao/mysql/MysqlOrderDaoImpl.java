package com.epam.kkorolkov.finalproject.db.dao.mysql;

import com.epam.kkorolkov.finalproject.db.dao.*;
import com.epam.kkorolkov.finalproject.db.entity.Book;
import com.epam.kkorolkov.finalproject.db.entity.Order;
import com.epam.kkorolkov.finalproject.db.entity.User;
import com.epam.kkorolkov.finalproject.exception.BadRequestException;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.util.DBUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MysqlOrderDaoImpl extends MysqlAbstractDao implements OrderDao {
    private int count(Connection connection) throws DBException {
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
    public int count(Connection connection, Map<String, String> parameters) throws DBException, BadRequestException {
        return count(connection);
    }

    @Override
    public int insert(Connection connection, Order order) throws DBException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(
                    SQL_STATEMENTS.getProperty("mysql.orders.insert"), Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, order.getUser().getId());
            preparedStatement.setInt(2, 1);
            preparedStatement.setDate(3, new Date(System.currentTimeMillis()));
            preparedStatement.setDouble(4, order.getTotal());
            preparedStatement.execute();
            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            int id = resultSet.getInt(1);
            LOGGER.info(String.format("Order (id=%d) successfully inserted.", id));
            order.setId(id);
            try {
                insertDetails(connection, order);
                LOGGER.info(String.format("Order (id=%d) details successfully inserted.", id));
            } catch (SQLException e) {
                LOGGER.info("Could not insert order details.");
                delete(connection, id);
                throw e;
            }
            return id;
        } catch (SQLException e) {
            LOGGER.info("Could not insert order.");
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
    }

    private void insertDetails(Connection connection, Order order) throws SQLException {
        if (order != null && order.getId() != 0) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    SQL_STATEMENTS.getProperty("mysql.orders.details.insert"));
            try {
                connection.setAutoCommit(false);
                for (Book book : order.getDetails().keySet()) {
                    preparedStatement.setInt(1, order.getId());
                    preparedStatement.setInt(2, book.getId());
                    preparedStatement.setInt(3, order.getDetails().get(book));
                    preparedStatement.execute();
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
            } finally {
                DBUtils.release(preparedStatement);
                connection.setAutoCommit(true);
            }
        }
    }

    @Override
    public void delete(Connection connection, int id) throws DBException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.orders.delete"));
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            LOGGER.info(String.format("Order with id = %d successfully deleted.", id));
        } catch (SQLException e) {
            LOGGER.info("Could not delete order with id " + id);
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(preparedStatement);
        }
    }

    @Override
    public void updateStatus(Connection connection, int orderId, int statusId) throws DBException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.orders.update.status"));
            preparedStatement.setInt(1, statusId);
            preparedStatement.setInt(2, orderId);
            preparedStatement.execute();
            LOGGER.info("Status has been successfully changed.");
        } catch (SQLException e) {
            LOGGER.info("Could not change status.");
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(preparedStatement);
        }
    }

    @Override
    public List<Order> getAll(Connection connection, int limit, int offset, Map<String, String> parameters) throws DBException {
        PreparedStatement preparedStatement = null;
        String query = SQL_STATEMENTS.getProperty("mysql.orders.select.all") + setClause(parameters) + " limit ? offset ?";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, limit);
            preparedStatement.setInt(2, offset);
            return getAll(connection, preparedStatement);
        } catch (SQLException e) {
            LOGGER.info("Could not load orders.");
            LOGGER.error(e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtils.release(preparedStatement);
        }
    }

    private String setClause(Map<String, String> parameters) {
        return "";
    }

    private List<Order> getAll(Connection connection, PreparedStatement preparedStatement) throws SQLException {
        List<Order> orders = new ArrayList<>();
        ResultSet resultSet = null;
        try {
            resultSet = preparedStatement.executeQuery();
            UserDao userDao = AbstractDaoFactory.getInstance().getUserDao();
            StatusDao statusDao = AbstractDaoFactory.getInstance().getStatusDao();
            while (resultSet.next()) {
                Order order = new Order();
                order.setId(resultSet.getInt("id"));
                order.setTotal(resultSet.getDouble("total"));
                order.setUser(userDao.get(connection, resultSet.getInt("customer_id")).orElseThrow(SQLException::new));
                order.setDateAdded(resultSet.getDate("date_added"));
                order.setStatus(statusDao.get(connection, resultSet.getInt("status_id")).orElseThrow(SQLException::new));
                order.setDetails(new HashMap<>());
                setOrderDetails(connection, order);
                orders.add(order);
            }
            LOGGER.info("Orders were successfully loaded.");
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
        return orders;
    }

    private void setOrderDetails(Connection connection, Order order) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.orders.details.select"));
            preparedStatement.setInt(1, order.getId());
            resultSet = preparedStatement.executeQuery();
            BookDao bookDao = AbstractDaoFactory.getInstance().getBookDao();
            Book book;
            while (resultSet.next()) {
                book = bookDao.get(connection, resultSet.getInt("book_id")).orElseThrow(SQLException::new);
                order.getDetails().put(book, resultSet.getInt("quantity"));
            }
            LOGGER.info("Order details were successfully loaded.");
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
    }
}