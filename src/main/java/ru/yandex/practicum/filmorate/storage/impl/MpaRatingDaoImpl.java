package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaRatingDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class MpaRatingDaoImpl implements MpaRatingDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaRatingDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> findAllMpaRatings() {
        String sqlQuery = "select * from MPA_RATINGS";
        return jdbcTemplate.query(sqlQuery, this::makeMpaRating);
    }

    @Override
    public Mpa findMpaRatingById(long id) {
        String sqlQuery = "select * from MPA_RATINGS where mpa_rating_id = ?";
        return jdbcTemplate.query(sqlQuery, this::makeMpaRating, id).get(0);
    }

    private Mpa makeMpaRating(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getLong("mpa_rating_id"))
                .name(rs.getString("mpa_name"))
                .build();
    }
}
