package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User add(User user) throws InvalidUserException;

    User update(User user) throws InvalidUserException;

    List<User> get();

    User getUser(long id);
}
