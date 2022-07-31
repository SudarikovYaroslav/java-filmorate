package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.dao.MarkDao;

@Repository
public class MarkDaoImpl implements MarkDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MarkDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addMark(long filmId, long userId, int mark) {
        String sqlQuery = "insert into MARKS (film_id, user_id, mark) " +
                "values (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId, mark);
        String sqlQueryFilms = "update FILMS set RATE = ? where film_id = ?";
        jdbcTemplate.update(sqlQueryFilms,
                findRateFilm(filmId),
                filmId);
    }

    @Override
    public void deleteMark(long filmId, long userId) {
        String sqlQuery = "delete FROM MARKS where film_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        String sqlQueryFilms = "update FILMS set RATE = ? where film_id = ?";
        jdbcTemplate.update(sqlQueryFilms,
                findRateFilm(filmId),
                filmId);
    }

    private Double findRateFilm(long filmId) {
        String sqlQuery = "select AVG(CAST(mark as real)) from (select * from MARKS where film_id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, Double.class, filmId);
    }

}
