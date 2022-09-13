package com.epam.kkorolkov.finalproject.exception;

import java.sql.SQLException;

public class DBException extends SQLException {
    public DBException(String message, Exception e) {
        super(message, e);
    }

    public DBException() {
        super();
    }

    public DBException(SQLException e) {
        super(e);
    }
}
