package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class GenreDaoImpl implements GenreDao {

    public String GENRE_ID_COLUMN = "genre_id";
    public String GENRE_NAME_COLUMN = "genre_name";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findAllGenres() {
        String sqlQuery = "select * from GENRES";
        return jdbcTemplate.query(sqlQuery, this::makeGenre);
    }

    @Override
    public Genre findGenreById(long id) {
        String sqlQuery = "select * from GENRES where GENRE_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::makeGenre, id).get(0);
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong(GENRE_ID_COLUMN))
                .name(rs.getString(GENRE_NAME_COLUMN))
                .build();
    }
}