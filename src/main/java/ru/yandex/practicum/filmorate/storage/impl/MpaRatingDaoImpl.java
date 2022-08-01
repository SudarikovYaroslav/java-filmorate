package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.IllegalIdException;
import ru.yandex.practicum.filmorate.exceptions.StorageException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaRatingDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class MpaRatingDaoImpl implements MpaRatingDao {

    public static String MPA_NAME_COLUMN = "mpa_name";
    public static String MPA_RATING_ID_COLUMN = "mpa_rating_id";

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
        List<Mpa> mpaList = jdbcTemplate.query(sqlQuery, this::makeMpaRating, id);
        if (mpaList.size() != 1) {
            throw new StorageException("Рейтинга с таким id нет в базе данных");
        }
        return mpaList.get(0);
    }

    private Mpa makeMpaRating(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getLong(MPA_RATING_ID_COLUMN))
                .name(rs.getString(MPA_NAME_COLUMN))
                .build();
    }
}
