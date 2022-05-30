package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.IdGenerator;

import java.time.LocalDate;
import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController extends Controller<User> {

    @PostMapping
    public void add(@RequestBody User user) throws InvalidUserException {
        validate(user);
        validateIdWhenAdd(user);
        data.put(user.getId(), user);
        log.debug("Добавлен пользователь: " + user.getLogin());
    }

    @PutMapping
    public void update(@RequestBody User user) throws InvalidUserException {
        validate(user);
        validateIdWhenUpdate(user);
        data.put(user.getId(), user);
        log.debug("Обновлён пользователь: " + user.getLogin());
    }

    @GetMapping
    public Collection<User> get() {
        log.debug("Текущее количество пользователей: " + data.size());
        return data.values();
    }

    @Override
    protected void validate(User user) throws InvalidUserException {
        if (user == null) {
            log.warn("Передано пустое значение пользователя");
            throw new InvalidUserException("Передано пустое значение пользователя!");
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

        if (user.getBirthday() == null) {
            throw new InvalidUserException("Не указана дата рождения пользователя!");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Передана некорректная дата рождения");
            throw new InvalidUserException("Дата рождения не может быть в будущем!");
        }
    }

    /**
     Для обеспечения не идемпотентности метода POST, в случае, если хранилище содержит пользователя с id, таким же,
     как у передаваемого, переданному пользователю присваивается новый id
     */
    private void validateIdWhenAdd(User user) {
        if (user.getId() <= 0) {
            user.setId(IdGenerator.generateId());
            log.debug("Пользователю " + user.getName() + "не установлен id. Присвоен id = " + user.getId());
        }

        if (data.containsKey(user.getId())) {
            user.setId(IdGenerator.generateId());
            log.debug("Пользователю " + user.getName() + " присвоен id = " + user.getId());
        }
    }

    /**
     Если у переданного пользователя не был установлен id то, чтобы избежать дублирования, сначала проверяем
     хранилище на наличие пользователя по email,
     т.к. если такой пользователь уже был добавлен, так же без id, то id ему уже был сгенерирован
     */
    private void validateIdWhenUpdate(User user) {
        if (user.getId() <= 0) {
            for (User existedUser : data.values()) {
                if (user.getEmail().equals(existedUser.getEmail())) user.setId(existedUser.getId());
            }
            if (user.getId() <= 0) user.setId(IdGenerator.generateId());
            log.debug("Пользователю " + user.getName() + "не установлен id. Присвоен id=" + user.getId());
        }
    }
}
