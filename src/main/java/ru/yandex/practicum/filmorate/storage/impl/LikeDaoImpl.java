package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.dao.LikeDao;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class LikeDaoImpl implements LikeDao {

    public static String USER_ID_COLUMN = "user_id";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikeDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(long film, long user) {
        String sqlQuery = "insert into LIKES (film_id, user_id) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery,
                film,
                user);
    }

    @Override
    public void deleteLike(long film, long user) {
        String sqlQuery = "delete FROM LIKES where film_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery, film, user);
    }

    @Override
    public int likesNumber(long film) {
        String sqlQuery = "select  user_id from LIKES where film_id = ?";
        return jdbcTemplate.query(sqlQuery, this::makeId, film).size();
    }

    private long makeId(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong(USER_ID_COLUMN);
    }
}
