package ru.yandex.practicum.filmorate.storage.dao;

import java.util.List;

public interface FriendshipDao {
    void addFriend(Long user, Long toUser);

    void deleteFriend(Long user, Long fromUser);

    List<Long> getFriends(Long user);
}
