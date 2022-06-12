package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
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

    public void addFriend(long user1Id, long user2Id) {
        userStorage.getUser(user1Id).addFriend(user2Id);
        userStorage.getUser(user2Id).addFriend(user1Id);
    }

    public void deleteFriend(long user1Id, long user2Id) {
        userStorage.getUser(user1Id).deleteFriend(user2Id);
        userStorage.getUser(user2Id).deleteFriend(user1Id);
    }

    public List<User> getUserFriends(long id) {
        List<User> result = new ArrayList<>();

        for (long friendId : userStorage.getUser(id).getFriends()) {
            result.add(userStorage.getUser(friendId));
        }

        return result;
    }

    public List<User> getCommonFriends(long user1Id, long user2Id) {
        List<User> commonFriends = new ArrayList<>();
        Set<Long> users1FriendsId = userStorage.getUser(user1Id).getFriends();
        Set<Long> users2FriendsId = userStorage.getUser(user2Id).getFriends();

        for (Long id : users1FriendsId) {
            if (users2FriendsId.contains(id)) commonFriends.add(userStorage.getUser(id));
        }
        return commonFriends;
    }
}
