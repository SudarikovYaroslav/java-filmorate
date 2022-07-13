package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.IllegalIdException;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.FilmDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
@Qualifier("filmDbStorage")
public class DbFilmDaoImpl implements FilmDao {
    public static String FILMS_TABLE = "FILMS";
    public static String FILM_ID_COLUMN = "film_id";
    public static String MPA_NAME_COLUMN = "mpa_name";
    public static String DURATION_COLUMN = "duration";
    public static String GENRE_ID_COLUMN = "genre_id";
    public static String FILM_NAME_COLUMN = "film_name";
    public static String GENRE_NAME_COLUMN = "genre_name";
    public static String DESCRIPTION_COLUMN = "description";
    public static String RELEASE_DATE_COLUMN = "release_date";
    public static String MPA_RATING_ID_COLUMN = "mpa_rating_id";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbFilmDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film save(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(FILMS_TABLE)
                .usingGeneratedKeyColumns(FILM_ID_COLUMN)
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
    public Film update(Film film) throws InvalidFilmException, IllegalIdException {
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
    public Optional<Film> findFilmById(long id) throws IllegalIdException {
        String sqlQuery = "select * from FILMS where film_id = " + id;
        return Optional.of(jdbcTemplate.query(sqlQuery, this::makeFilm).get(0));
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        long filmId = rs.getLong(FILM_ID_COLUMN);
        List<Genre> genres = findGenresByFilmId(filmId);
        return Film.builder()
                .id(filmId)
                .name(rs.getString(FILM_NAME_COLUMN))
                .description((rs.getString(DESCRIPTION_COLUMN)))
                .releaseDate(rs.getDate(RELEASE_DATE_COLUMN).toLocalDate())
                .duration(rs.getLong(DURATION_COLUMN))
                .mpa(makeMpaById(rs.getLong(MPA_RATING_ID_COLUMN)))
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
        return rs.getString(MPA_NAME_COLUMN);
    }

    private String makeGenreName(ResultSet rs) throws SQLException {
        return rs.getString(GENRE_NAME_COLUMN);
    }

    private Long makeGenreId(ResultSet rs) throws SQLException {
        return rs.getLong(GENRE_ID_COLUMN);
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
        values.put(FILM_NAME_COLUMN, film.getName());
        values.put(DESCRIPTION_COLUMN, film.getDescription());
        values.put(RELEASE_DATE_COLUMN, film.getReleaseDate());
        values.put(DURATION_COLUMN, film.getDuration());
        values.put(MPA_RATING_ID_COLUMN, film.getMpa().getId());
        return values;
    }
}

