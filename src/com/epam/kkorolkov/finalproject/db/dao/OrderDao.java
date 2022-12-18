package com.epam.kkorolkov.finalproject.db.dao;

import com.epam.kkorolkov.finalproject.db.entity.Order;
import com.epam.kkorolkov.finalproject.exception.DbException;
import com.epam.kkorolkov.finalproject.exception.DaoException;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Provides methods to deal with <i>orders</i> table in the database.
 */
public interface OrderDao {
    /**
     * @return number of rows in the table <i>orders</i> with
     * where clause specified by the filter given in {@code parameters}.
     *
     * @param connection an instance of {@link Connection} to reach the database.
     * @param parameters parameters of where clause.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    int count(Connection connection, Map<String, String> parameters) throws DbException;

    /**
     * Method {@code insert} inserts a row to the table <i>orders</i> with data specified in {@code order}.
     *
     * @param connection an instance of {@link Connection} to reach the database.
     * @param order a order to be inserted.
     *
     * @return {@code id} of the inserted {@link Order}.
     *
     * @throws DbException is thrown if data cannot be deleted.
     */
    int insert(Connection connection, Order order) throws DbException;

    /**
     * Method {@code delete} deletes a row which represents an order in the table <i>orders</i>.
     *
     * @param connection an instance of {@link Connection} to reach the database.
     * @param id id of a order to be deleted.
     *
     * @throws DbException is thrown if data cannot be deleted.
     */
    void delete(Connection connection, int id) throws DbException;

    /**
     * Method {@code updateStatus} changes status of an order in the table <i>orders</i>.
     *
     * @param connection an instance of {@link Connection} to reach the database.
     * @param orderId id of an order to change status.
     * @param statusId id of a status to change to.
     *
     * @throws DbException is thrown if data cannot be updated.
     */
    void updateStatus(Connection connection, int orderId, int statusId) throws DbException;

    /**
     * Method {@code getAll} retrieves rows from the table <i>orders</i> with specified limit, offset,
     * and where clause specified by the filter given in {@code parameters}.
     *
     * @param connection an instance of {@link Connection} to reach the database.
     * @param limit number of rows to retrieve.
     * @param offset number of a row to begin with.
     * @param parameters parameters of where clause.
     *
     * @return {@link List} representing all rows from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     * @throws DaoException is thrown if DAO cannot be instantiated.
     */
    List<Order> getAll(Connection connection, int limit, int offset, Map<String, String> parameters) throws DbException, DaoException;

    /**
     * Method {@code getAllByUser} retrieves all rows from the table <i>orders</i>
     * representing orders made by a single user.
     *
     * @param connection an instance of {@link Connection} to reach the database.
     * @param limit number of rows to retrieve.
     * @param offset number of a row to begin with.
     * @param userId id of a user whose orders are to be retrieved.
     *
     * @return {@link List} representing orders made by a single user.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     * @throws DaoException is thrown if DAO cannot be instantiated.
     */
    List<Order> getAllByUser(Connection connection, int limit, int offset, int userId) throws DbException, DaoException;
}
