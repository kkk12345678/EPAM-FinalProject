package com.epam.kkorolkov.finalproject.db.dao;

import com.epam.kkorolkov.finalproject.db.entity.Language;
import com.epam.kkorolkov.finalproject.exception.DBException;

import java.sql.Connection;
import java.util.Map;
import java.util.Optional;

public interface LanguageDao {
    Map<Integer, Language> getAll(Connection connection) throws DBException;

    Optional<Language> getByLocale(Connection connection, String locale) throws DBException;
}
