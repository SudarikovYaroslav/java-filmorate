package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.IllegalIdException;
import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User add(@RequestBody User user) throws InvalidUserException {
        validate(user);
        return userService.add(user);
    }

    @PutMapping
    public User update(@RequestBody User user) throws InvalidUserException {
        validate(user);
        return userService.update(user);
    }

    @GetMapping
    public List<User> get() {
       return userService.get();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        checkNegativeIds(id);
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        checkNegativeIds(id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        checkNegativeIds(id, friendId);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable long id) {
        checkNegativeIds(id);
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        checkNegativeIds(id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    protected void validate(User user) throws InvalidUserException {
        validateNotNull(user);

        if(user.getId() < 0) {
            throw new IllegalIdException("id пользователя не может быть отрицательным");
        }

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
    }

    private void validateNotNull(User user) {
        if (user == null) {
            String message = "Передан null user";
            log.warn(message);
            throw new IllegalStateException(message);
        }
    }

    public void checkNegativeIds(long... ids) {
        for (long id : ids) {
            if (id <= 0 ) throw new IllegalIdException("user id:" + id + " отрицательный");
        }
    }
}
