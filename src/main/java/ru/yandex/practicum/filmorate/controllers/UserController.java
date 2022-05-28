package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController extends Controller<User> {

    private final Map<String, User> users = new HashMap<>();

    @PostMapping
    public void add(@RequestBody User user) throws InvalidUserException {
        validateUser(user);
        users.put(user.getEmail(), user);
        log.debug("Добавлен пользователь: " + user.getLogin() + " - email: " + user.getEmail());
    }

    @PutMapping
    public void update(@RequestBody User user) throws InvalidUserException {
        validateUser(user);
        users.put(user.getEmail(), user);
        log.debug("Обновлён пользователь: " + user.getLogin() + " - email: " + user.getEmail());
    }

    @GetMapping
    public Collection<User> get() {
        log.debug("Текущее количество пользователей: " + users.size());
        return users.values();
    }

    private void validateUser(User user) throws InvalidUserException {
        if (user == null) {
            log.warn("Передано пустое значение пользователя");
            throw new NullPointerException("Передано пустое значение пользователя!");
        }

        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Передано невалидное значение email");
            throw new InvalidUserException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Передано невалидное значение login");
            throw new InvalidUserException("Логин не может быть пустым и содержать пробелы!");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Передано пустое name, присвоено имя name = " + user.getLogin());
            user.setName(user.getLogin());
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Передана некорректная дата рождения");
            throw new InvalidUserException("Дата рождения не может быть в будущем");
        }
    }
}
