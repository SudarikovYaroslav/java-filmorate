package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserIdGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController extends Controller<User> {
    public static final String NULL_USER_LOG = "Передан null user";
    public static final String NULL_FIELDS_LOG = "Некорректно инициализирован пользователь, есть null поля";
    public static final String BAD_EMAIL_LOG = "Некорректный адрес email";
    public static final String BAD_LOGIN_LOG = "Логин пустой или содержит пробелы";
    public static final String ASSIGNED_NAME_LOG = "Пользователю присвоено имя: ";
    public static final String BAD_BIRTHDAY_LOG = "День рождения указан в будущем";
    public static final String NEGATIVE_ID_LOG = "У пользователя отрицательный id";
    public static final String ASSIGNED_ID_LOG = "Пользователю присвоен id: ";

    @PostMapping
    public User add(@RequestBody User user) throws InvalidUserException {
        isNull(user);
        validate(user);
        user.setId(UserIdGenerator.generate());
        log.debug(ASSIGNED_ID_LOG + user.getId());
        data.put(user.getId(), user);
        log.debug("Добавлен пользователь: " + user.getLogin());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) throws InvalidUserException {
        isNull(user);
        validate(user);

        if (data.containsKey(user.getId())) {
            data.put(user.getId(), user);
            log.debug("Обновлён пользователь: " + user.getLogin());
        }
        return user;
    }

    @GetMapping
    public List<User> get() {
        log.debug("Текущее количество пользователей: " + data.size());
        return new ArrayList<>(data.values());
    }

    @Override
    protected void validate(User user) throws InvalidUserException {
        if (user.getEmail() == null
                || user.getLogin() == null
                || user.getBirthday() == null
        ) {
            log.warn(NULL_FIELDS_LOG);
            throw new NullPointerException(NULL_FIELDS_LOG);
        }

        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn(BAD_EMAIL_LOG);
            throw new InvalidUserException(BAD_EMAIL_LOG);
        }

        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn(BAD_LOGIN_LOG);
            throw new InvalidUserException(BAD_LOGIN_LOG);
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug(ASSIGNED_NAME_LOG + user.getName());
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn(BAD_BIRTHDAY_LOG);
            throw new InvalidUserException(BAD_BIRTHDAY_LOG);
        }

        if (user.getId() < 0) {
            log.warn(NEGATIVE_ID_LOG);
            throw new InvalidUserException(NEGATIVE_ID_LOG);
        }
    }

    private void isNull(User user) {
        log.warn(NULL_USER_LOG);
        if (user == null) throw new NullPointerException(NULL_USER_LOG);
    }
}
