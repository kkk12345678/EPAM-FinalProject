package com.epam.kkorolkov.finalproject.exception;

public class BadRequestException extends Exception {
    public BadRequestException(Exception e) {
        super(e);
    }

    public BadRequestException() {
        super();
    }
}
