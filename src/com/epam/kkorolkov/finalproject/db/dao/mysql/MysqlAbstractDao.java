package com.epam.kkorolkov.finalproject.db.dao.mysql;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class MysqlAbstractDao {
    protected static final Logger LOGGER = LogManager.getLogger("DAO");
    protected static final Properties SQL_STATEMENTS = new Properties();
    protected static final String PROPERTIES_FILE = "db/mysql/db.properties";

    static {
        try (InputStream settings = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            SQL_STATEMENTS.load(settings);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
