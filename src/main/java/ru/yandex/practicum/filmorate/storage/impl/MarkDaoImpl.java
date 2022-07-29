package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.dao.MarkDao;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class MarkDaoImpl implements MarkDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MarkDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addMark(long film, long user, int mark) {
        String sqlQuery = "insert into MARKS (film_id, user_id, mark) " +
                "values (?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                film,
                user, mark);
    }

    @Override
    public void deleteMark(long film, long user) {
        String sqlQuery = "delete FROM MARKS where film_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery, film, user);
    }

    @Override
    public Double findAvgMark(long film) {
        String sqlQuery = "select AVG(CAST(mark as real)) from (select * from MARKS where film_id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, Double.class, film);
    }
}
