package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class FriendshipDaoImpl implements FriendshipDao {

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
    }

    @Override
    public boolean deleteFriend(Long fromUser, Long user) {
        String sqlQuery = "delete from USER_FRIENDS where user_id = ? and friend_id = ?";
        return jdbcTemplate.update(sqlQuery, user, fromUser) > 0;
    }

    @Override
    public List<Long> getFriends(Long user) {
        String sqlQuery = "select friend_id from USER_FRIENDS where user_id = ?";
        return jdbcTemplate.query(sqlQuery, this::makeId, user);
    }

    private Long makeId(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong("friend_id");
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
