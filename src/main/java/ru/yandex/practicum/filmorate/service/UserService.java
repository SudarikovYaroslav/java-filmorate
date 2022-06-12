package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private static final String NULL_USER_MESSAGE = "передан null пользователь";

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User add(User user) throws InvalidUserException {
        return userStorage.add(user);
    }

    public User update(User user) throws InvalidUserException {
        return userStorage.update(user);
    }

    public List<User> get() {
        return userStorage.get();
    }

    public void addFriend(User user1, User user2) {
        if (user1 == null || user2 == null) throw new IllegalArgumentException(NULL_USER_MESSAGE);
        user1.addFriend(user2);
        user2.addFriend(user1);
    }

    public void deleteFriend(User user1, User user2) {
        if (user1 == null || user2 == null) throw new IllegalArgumentException(NULL_USER_MESSAGE);
        user1.deleteFriend(user2);
        user2.deleteFriend(user1);
    }

    public List<Long> getCommonFriends(User user1, User user2) {
        if (user1 == null || user2 == null) throw new IllegalArgumentException(NULL_USER_MESSAGE);
        List<Long> commonFriends = new ArrayList<>();

        for (Long id : user1.getFriends()) {
            if (user2.getFriends().contains(id)) commonFriends.add(id);
        }

        return commonFriends;
    }
}
