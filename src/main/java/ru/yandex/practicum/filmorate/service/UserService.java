package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IllegalIdException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FeedDao;
import ru.yandex.practicum.filmorate.storage.dao.FilmDao;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.storage.dao.UserDao;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserDao userDao;
    private final FilmDao filmDao;
    private final FeedDao feedDao;
    private final FriendshipDao friendshipDao;
    private final ValidationService validationService;

    @Autowired
    public UserService(@Qualifier("dbUserDaoImpl") UserDao userDao,
                       FriendshipDao friendshipDao,
                       FilmDao filmDao,
                       FeedDao feedDao,
                       ValidationService validationService) {
        this.userDao = userDao;
        this.friendshipDao = friendshipDao;
        this.filmDao = filmDao;
        this.feedDao = feedDao;
        this.validationService = validationService;
    }

    public User add(User user) {
        validationService.validate(user);
        return userDao.save(user);
    }

    public User update(User user) {
        validationService.validate(user);
        return userDao.update(user);
    }

    public List<User> get() {
        return userDao.findAll();
    }

    public User getUserById(long id) {
        return userDao.findUserById(id)
                .orElseThrow(() -> new IllegalIdException(String.format("Пользователь %d не найден", id)));
    }

    public void addFriend(long userId, long friendId) {
        getUserById(userId);
        getUserById(friendId);
        Feed feed = new Feed(1, Instant.now().toEpochMilli(), userId,"FRIEND","ADD", friendId);
        feedDao.saveFeed(feed);
        friendshipDao.addFriend(userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        getUserById(userId);
        getUserById(friendId);
        Feed feed = new Feed(1, Instant.now().toEpochMilli(), userId,"FRIEND","REMOVE",
                friendId);
        feedDao.saveFeed(feed);
        friendshipDao.deleteFriend(userId, friendId);
    }

    public List<User> getUserFriends(long id) {
        getUserById(id);
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
        getUserById(user1Id);
        getUserById(user2Id);
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

    public void deleteUserById(Long userId) {
        getUserById(userId);
        userDao.deleteUserById(userId);
    }

    public List<Film> recommendationsFilms(Long id) {
        getUserById(id);
        List<Film> userFilms = new ArrayList<>(filmDao.findAllFavoriteMovies(id));
        List<Film> recommendationsFilms = new ArrayList<>(filmDao.recommendationsFilm(id));
        return recommendationsFilms.stream()
                .filter(film -> !userFilms.contains(film))
                .collect(Collectors.toList());
    }

    public List<Feed> getUserFeedList(Long userId){
        return feedDao.getUserFeedList(userId);
    }
}