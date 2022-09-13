package com.epam.kkorolkov.finalproject.db.dao.mysql;

import com.epam.kkorolkov.finalproject.db.dao.LanguageDao;
import com.epam.kkorolkov.finalproject.db.entity.Language;
import com.epam.kkorolkov.finalproject.exception.DBException;
import com.epam.kkorolkov.finalproject.utils.DBUtils;

import java.sql.*;
import java.util.*;

public class MysqlLanguageDaoImpl extends MysqlAbstractDao implements LanguageDao {
    @Override
    public Map<Integer, Language> getAll(Connection connection) throws DBException {
        Map<Integer, Language> languages = new HashMap<>();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_STATEMENTS.getProperty("mysql.languages.select.all"));
            while (resultSet.next()) {
                Language language = new Language();
                language.setId(resultSet.getInt(1));
                language.setCode(resultSet.getString(4));
                language.setImage(resultSet.getString(3));
                language.setLocale(resultSet.getString(5));
                language.setName(resultSet.getString(2));
                languages.put(language.getId(), language);
            }
            LOGGER.info("All languages were successfully loaded.");
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new DBException(e.getMessage(), e);
        } finally {
            DBUtils.release(resultSet, statement);
        }
        return languages;
    }

    @Override
    public int getIdByLocale(Connection connection, String locale) throws DBException{
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int id = 1;
        try {
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS.getProperty("mysql.languages.select.by.locale"));
            preparedStatement.setString(1, locale);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new DBException(e.getMessage(), e);
        } finally {
            DBUtils.release(resultSet, preparedStatement);
        }
        return id;
    }
}
