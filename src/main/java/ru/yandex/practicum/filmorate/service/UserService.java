package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.storage.dao.UserStorageDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserStorageDao userStorageDao;
    private final FriendshipDao friendshipDao;

    @Autowired
    public UserService(@Qualifier("userDbStorageDaoImpl") UserStorageDao userStorageDao, FriendshipDao friendshipDao) {
        this.userStorageDao = userStorageDao;
        this.friendshipDao = friendshipDao;
    }

    public User add(User user) throws InvalidUserException {
        return userStorageDao.save(user);
    }

    public User update(User user) throws InvalidUserException, UserNotFoundException {
        return userStorageDao.update(user);
    }

    public List<User> get() {
        return userStorageDao.findAll();
    }

    public User getUserById(long id) throws UserNotFoundException {
        return userStorageDao.findUserById(id).orElse(null);
    }

    public void addFriend(long userId, long friendId) throws UserNotFoundException {
        friendshipDao.addFriend(userId, friendId);
        log.debug("пользователь: id:" + friendId + " добавлен в друзья пользователю id: " + userId);
    }

    public void deleteFriend(long user1Id, long user2Id) throws UserNotFoundException {
        friendshipDao.deleteFriend(user1Id, user2Id);
        friendshipDao.deleteFriend(user2Id, user1Id);
        log.debug("пользователи: id:" + user1Id + " и id:" + user2Id + " больше не друзья");
    }

    public List<User> getUserFriends(long id) throws UserNotFoundException {
        List<User> result = new ArrayList<>();

        Optional<User> optionalUser = userStorageDao.findUserById(id);
        if (optionalUser.isPresent()) {
            for (long friendId : friendshipDao.getFriends(id)) {
                if (userStorageDao.findUserById(friendId).isPresent())
                    result.add(userStorageDao.findUserById(friendId).get());
            }
        }

        return result;
    }

    public List<User> getCommonFriends(long user1Id, long user2Id) throws UserNotFoundException {
        List<User> commonFriends = new ArrayList<>();
        List<Long> user1FriendsId = friendshipDao.getFriends(user1Id);
        List<Long> user2FriendsId = friendshipDao.getFriends(user2Id);

        for (long id : user1FriendsId) {
            if (user2FriendsId.contains(id)) {
                userStorageDao.findUserById(id).ifPresent(commonFriends::add);
            }
        }
        return commonFriends;
    }
}
