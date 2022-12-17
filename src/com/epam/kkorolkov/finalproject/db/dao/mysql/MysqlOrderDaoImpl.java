package com.epam.kkorolkov.finalproject.db.dao.mysql;

import com.epam.kkorolkov.finalproject.db.dao.*;
import com.epam.kkorolkov.finalproject.db.entity.Book;
import com.epam.kkorolkov.finalproject.db.entity.Order;
import com.epam.kkorolkov.finalproject.exception.DbException;
import com.epam.kkorolkov.finalproject.exception.DaoException;
import com.epam.kkorolkov.finalproject.util.DBUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Provides MySQL specific implementation of {@link OrderDao}.
 */
public class MysqlOrderDaoImpl extends MysqlAbstractDao implements OrderDao {
    /** SQL statements */
    private static final String SQL_COUNT = SQL_STATEMENTS.getProperty("mysql.orders.count");
    private static final String SQL_GET_CUSTOMER = SQL_STATEMENTS.getProperty("mysql.orders.select.by.customer");
    private static final String SQL_GET_ALL = SQL_STATEMENTS.getProperty("mysql.orders.select.all") + "%s limit ? offset ?";
    private static final String SQL_UPDATE = SQL_STATEMENTS.getProperty("mysql.orders.update.status");
    private static final String SQL_DELETE = SQL_STATEMENTS.getProperty("mysql.orders.delete");
    private static final String SQL_INSERT = SQL_STATEMENTS.getProperty("mysql.orders.insert");
    private static final String SQL_GET_DETAILS = SQL_STATEMENTS.getProperty("mysql.orders.details.select");
    private static final String SQL_INSERT_DETAILS =  SQL_STATEMENTS.getProperty("mysql.orders.details.insert");

    /** Logger success messages */
    private static final String MESSAGE_ORDERS_COUNTED = "Orders were successfully counted.";
    private static final String MESSAGE_ORDERS_LOADED = "Orders were successfully loaded.";
    private static final String MESSAGE_STATUS_UPDATED = "Status has been successfully changed.";
    private static final String MESSAGE_ORDER_DELETED = "Order with id = %d successfully deleted.";
    private static final String MESSAGE_ORDER_INSERTED = "Order (id=%d) successfully inserted.";

    /** Logger error messages */
    private static final String ERROR_ORDERS_NOT_COUNTED = "Could not count orders.";
    private static final String ERROR_ORDERS_NOT_LOADED = "Could not load orders.";
    private static final String ERROR_STATUS_NOT_UPDATED = "Could not change status.";
    private static final String ERROR_ORDER_NOT_DELETED = "Could not delete order with id ";
    private static final String ERROR_ORDER_NOT_INSERTED = "Could not insert order.";

    /** Table fields */
    private static final String FIELD_ID = "id";
    private static final String FIELD_BOOK_ID = "book_id";
    private static final String FIELD_STATUS_ID = "status_id";
    private static final String FIELD_DATE = "date_added";
    private static final String FIELD_QUANTITY = "quantity";
    private static final String FIELD_CUSTOMER_ID = "customer_id";
    private static final String FIELD_TOTAL = "total";

    /** Request parameters */
    private static final String PARAM_USER = "user";
    private static final String PARAM_SUM = "sum";
    private static final String PARAM_DATE = "date";
    private static final String PARAM_STATUS = "status";
    private static final String PARAM_USER_ID = "user_id";

    /** Where clause parts */
    private static final String CLAUSE_WHERE = " where ";
    private static final String CLAUSE_STATUS = "status_id = ";
    private static final String CLAUSE_TOTAL = "total = ";
    private static final String CLAUSE_USER = "customer_id = ";
    private static final String CLAUSE_DATE = "date_added = '%s'";
    private static final String CLAUSE_AND = " and ";
    private static final String CLAUSE_CUSTOMER = " where customer_id = ";

