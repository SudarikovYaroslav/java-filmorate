package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IllegalIdException;
import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.storage.dao.UserDao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
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
        validate(user);
        return userDao.save(user);
    }

    public User update(User user) throws InvalidUserException {
        validate(user);
        return userDao.update(user);
    }

    public List<User> get() {
        return userDao.findAll();
    }

    public User getUserById(long id) {
        checkNegativeIds(id);
        return userDao.findUserById(id).orElse(null);
    }

    public void addFriend(long userId, long friendId) {
        checkNegativeIds(userId, friendId);
        friendshipDao.addFriend(userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        checkNegativeIds(userId, friendId);
        friendshipDao.deleteFriend(userId, friendId);
    }

    public List<User> getUserFriends(long id) {
        checkNegativeIds(id);
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
        checkNegativeIds(user1Id, user2Id);
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
