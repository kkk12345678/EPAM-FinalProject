package com.epam.kkorolkov.finalproject.exception;

import java.sql.SQLException;

/**
 * {@code DbException} is to be thrown if an error occurs while communicating with the database.
 */
public class DbException extends SQLException {
    public DbException() {
        super();
    }
}
