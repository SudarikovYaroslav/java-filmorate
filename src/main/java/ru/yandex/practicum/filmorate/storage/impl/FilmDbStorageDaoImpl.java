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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorageDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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

        Mpa mpa = makeMpaById(film.getMpa().getId());
        film.setId(id);
        film.setMpa(mpa);

        if (film.getGenres() != null) fillFilmGenresTable(film);
        film.setGenres(findGenresByFilmId(id));
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
                film.getMpa().getId(),
                film.getId()
        );

        if (film.getGenres() != null) updateFilmGenresTable(film);
        Mpa mpa = makeMpaById(film.getMpa().getId());
        film.setMpa(mpa);
        film.setGenres(findGenresByFilmId(film.getId()));
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

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        long filmId = rs.getLong("film_id");
        List<Genre> genres = findGenresByFilmId(filmId);
        return Film.builder()
                .id(filmId)
                .name(rs.getString("film_name"))
                .description((rs.getString("description")))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getLong("duration"))
                .mpa(makeMpaById(rs.getLong("mpa_rating_id")))
                .genres(genres)
                .build();
    }

    private Mpa makeMpaById(long mpaId) {
        String sqlQuery = "select mpa_name from MPA_RATINGS where mpa_rating_id = ?";
        String mpaName = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeMpaName(rs), mpaId).get(0);
        return Mpa.builder()
                .id(mpaId)
                .name(mpaName)
                .build();
    }

    private Genre makeGenreById(long genreId) {
        String sqlQuery = "select genre_name from GENRES where genre_id = ?";
        String genreName = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeGenreName(rs), genreId).get(0);
        return Genre.builder()
                .id(genreId)
                .name(genreName)
                .build();
    }

    private String makeMpaName(ResultSet rs) throws SQLException {
        return rs.getString("mpa_name");
    }

    private String makeGenreName(ResultSet rs) throws SQLException {
        return rs.getString("genre_name");
    }

    private Long makeGenreId(ResultSet rs) throws SQLException {
        return rs.getLong("genre_id");
    }

    private List<Genre> findGenresByFilmId(long filmId) {
        String sqlQuery = "select genre_id from FILM_GENRES where film_id = ?";
        List<Long> genresId = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeGenreId(rs), filmId);
        List<Genre> result = new ArrayList<>();

        for (long genreId : genresId) {
            result.add(makeGenreById(genreId));
        }
        return result;
    }

    private void fillFilmGenresTable(Film film) {
        String sql = "merge into FILM_GENRES (film_id, genre_id) " +
                "values (?, ?)";

        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sql,
                    film.getId(),
                    genre.getId());
        }
    }

    private void updateFilmGenresTable(Film film) {
        cleanOldFilmGenresRecords(film);
        fillFilmGenresTable(film);
    }

    public void cleanOldFilmGenresRecords(Film film) {
        String sqlQuery = "delete from FILM_GENRES where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    private Map<String, Object> toMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("film_name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("mpa_rating_id", film.getMpa().getId());
        return values;
    }
}

