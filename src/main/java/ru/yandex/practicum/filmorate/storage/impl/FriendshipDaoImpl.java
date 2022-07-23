package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
public class FriendshipDaoImpl implements FriendshipDao {

    public static String FRIEND_ID_COLUMN = "friend_id";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendshipDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        long friendsHipStatus = determineFriendsHipStatus(userId, friendId);
        String sqlQuery = "insert into USER_FRIENDS (user_id, friend_id, friendship_status_id) " +
                "values (?, ?, ?)"
        ;

        jdbcTemplate.update(sqlQuery,
                userId,
                friendId,
                friendsHipStatus
        );

        log.debug(String.format("Пользователю id: %d добавлен друг id: %d статус дружбы id: %s"
                , userId, friendId, friendsHipStatus))
        ;
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        String sqlQuery = "delete from USER_FRIENDS where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        log.debug(String.format("пользователь id: %d удалён из друзей пользователя id: %d", friendId, userId));
    }

    @Override
    public List<Long> getFriends(Long user) {
        String sqlQuery = "select friend_id from USER_FRIENDS where user_id = ?";
        return jdbcTemplate.query(sqlQuery, this::makeId, user);
    }

    private Long makeId(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong(FRIEND_ID_COLUMN);
    }

    /**
     * Определяет статус дружбы: если user уже находится в друзьях у friend, то дружбе будет присвоен статус 2 -
     * подтверждённая, в противном случае 1 - неподтверждённая
     */
    private long determineFriendsHipStatus(long user, long friend) {
        List<Long> friendsId = getFriends(friend);
        if (friendsId.contains(user)) return 2;
        return 1;
    }
}
