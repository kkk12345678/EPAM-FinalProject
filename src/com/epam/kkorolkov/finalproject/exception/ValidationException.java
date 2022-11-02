package com.epam.kkorolkov.finalproject.exception;

public class ValidationException extends Exception {
    /**
     * Constructs a new {@code ValidationException} with the specified detail message.
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@code #getMessage()} method.
     */
    public ValidationException(String message) {
        super(message);
    }

}
