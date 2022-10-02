package com.epam.kkorolkov.finalproject.exception;

public class DBConnectionException extends DBException {
    public DBConnectionException(String message, Exception cause) {
        super(message, cause);
    }
}
