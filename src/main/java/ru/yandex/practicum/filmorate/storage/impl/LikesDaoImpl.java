package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.dao.LikesDao;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class LikesDaoImpl implements LikesDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikesDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(long film, long user) {
        String sqlQuery = "insert into LIKES (film_id, user_id) " +
                          "values (?, ?)"
        ;
        jdbcTemplate.update(sqlQuery,
                film,
                user
        );
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
        return rs.getLong("user_id");
    }
}
