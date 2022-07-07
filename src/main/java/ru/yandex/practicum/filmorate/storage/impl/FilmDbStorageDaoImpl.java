package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorageDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@Qualifier("filmDbStorage")
public class FilmDbStorageDaoImpl implements FilmStorageDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorageDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film save(Film film) throws InvalidFilmException {
        String sqlQueryFilms =
                "insert into FILMS (film_id, film_name, description, release_date, duration, mpa_rating_id) " +
                "values (?, ?, ?, ?, ?, ?)"
        ;
        jdbcTemplate.update(sqlQueryFilms,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaId()
        );

        String sqlQueryFilmGenres = "insert into FILM_GENRES (film_id, genre_id) values (?, ?)";

        for (Long genreId : film.getGenres()) {
            jdbcTemplate.update(sqlQueryFilmGenres, film.getId(), genreId);
        }
        return film;
    }

    @Override
    public Film update(Film film) throws InvalidFilmException, FilmNotFoundException {
        String sqlQueryFilms =
                "update FILMS set " +
                "film_name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? " +
                "where id = ?"
        ;
        jdbcTemplate.update(sqlQueryFilms,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaId(),
                film.getId()
        );

        String sqlQueryFilmGenres = "update FILM_GENRES genre_id = ? where film_id = ?";
        for (Long genreId : film.getGenres()) {
            jdbcTemplate.update(sqlQueryFilmGenres, genreId, film.getId());
        }
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
                .mpaId(rs.getLong("mpa_rating"))
                .genres(findGenresByFilmId(filmId))
                .build();
    }

    private Long makeGenreId(ResultSet rs) throws SQLException {
        return rs.getLong("genre_id");
    }
}
