package com.epam.kkorolkov.finalproject.db.dao.mysql;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A stub for any MySQL realization for all DAOs.
 * Contains logger and properties instantiation. Also loads properties.
 */
public abstract class MysqlAbstractDao {
    /** Logger */
    protected static final Logger LOGGER = LogManager.getLogger("DAO");

    /** Properties */
    protected static final Properties SQL_STATEMENTS = new Properties();
    protected static final String PROPERTIES_FILE = "db/mysql/db.properties";

    static {
        try (InputStream settings = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            SQL_STATEMENTS.load(settings);
        } catch (IOException e) {
            LOGGER.info("Unable to load properties.");
            LOGGER.error(e.getMessage());
        }
    }
}
