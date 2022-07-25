package ru.yandex.practicum.filmorate.storage.impl;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.IllegalIdException;
import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@Qualifier("userDbStorage")
public class DbUserDaoImpl implements UserDao {

    public static String USERS_TABLE = "USERS";
    public static String EMAIL_COLUMN = "email";
    public static String LOGIN_COLUMN = "login";
    public static String USER_ID_COLUMN = "user_id";
    public static String USERNAME_COLUMN = "username";
    public static String BIRTHDAY_COLUMN = "birthday";
    public static String FRIEND_ID_COLUMN = "friend_id";
    public static String STATUS_NAME_COLUMN = "status_name";
    public static String FRIENDSHIP_STATUS_ID_COLUMN = "friendship_status_id";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbUserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User save(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(USERS_TABLE)
                .usingGeneratedKeyColumns(USER_ID_COLUMN);
        long id = simpleJdbcInsert.executeAndReturnKey(toMap(user)).longValue();
        user.setId(id);
        log.debug("Сохранён пользователь id: " + id);
        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "update USERS set " +
                          "email = ?, login = ?, username = ?, birthday = ? " +
                          "where user_id = ?";
        jdbcTemplate.update(sqlQuery,
                            user.getEmail(),
                            user.getLogin(),
                            user.getName(),
                            user.getBirthday(),
                            user.getId());
        log.debug("Обновлён пользователь id: " + user.getId());
        return user;
    }

    @Override
    public List<User> findAll() {
        String sqlQuery = "select * from USERS";
        return jdbcTemplate.query(sqlQuery, this::makeUser);
    }

    @Override
    public Optional<User> findUserById(long id) {
        String sqlQuery = "select * from USERS where user_id = " + id;
        User user = jdbcTemplate.query(sqlQuery, rs -> rs.next() ? makeUser(rs, 0) : null);
        if (user == null) {
            throw new IllegalIdException(String.format("Пользователь %d не найден", id));
        }
        return Optional.of(user);
    }

    @Override
    public void deleteUserById(Long userId) {
        String sqlQuery = "delete from USERS where user_id = ?;";
        jdbcTemplate.update(sqlQuery, userId);
        log.debug("Удален пользователь id: " + userId);
    }

    private Map<Long, FriendshipStatus> findFriendsWithFriendshipById(long userId) {
        Map<Long, FriendshipStatus> resultMap = new HashMap<>();
        String sqlQueryFriendsId = "select * from USER_FRIENDS AS UF" +
                "join FRIENDSHIP_STATUSES as FS on UF.friendship_status_id=FS.friendship_status_id " +
                "where user_id = " + userId;
        List<FriendLoadingContainer> friends =
                jdbcTemplate.query(sqlQueryFriendsId, (rs, rowNum) -> makeFriendLoadingContainer(rs));

        for (FriendLoadingContainer friendContainer : friends) {
            FriendshipStatus fs = FriendshipStatus.builder()
                    .id(friendContainer.getFriendshipStatusId())
                    .status(friendContainer.getFriendshipStatus())
                    .build();
            resultMap.put(friendContainer.getFriend_id(), fs);
        }

        return resultMap;
    }

    private FriendLoadingContainer makeFriendLoadingContainer(ResultSet rs) throws SQLException {
        return FriendLoadingContainer.builder()
                .friend_id(rs.getLong(FRIEND_ID_COLUMN))
                .friendshipStatusId(rs.getLong(FRIENDSHIP_STATUS_ID_COLUMN))
                .friendshipStatus(rs.getString(STATUS_NAME_COLUMN))
                .build();
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        long userId = rs.getLong(USER_ID_COLUMN);
        return User.builder()
                .id(userId)
                .email(rs.getString(EMAIL_COLUMN))
                .login(rs.getString(LOGIN_COLUMN))
                .name(rs.getString(USERNAME_COLUMN))
                .birthday(rs.getDate(BIRTHDAY_COLUMN).toLocalDate())
                .build();
    }

    private Map<String, Object> toMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put(EMAIL_COLUMN, user.getEmail());
        values.put(LOGIN_COLUMN, user.getLogin());
        values.put(USERNAME_COLUMN, user.getName());
        values.put(BIRTHDAY_COLUMN, user.getBirthday());
        return values;
    }

    @Data
    @Builder
    static class FriendLoadingContainer {
        private long friend_id;
        private long friendshipStatusId;
        private String friendshipStatus;
    }
}