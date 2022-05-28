package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    private final Map<String, User> users = new HashMap<>();

    @PostMapping
    public void createUser(@RequestBody User user) throws InvalidUserException {
        validateUser(user);
        users.put(user.getEmail(), user);
    }

    @PutMapping
    public void updateUser(@RequestBody User user) throws InvalidUserException {
        validateUser(user);
        users.put(user.getEmail(), user);
    }

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    private void validateUser(User user) throws InvalidUserException {
        if (user == null) throw new NullPointerException("Передано пустое значение пользователя!");

        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@"))
            throw new InvalidUserException("Электронная почта не может быть пустой и должна содержать символ @");

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" "))
            throw new InvalidUserException("Логин не может быть пустым и содержать пробелы!");

        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now()))
            throw new InvalidUserException("Дата рождения не может быть в будущем");
    }
}
