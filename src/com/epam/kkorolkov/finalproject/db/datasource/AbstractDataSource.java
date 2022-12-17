package com.epam.kkorolkov.finalproject.db.datasource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class {@code AbstractDataSource} is a stub for all classes
 * that implement {@link DataSource} interface.
 *
 * Contains following fields: database credentials,
 * database driver, logger, and logger messages.
 */
public abstract class AbstractDataSource {
    /** Logger */
    protected static final Logger LOGGER = LogManager.getLogger("DB");

    /** Logger messages */
    protected static final String MESSAGE_ERROR = "Unable to connect to the database.";
    protected static final String MESSAGE_SUCCESS = "Connection successful.";
    protected static final String MESSAGE_CLOSE = "Connection closed.";

    /** Database credentials */
    protected String dbUrl;
    protected String dbUser;
    protected String dbPassword;
    protected static final String mySqlConnectionUrlFormat= "%s?user=%s&password=%s";

    /** Database driver */
    protected String dbDriver;
}
