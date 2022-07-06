package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film add(Film film) throws InvalidFilmException {
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
                film.getMpaRating()
        );
        
        String sqlQueryLikes = "insert into LIKES (film_id, user_id) values (?, ?)";

        for (long userId : film.getLikes()) {
            jdbcTemplate.update(sqlQueryLikes, film.getId(), userId);
        }

        String sqlQueryFilmGenres = "insert into FILM_GENRES (film_id, genre_id) values (?, ?)";

        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sqlQueryFilmGenres, film.getId(), genre.getId());
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
                film.getMpaRating(),
                film.getId()
        );

        String sqlQueryLikes = "update LIKES user_id = ? where film_id = ?";

        for (long userId : film.getLikes()) {
            jdbcTemplate.update(sqlQueryLikes, userId, film.getId());
        }

        String sqlQueryFilmGenres = "update FILM_GENRES genre_id = ? where film_id = ?";

        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sqlQueryFilmGenres, genre.getId(), film.getId());
        }
        return film;
    }

    @Override
    public List<Film> get() {
        String sqlQuery = "select * from FILMS";
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    @Override
    public Film getFilm(long id) throws FilmNotFoundException {
        String sqlQuery = "select * from FILMS where film_id = " + id;
        return jdbcTemplate.query(sqlQuery, this::makeFilm).get(0);
    }

    private MpaRating findRatingById(long ratingId) {
        String sqlQuery = "select * from MPA_RATINGS where mpa_rating_id = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeMpaRatingById(rs)).get(0);
    }

    private Set<Long> findLikesByFilmId(long filmId) {
        String sqlQuery = "select user_id where film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUserId(rs)));
    }

    private List<Genre> findGenresByFilmId(long filmId) {
        String sqlQuery = "select distinct * from FILM_GENRES as fg " +
                          "join GENRES AS g on fg.genre_id=g.genre_id where film_id = " + filmId;
        ;
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeGenre(rs));
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        long filmId = rs.getLong("film_id");
        return Film.builder()
                .id(filmId)
                .name(rs.getString("film_name"))
                .description((rs.getString("description")))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getLong("duration"))
                .mpaRating(findRatingById(rs.getLong("mpa_rating")))
                .likes(findLikesByFilmId(filmId))
                .genres(findGenresByFilmId(filmId))
                .build();
    }

    private MpaRating makeMpaRatingById(ResultSet rs) throws SQLException {
        return MpaRating.builder()
                .id(rs.getLong("mpa_rating_id"))
                .name(rs.getString("mpa_name"))
                .description(rs.getString("description"))
                .build();
    }

    private Long makeUserId(ResultSet rs) throws SQLException {
        return rs.getLong("user_id");
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("genre_name"))
                .description(rs.getString("description"))
                .build();
    }
}
