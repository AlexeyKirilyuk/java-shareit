package ru.practicum.shareit.exceptions;

public class AlreadyExistException extends RuntimeException {
    public AlreadyExistException() {
    }

    public AlreadyExistException(final String message) {
        super(message);
    }

    public AlreadyExistException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AlreadyExistException(final Throwable cause) {
        super(cause);
    }

}