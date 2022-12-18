package com.epam.kkorolkov.finalproject.db.dao;

import com.epam.kkorolkov.finalproject.db.entity.Language;
import com.epam.kkorolkov.finalproject.exception.DbException;

import java.sql.Connection;
import java.util.Map;

/**
 * Provides methods to deal with <i>languages</i> table in the database.
 */
public interface LanguageDao {
    /**
     * Method {@code getAll} retrieves all rows from the table <i>languages</i>
     *
     * @param connection an instance of {@link Connection} to reach the database.
     *
     * @return {@link Map} representing all rows from the table where key is <i>language_id</i>.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    Map<Integer, Language> getAll(Connection connection) throws DbException;
}
