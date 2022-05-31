package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserIdGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static ru.yandex.practicum.filmorate.model.Constants.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController extends Controller<User> {

    @PostMapping
    public User add(@RequestBody User user) throws InvalidUserException {
        isNull(user);
        validate(user);
        user.setId(UserIdGenerator.generate());
        log.debug(ASSIGNED_USER_ID_LOG + user.getId());
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
            log.warn(NULL_USER_FIELDS_LOG);
            throw new NullPointerException(NULL_USER_FIELDS_LOG);
        }

        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn(BAD_USER_EMAIL_LOG);
            throw new InvalidUserException(BAD_USER_EMAIL_LOG);
        }

        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn(BAD_USER_LOGIN_LOG);
            throw new InvalidUserException(BAD_USER_LOGIN_LOG);
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug(ASSIGNED_USER_NAME_LOG + user.getName());
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn(BAD_USER_BIRTHDAY_LOG);
            throw new InvalidUserException(BAD_USER_BIRTHDAY_LOG);
        }

        if (user.getId() < 0) {
            log.warn(NEGATIVE_USER_ID_LOG);
            throw new InvalidUserException(NEGATIVE_USER_ID_LOG);
        }
    }

    private void isNull(User user) {
        log.warn(NULL_USER_LOG);
        if (user == null) throw new NullPointerException(NULL_USER_LOG);
    }
}
