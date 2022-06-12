package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.generators.UserIdGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User add(User user) throws InvalidUserException {
        user.setId(UserIdGenerator.generate());
        log.debug("Пользователю присвоен id: " + user.getId());
        users.put(user.getId(), user);
        log.debug("Добавлен пользователь: " + user.getLogin());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) throws InvalidUserException {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.debug("Обновлён пользователь: " + user.getId());
        }
        return user;
    }

    @Override
    public List<User> get() {
        log.debug("Текущее количество пользователей: " + users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(long id) {
        return users.get(id);
    }
}
