package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.IllegalIdException;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Director;
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
    public static String DIRECTOR_ID_COLUMN = "director_id";
    public static String RELEASE_DATE_COLUMN = "release_date";
    public static String MPA_RATING_ID_COLUMN = "mpa_rating_id";
    public static String DIRECTOR_NAME_COLUMN = "director_name";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbFilmDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film save(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(FILMS_TABLE)
                .usingGeneratedKeyColumns(FILM_ID_COLUMN);
        long id = simpleJdbcInsert.executeAndReturnKey(toMap(film)).longValue();

        Mpa mpa = makeMpaById(film.getMpa().getId());
        film.setId(id);
        film.setMpa(mpa);

        if (film.getGenres() != null) fillFilmGenresTable(film);
        film.setGenres(findGenresByFilmId(id));

        if (film.getDirectors() != null) fillFilmDirectorsTable(film);
        film.setDirectors(findDirectorsByFilmId(id));

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

        if (film.getDirectors() != null) updateFilmDirectorsTable(film);
        film.setDirectors(findDirectorsByFilmId(film.getId()));

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

        Film film = jdbcTemplate.query(sqlQuery, rs -> rs.next() ? makeFilm(rs, 0) : null);
        if (film == null) {
            throw new IllegalIdException(String.format("Фильм %d не найден", id));
        }
        return Optional.of(film);
    }

    @Override
    public void deleteFilmById(Long filmId) {
        String sqlQuery = "delete from FILMS where film_id = ?;";
        jdbcTemplate.update(sqlQuery, filmId);
        log.debug("Удален фильм id: " + filmId);
    }

    /**
     * Если в sortBy передано "likes", отсортирует все фильмы режиссёра по количеству лайков в порядке убывания,
     * в противном случае по дате релиза фильма от поздних к ранним (сортировка по умолчанию)
     */
    @Override
    public List<Film> getDirectorFilms(long directorId, String sortBy) {
        List<Film> result = new ArrayList<>();
        String sqlQuery;

        if (sortBy.equals("likes")) {
            sqlQuery = "select FD.FILM_ID from FILM_DIRECTORS as FD " +
                    "join LIKES as L on FD.FILM_ID = L.FILM_ID where DIRECTOR_ID = ? " +
                    "order by count(USER_ID) desc";

        } else {
            sqlQuery = "select FD.FILM_ID from FILM_DIRECTORS as FD " +
                    "join FILMS F on FD.FILM_ID = F.FILM_ID where DIRECTOR_ID = ? " +
                    "order by RELEASE_DATE desc";
        }

        List<Long> filmsId = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilmId(rs), directorId);

        for (Long filmId : filmsId) {
            findFilmById(filmId).ifPresent(result::add);
        }
        return result;
    }

    private long makeFilmId(ResultSet rs) throws SQLException {
        return rs.getLong("film_id");
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        long filmId = rs.getLong(FILM_ID_COLUMN);
        List<Genre> genres = findGenresByFilmId(filmId);
        List<Director> directors = findDirectorsByFilmId(filmId);
        return Film.builder()
                .id(filmId)
                .name(rs.getString(FILM_NAME_COLUMN))
                .description((rs.getString(DESCRIPTION_COLUMN)))
                .releaseDate(rs.getDate(RELEASE_DATE_COLUMN).toLocalDate())
                .duration(rs.getLong(DURATION_COLUMN))
                .mpa(makeMpaById(rs.getLong(MPA_RATING_ID_COLUMN)))
                .genres(genres)
                .directors(directors)
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

    private Director makeDirectorById(long directorId) {
        String sqlQuery = "select director_name from DIRECTORS where director_id = ?";
        String directorName = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeDirectorName(rs), directorId).get(0);
        return Director.builder()
                .id(directorId)
                .name(directorName)
                .build();
    }

    private String makeMpaName(ResultSet rs) throws SQLException {
        return rs.getString(MPA_NAME_COLUMN);
    }

    private String makeGenreName(ResultSet rs) throws SQLException {
        return rs.getString(GENRE_NAME_COLUMN);
    }

    private String makeDirectorName(ResultSet rs) throws SQLException {
        return rs.getString(DIRECTOR_NAME_COLUMN);
    }

    private Long makeGenreId(ResultSet rs) throws SQLException {
        return rs.getLong(GENRE_ID_COLUMN);
    }

    private Long makeDirectorId(ResultSet rs) throws SQLException {
        return rs.getLong(DIRECTOR_ID_COLUMN);
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

    private List<Director> findDirectorsByFilmId(long filmId) {
        String sqlQuery = "select director_id from FILM_DIRECTORS where film_id = ?";
        List<Long> directorsId = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeDirectorId(rs), filmId);
        List<Director> result = new ArrayList<>();

        for (long directorId : directorsId) {
            result.add(makeDirectorById(directorId));
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

    private void fillFilmDirectorsTable(Film film) {
        String sql = "merge into FILM_DIRECTORS (film_id, director_id) " +
                "values (?,?)";

        for (Director director : film.getDirectors()) {
            jdbcTemplate.update(sql,
                    film.getId(),
                    director.getId());
        }
    }

    private void updateFilmGenresTable(Film film) {
        cleanOldFilmGenresRecords(film);
        fillFilmGenresTable(film);
    }

    private void updateFilmDirectorsTable(Film film) {
        cleanOldFilmDirectorsRecords(film);
        fillFilmDirectorsTable(film);
    }

    public void cleanOldFilmGenresRecords(Film film) {
        String sqlQuery = "delete from FILM_GENRES where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    public void cleanOldFilmDirectorsRecords(Film film) {
        String sqlQuery = "delete from FILM_DIRECTORS where film_id = ?";
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

