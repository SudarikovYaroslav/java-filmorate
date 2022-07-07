package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorageDao {
    User save(User user) throws InvalidUserException;

    User update(User user) throws InvalidUserException, UserNotFoundException;

    List<User> findAll();

    Optional<User> findUserById(long id) throws UserNotFoundException;
}
