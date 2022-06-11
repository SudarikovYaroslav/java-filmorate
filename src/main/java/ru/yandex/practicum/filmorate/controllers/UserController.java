package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage = new InMemoryUserStorage();

    @PostMapping
    public User add(@RequestBody User user) throws InvalidUserException {
        return userStorage.add(user);
    }

    @PutMapping
    public User update(@RequestBody User user) throws InvalidUserException {
        return userStorage.update(user);
    }

    @GetMapping
    public List<User> get() {
       return userStorage.get();
    }
}