    /**
     * MySQL specific realization of {@link OrderDao#count(Connection, Map)} method.
     *
     * @return number of rows in the table <i>orders</i> with
     * where clause specified by the filter given in {@code parameters}.
     *
     * @param connection - instance of {@link Connection} to reach the database.
     * @param parameters - parameters of where clause.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    @Override
    public int count(Connection connection, Map<String, String> parameters) throws DbException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_COUNT + setClause(parameters));
            return count(preparedStatement);
        } catch (SQLException e) {
            LOGGER.info(ERROR_ORDERS_NOT_COUNTED);
            LOGGER.error(e.getMessage());
            throw new DbException();
        }
    }

    /**
     * MySQL specific realization of {@link OrderDao#insert(Connection, Order)} method.
     * Inserts a row to the table <i>orders</i> with data specified in book.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param order - an order to be inserted.
     *
     * @throws DbException is thrown if data cannot be deleted.
     */
    @Override
    public int insert(Connection connection, Order order) throws DbException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, order.getUser().getId());
            preparedStatement.setInt(2, 1);
            preparedStatement.setDate(3, new Date(System.currentTimeMillis()));
            preparedStatement.setDouble(4, order.getTotal());
            preparedStatement.execute();
            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            int id = resultSet.getInt(1);
            order.setId(id);
            insertDetails(connection, order);
            LOGGER.info(String.format(MESSAGE_ORDER_INSERTED, id));
            return id;
        } catch (SQLException e) {
            LOGGER.info(ERROR_ORDER_NOT_INSERTED);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
    }

    /**
     * MySQL specific realization of method which inserts into the table
     * <i>order_details</i> necessary data.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param order - an order which details to be inserted.
     * @throws SQLException is thrown if data cannot be inserted.
     */
    private void insertDetails(Connection connection, Order order) throws SQLException {
        if (order != null && order.getId() != 0) {
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_DETAILS);
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

    /**
     * MySQL specific realization of {@link OrderDao#delete(Connection, int)} method.
     * Deletes a row which represents an order in the table <i>orders</i>.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param id - id of an order to be deleted.
     *
     * @throws DbException is thrown if data cannot be deleted.
     */
    @Override
    public void delete(Connection connection, int id) throws DbException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_DELETE);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            LOGGER.info(String.format(MESSAGE_ORDER_DELETED, id));
        } catch (SQLException e) {
            LOGGER.info(ERROR_ORDER_NOT_DELETED + id);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(preparedStatement);
        }
    }

    /**
     * MySQL specific realization of method which changes status
     * for a selected order in the table <i>orders</i>.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param orderId - id of an order to change status.
     * @param statusId - id of a status to change to.
     *
     * @throws DbException is thrown if data cannot be updated.
     */
    @Override
    public void updateStatus(Connection connection, int orderId, int statusId) throws DbException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_UPDATE);
            preparedStatement.setInt(1, statusId);
            preparedStatement.setInt(2, orderId);
            preparedStatement.execute();
            LOGGER.info(MESSAGE_STATUS_UPDATED);
        } catch (SQLException e) {
            LOGGER.info(ERROR_STATUS_NOT_UPDATED);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(preparedStatement);
        }
    }

    /**
     * MySQL specific realization of {@link OrderDao#getAll(Connection, int, int, Map)} method.
     * Retrieves rows from the table <i>orders</i> with specified limit, offset,
     * and where clause specified by the filter given in {@code parameters}.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param limit - number of rows to retrieve.
     * @param offset - number of a row to begin with.
     * @param parameters - parameters of where clause.
     *
     * @return {@link List<Book>} representing all rows from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     * @throws DaoException is thrown if DAO cannot be instantiated.
     */
    @Override
    public List<Order> getAll(Connection connection, int limit, int offset, Map<String, String> parameters) throws DbException, DaoException {
        String query = String.format(SQL_GET_ALL, setClause(parameters));
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, limit);
            preparedStatement.setInt(2, offset);
            return getAll(connection, preparedStatement);
        } catch (SQLException e) {
            LOGGER.info(ERROR_ORDERS_NOT_LOADED);
            LOGGER.error(e.getMessage());
            throw new DbException();
        }
    }

    /**
     * MySQL specific realization of {@link OrderDao#getAllByUser(Connection, int, int, int)} method.
     * Retrieves rows from the table <i>orders</i> with specified limit, offset for specified customer.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param limit - number of rows to retrieve.
     * @param offset - number of a row to begin with.
     * @param userId - id of a customer.
     *
     * @return {@link List<Book>} representing all rows from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     * @throws DaoException is thrown if DAO cannot be instantiated.
     */
    @Override
    public List<Order> getAllByUser(Connection connection, int limit, int offset, int userId) throws DbException, DaoException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_GET_CUSTOMER);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, limit);
            preparedStatement.setInt(3, offset);
            return getAll(connection, preparedStatement);
        } catch (SQLException e) {
            LOGGER.info(ERROR_ORDERS_NOT_LOADED);
            LOGGER.error(e.getMessage());
            throw new DbException();
        }
    }

    /**
     * Constructs SQL query where clause based on {@code parameters} map.
     *
     * @param parameters - parameters of where clause.
     * @return where clause of the SQL query.
     */
    private String setClause(Map<String, String> parameters) {
        if (parameters == null || parameters.keySet().size() == 0) {
            return "";
        }
        if (parameters.containsKey(PARAM_USER_ID)) {
            return CLAUSE_CUSTOMER + parameters.get(PARAM_USER_ID);
        }
        String user = parameters.get(PARAM_USER);
        String sum = parameters.get(PARAM_SUM);
        String date = parameters.get(PARAM_DATE);
        String status = parameters.get(PARAM_STATUS);
        StringBuilder stringBuilder = new StringBuilder();
        if (user != null && !"".equals(user) || sum != null && !"".equals(sum) ||
                date != null && !"".equals(date) || status != null && !"".equals(status)) {
            stringBuilder.append(CLAUSE_WHERE);
            List<String> parts = new ArrayList<>();
            if (user != null && !"".equals(user)) {
                parts.add(CLAUSE_USER + user);
            }
            if (sum != null && !"".equals(sum)) {
                parts.add(CLAUSE_TOTAL + sum);
            }
            if (date != null && !"".equals(date)) {
                parts.add(String.format(CLAUSE_DATE, date));
            }
            if (status != null && !"".equals(status)) {
                parts.add(CLAUSE_STATUS + status);
            }
            stringBuilder.append(String.join(CLAUSE_AND, parts));
            System.out.println(stringBuilder.toString());
        }
        return stringBuilder.toString();
    }

    /**
     * Retrieves rows from the table <i>orders</i> with an arbitrary {@link PreparedStatement}.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param preparedStatement an SQL statement to be executed.
     *
     * @return {@link List<Order>} representing selected rows.
     *
     * @throws SQLException is thrown if data cannot be retrieved.
     * @throws DaoException is thrown if DAO cannot be instantiated.
     */
    private List<Order> getAll(Connection connection, PreparedStatement preparedStatement) throws SQLException, DaoException {
        List<Order> orders = new ArrayList<>();
        ResultSet resultSet = null;
        try {
            resultSet = preparedStatement.executeQuery();
            UserDao userDao = AbstractDaoFactory.getInstance().getUserDao();
            StatusDao statusDao = AbstractDaoFactory.getInstance().getStatusDao();
            while (resultSet.next()) {
                Order order = new Order();
                order.setId(resultSet.getInt(FIELD_ID));
                order.setTotal(resultSet.getDouble(FIELD_TOTAL));
                order.setUser(userDao.get(connection, resultSet.getInt(FIELD_CUSTOMER_ID)).orElseThrow(SQLException::new));
                order.setDateAdded(resultSet.getDate(FIELD_DATE));
                order.setStatus(statusDao.get(connection, resultSet.getInt(FIELD_STATUS_ID)).orElseThrow(SQLException::new));
                order.setDetails(new HashMap<>());
                setOrderDetails(connection, order);
                orders.add(order);
            }
            LOGGER.info(MESSAGE_ORDERS_LOADED);
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
        return orders;
    }

    /**
     * Sets details from the table <i>order_details</i> to an instance of {@link Order}.
     *
     * @param connection - an instance of {@link Connection} to reach the database.
     * @param order - an order which details are to be selected.
     *
     * @throws SQLException is thrown if data cannot be retrieved.
     * @throws DaoException is thrown if DAO cannot be instantiated.
     */
    private void setOrderDetails(Connection connection, Order order) throws SQLException, DaoException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL_GET_DETAILS);
            preparedStatement.setInt(1, order.getId());
            resultSet = preparedStatement.executeQuery();
            BookDao bookDao = AbstractDaoFactory.getInstance().getBookDao();
            Book book;
            while (resultSet.next()) {
                book = bookDao.get(connection, resultSet.getInt(FIELD_BOOK_ID)).orElseThrow(SQLException::new);
                order.getDetails().put(book, resultSet.getInt(FIELD_QUANTITY));
            }
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
    }

    /**
     * Common method for counting rows with specified SQL query.
     *
     * @param preparedStatement an SQL statement to be executed.
     *
     * @return number of rows in the table satisfying parameters..
     *
     * @throws SQLException is thrown if data cannot be retrieved.
     */
    private int count(PreparedStatement preparedStatement) throws SQLException {
        ResultSet resultSet = null;
        try {
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            LOGGER.info(MESSAGE_ORDERS_COUNTED);
            return resultSet.getInt(1);
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
    }
}