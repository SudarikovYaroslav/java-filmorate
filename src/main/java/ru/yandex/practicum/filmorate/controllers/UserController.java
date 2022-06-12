package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

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
        return userService.add(user);
    }

    @PutMapping
    public User update(@RequestBody User user) throws InvalidUserException {
        return userService.update(user);
    }

    @GetMapping
    public List<User> get() {
       return userService.get();
    }
}
