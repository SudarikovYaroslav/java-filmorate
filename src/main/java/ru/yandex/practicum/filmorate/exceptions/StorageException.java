package ru.yandex.practicum.filmorate.exceptions;

import java.util.function.Supplier;

public class StorageException extends RuntimeException {
    public StorageException(String message) {
        super(message);
    }
}
