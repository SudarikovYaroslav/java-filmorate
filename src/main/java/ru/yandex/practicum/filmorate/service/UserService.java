package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User add(User user) throws InvalidUserException {
        return userStorage.add(user);
    }

    public User update(User user) throws InvalidUserException, UserNotFoundException {
        return userStorage.update(user);
    }

    public List<User> get() {
        return userStorage.get();
    }

    public User getUserById(long id) throws UserNotFoundException {
        return userStorage.getUser(id);
    }

    public void addFriend(long user1Id, long user2Id) throws UserNotFoundException {
        userStorage.getUser(user1Id).addFriend(user2Id);
        userStorage.getUser(user2Id).addFriend(user1Id);
        log.debug("пользователи: id:" + user1Id + " и id:" + user2Id + " теперь друзья");
    }

    public void deleteFriend(long user1Id, long user2Id) throws UserNotFoundException {
        userStorage.getUser(user1Id).deleteFriend(user2Id);
        userStorage.getUser(user2Id).deleteFriend(user1Id);
        log.debug("пользователи: id:" + user1Id + " и id:" + user2Id + " больше не друзья");
    }

    public List<User> getUserFriends(long id) throws UserNotFoundException {
        List<User> result = new ArrayList<>();

        for (long friendId : userStorage.getUser(id).getFriends()) {
            result.add(userStorage.getUser(friendId));
        }

        return result;
    }

    public List<User> getCommonFriends(long user1Id, long user2Id) throws UserNotFoundException {
        List<User> commonFriends = new ArrayList<>();
        Set<Long> users1FriendsId = userStorage.getUser(user1Id).getFriends();
        Set<Long> users2FriendsId = userStorage.getUser(user2Id).getFriends();

        for (Long id : users1FriendsId) {
            if (users2FriendsId.contains(id)) commonFriends.add(userStorage.getUser(id));
        }
        return commonFriends;
    }
}
