package com.epam.kkorolkov.finalproject.db.dao.mysql;

import com.epam.kkorolkov.finalproject.db.dao.LanguageDao;
import com.epam.kkorolkov.finalproject.db.entity.Language;
import com.epam.kkorolkov.finalproject.exception.DbException;
import com.epam.kkorolkov.finalproject.util.DBUtils;

import java.sql.*;
import java.util.*;

/**
 *  Provides MySQL specific implementation of {@link LanguageDao}.
 */
public class MysqlLanguageDaoImpl extends MysqlAbstractDao implements LanguageDao {
    /** SQL statements */
    private static final String SQL_GET_ALL = SQL_STATEMENTS.getProperty("mysql.languages.select.all");

    /** Logger success messages */
    private static final String MESSAGE_LANGUAGES_LOADED = "All languages were successfully loaded.";

    /** Logger error messages */
    private static final String ERROR_LANGUAGES_NOT_LOADED = "Could not load languages";

    /**
     * MySQL specific realization of {@link LanguageDao#getAll(Connection)} method.
     * Retrieves all rows from the table <i>languages</i>.
     *
     * @param connection an instance of {@link Connection} to reach the database.
     *
     * @return {@link Map} representing all rows from the table.
     *
     * @throws DbException is thrown if data cannot be retrieved.
     */
    @Override
    public Map<Integer, Language> getAll(Connection connection) throws DbException {
        Map<Integer, Language> languages = new HashMap<>();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_GET_ALL);
            while (resultSet.next()) {
                Language language = new Language();
                language.setId(resultSet.getInt(1));
                language.setImage(resultSet.getString(3));
                language.setLocale(resultSet.getString(5));
                language.setName(resultSet.getString(2));
                languages.put(language.getId(), language);
            }
            LOGGER.info(MESSAGE_LANGUAGES_LOADED);
            return languages;
        } catch (SQLException e) {
            LOGGER.info(ERROR_LANGUAGES_NOT_LOADED);
            LOGGER.error(e.getMessage());
            throw new DbException();
        } finally {
            DBUtils.release(resultSet, statement);
        }
    }
}
