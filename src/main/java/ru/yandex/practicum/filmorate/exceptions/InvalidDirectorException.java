package ru.yandex.practicum.filmorate.exceptions;

public class InvalidDirectorException extends RuntimeException {
    public InvalidDirectorException(String message) {
        super(message);
    }
}
