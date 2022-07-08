package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.dialect.HsqlDbDialect;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;
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
    public List<MpaRating> findAllMpaRatings() {
        String sqlQuery = "select * from MPA_RATINGS";
        return jdbcTemplate.query(sqlQuery, this::makeMpaRating);
    }

    @Override
    public MpaRating findMpaRatingById(long id) {
        String sqlQuery = "select * from MPA_RATINGS where mpa_rating_id = ?";
        return jdbcTemplate.query(sqlQuery, this::makeMpaRating, id).get(0);
    }

    private MpaRating makeMpaRating(ResultSet rs, int rowNum) throws SQLException {
        return MpaRating.builder()
                .id(rs.getLong("mpa_rating_id"))
                .name(rs.getString("mpa_name"))
                .build();
    }
}
