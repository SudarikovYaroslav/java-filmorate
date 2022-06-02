package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserIdGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    Map<Long,User> users = new HashMap<>();

    @PostMapping
    public User add(@RequestBody User user) throws InvalidUserException {
        validateNotNull(user);
        validate(user);
        user.setId(UserIdGenerator.generate());
        log.debug("Пользователю присвоен id: " + user.getId());
        users.put(user.getId(), user);
        log.debug("Добавлен пользователь: " + user.getLogin());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) throws InvalidUserException {
        validateNotNull(user);
        validate(user);

        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.debug("Обновлён пользователь: " + user.getId());
        }
        return user;
    }

    @GetMapping
    public List<User> get() {
        log.debug("Текущее количество пользователей: " + users.size());
        return new ArrayList<>(users.values());
    }

    protected void validate(User user) throws InvalidUserException {
        if (user.getEmail() == null
                || user.getLogin() == null
                || user.getBirthday() == null
        ) {
            String message = "Некорректно инициализирован пользователь, есть null поля id: " + user.getId();
            log.warn(message);
            throw new NullPointerException(message);
        }

        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            String message = "Некорректный адрес email id: " + user.getId();
            log.warn(message);
            throw new InvalidUserException(message);
        }

        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            String message = "Логин пустой или содержит пробелы id: " + user.getId();
            log.warn(message);
            throw new InvalidUserException(message);
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Пользователю присвоено имя: " + user.getName());
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            String message = "День рождения указан в будущем id: " + user.getId();
            log.warn(message);
            throw new InvalidUserException(message);
        }

        if (user.getId() < 0) {
            String message = "У пользователя отрицательный id. id: " + user.getId();;
            log.warn(message);
            throw new InvalidUserException(message);
        }
    }

    private void validateNotNull(User user) {
        String message = "Передан null user";
        log.warn(message);
        if (user == null) throw new IllegalStateException(message);
    }
}
