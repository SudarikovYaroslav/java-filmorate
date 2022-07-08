package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorageDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@Qualifier("filmDbStorage")
public class FilmDbStorageDaoImpl implements FilmStorageDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorageDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film save(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("film_id")
        ;
        long id = simpleJdbcInsert.executeAndReturnKey(toMap(film)).longValue();
        film.setId(id);
        log.debug("Сохранён фильм id: " + id);
        return film;
    }

    @Override
    public Film update(Film film) throws InvalidFilmException, FilmNotFoundException {
        String sqlQueryFilms =
                "update FILMS set " +
                "film_name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? " +
                "where film_id = ?"
        ;
        jdbcTemplate.update(sqlQueryFilms,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaId(),
                film.getId()
        );

        if (film.getGenres() != null) {
            String sqlQueryFilmGenres = "update FILM_GENRES genre_id = ? where film_id = ?";
            for (Long genreId : film.getGenres()) {
                jdbcTemplate.update(sqlQueryFilmGenres, genreId, film.getId());
            }
        }
        log.info("Обновлён фильм id: " + film.getId());
        return film;
    }

    @Override
    public List<Film> findAll() {
        String sqlQuery = "select * from FILMS";
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    @Override
    public Optional<Film> findFilmById(long id) throws FilmNotFoundException {
        String sqlQuery = "select * from FILMS where film_id = " + id;
        return Optional.of(jdbcTemplate.query(sqlQuery, this::makeFilm).get(0));
    }

    private List<Long> findGenresByFilmId(long filmId) {
        String sqlQuery = "select genre_id from FILM_GENRES where film_id = " + filmId;
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeGenreId(rs));
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        long filmId = rs.getLong("film_id");
        return Film.builder()
                .id(filmId)
                .name(rs.getString("film_name"))
                .description((rs.getString("description")))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getLong("duration"))
                .mpaId(rs.getLong("mpa_rating_id"))
                .genres(findGenresByFilmId(filmId))
                .build();
    }

    private Long makeGenreId(ResultSet rs) throws SQLException {
        return rs.getLong("genre_id");
    }

    private Map<String, Object> toMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("film_name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("mpa_rating_id", film.getMpaId());
        return values;
    }
}
