package com.epam.kkorolkov.finalproject.db.dao;

import com.epam.kkorolkov.finalproject.db.entity.Language;
import com.epam.kkorolkov.finalproject.exception.DBException;

import java.sql.Connection;
import java.util.Map;

public interface LanguageDao {
    Map<Integer, Language> getAll(Connection connection) throws DBException;
    int getIdByLocale(Connection connection, String locale) throws DBException;

}
