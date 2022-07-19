package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.storage.dao.UserDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserDao userDao;
    private final FriendshipDao friendshipDao;

    @Autowired
    public UserService(@Qualifier("dbUserDaoImpl") UserDao userDao, FriendshipDao friendshipDao) {
        this.userDao = userDao;
        this.friendshipDao = friendshipDao;
    }

    public User add(User user) throws InvalidUserException {
        return userDao.save(user);
    }

    public User update(User user) throws InvalidUserException {
        return userDao.update(user);
    }

    public List<User> get() {
        return userDao.findAll();
    }

    public User getUserById(long id) {
        return userDao.findUserById(id).orElse(null);
    }

    public void addFriend(long userId, long friendId) {
        friendshipDao.addFriend(userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        friendshipDao.deleteFriend(userId, friendId);
    }

    public List<User> getUserFriends(long id) {
        List<User> result = new ArrayList<>();

        Optional<User> optionalUser = userDao.findUserById(id);
        if (optionalUser.isPresent()) {
            for (long friendId : friendshipDao.getFriends(id)) {
                if (userDao.findUserById(friendId).isPresent())
                    result.add(userDao.findUserById(friendId).get());
            }
        }

        return result;
    }

    public List<User> getCommonFriends(long user1Id, long user2Id) {
        List<User> commonFriends = new ArrayList<>();
        List<Long> user1FriendsId = friendshipDao.getFriends(user1Id);
        List<Long> user2FriendsId = friendshipDao.getFriends(user2Id);

        for (long id : user1FriendsId) {
            if (user2FriendsId.contains(id)) {
                userDao.findUserById(id).ifPresent(commonFriends::add);
            }
        }
        return commonFriends;
    }
}
