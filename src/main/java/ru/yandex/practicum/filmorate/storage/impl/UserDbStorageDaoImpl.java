package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.FriendLoadingContainer;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserStorageDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@Qualifier("userDbStorage")
public class UserDbStorageDaoImpl implements UserStorageDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorageDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User save(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("user_id");
        long id = simpleJdbcInsert.executeAndReturnKey(toMap(user)).longValue();
        user.setId(id);
        log.debug("Сохранён пользователь id: " + id);
        return user;
    }

    @Override
    public User update(User user) throws InvalidUserException, UserNotFoundException {
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
    public Optional<User> findUserById(long id) throws UserNotFoundException {
        String sqlQuery = "select * from USERS where user_id = " + id;
        return Optional.of(jdbcTemplate.query(sqlQuery, this::makeUser).get(0));
    }

    /**
     * При загрузке друзей пользователя используется промежуточный контейнер - FriendLoadingContainer,
     * для обеспечения возможности загрузки всей необходимой информации по дружбе за один запрос
     * сразу к двум таблицам, и удобного заполнения мапы друзей класса User
     * */
    private Map<Long, FriendshipStatus> findFriendsWithFriendshipById(long userId) {
        Map<Long, FriendshipStatus> resultMap = new HashMap<>();

        String sqlQueryFriendsId = "select * from USER_FRIENDS AS UF" +
                "join FRIENDSHIP_STATUSES as FS on UF.friendship_status_id=FS.friendship_status_id " +
                "where user_id = " + userId
        ;
        List<FriendLoadingContainer> friends =
                jdbcTemplate.query(sqlQueryFriendsId, (rs, rowNum) -> makeFriendLoadingContainer(rs));

        for (FriendLoadingContainer friendContainer : friends) {
            FriendshipStatus fs = FriendshipStatus.builder()
                    .status_id(friendContainer.getFriendshipStatusId())
                    .status(friendContainer.getFriendshipStatus())
                    .build()
            ;
            resultMap.put(friendContainer.getFriend_id(), fs);
        }

        return resultMap;
    }

    private FriendLoadingContainer makeFriendLoadingContainer(ResultSet rs) throws SQLException {
        return FriendLoadingContainer.builder()
                .friend_id(rs.getLong("friend_id"))
                .friendshipStatusId(rs.getLong("friendship_status_id"))
                .friendshipStatus(rs.getString("status_name"))
                .build();
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        long userId = rs.getLong("user_id");
        return User.builder()
                .id(userId)
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("username"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    private Map<String, Object> toMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("username", user.getName());
        values.put("birthday", user.getBirthday());
        return values;
    }
}