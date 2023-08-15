package ru.practicum.shareit.exceptions;
public class AlreadyExistException extends RuntimeException {
    public AlreadyExistException(final String message) {
        super(message);
    }
}