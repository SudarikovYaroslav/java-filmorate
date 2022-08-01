package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.IllegalIdException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.sorts.SortingType;
import ru.yandex.practicum.filmorate.storage.dao.FilmDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
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

    public static String RATE_COLUMN = "rate";


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
    public Film update(Film film) {
        findFilmById(film.getId());
        String sqlQueryFilms =
                "update FILMS set " +
                        "film_name = ?, description = ?, release_date = ?, duration = ?, rate = ?, mpa_rating_id = ? " +
                        "where film_id = ?";
        jdbcTemplate.update(sqlQueryFilms,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());
        if (film.getGenres() != null) updateFilmGenresTable(film);
        Mpa mpa = makeMpaById(film.getMpa().getId());
        film.setMpa(mpa);
        film.setGenres(findGenresByFilmId(film.getId()));

        updateFilmDirectorsTable(film);
        if (film.getDirectors() != null) {
            film.setDirectors(findDirectorsByFilmId(film.getId()));
            if (film.getDirectors().isEmpty()) film.setDirectors(null);
        }
        log.info("Обновлён фильм id: " + film.getId());
        return film;
    }

    @Override
    public List<Film> findAll() {
        String sqlQuery = "select * from FILMS";
        return jdbcTemplate.query(sqlQuery, this::makeFilm)
                .stream()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Film> findFilmById(long id) {
        String sqlQuery = "select * from FILMS where film_id = " + id;
        Film film = jdbcTemplate.query(sqlQuery, rs -> rs.next() ? makeFilm(rs, 0) : null);
        if (film == null) {
            throw new IllegalIdException(String.format("Фильм %d не найден", id));
        }
        return Optional.of(film);
    }

    @Override
    public List<Film> searchFilms(String query, String directorAndTitle) {
        String[] splitedRequest = directorAndTitle.split(",");
        switch (splitedRequest.length) {
            case (1):
                if (splitedRequest[0].equals("title")) {
                    return getFilmsByPartOfTitle(query);
                }
                return getFilmsByPartOfDirectorName(query);
            case (2):
                return getFilmsByPartOfTitleAndDirectorName(query);
        }
        return new ArrayList<>();
    }

    private List<Film> getFilmsByPartOfTitle(String filmNamePart) {
        String sqlForFilmsWithSearchedNames = "SELECT * FROM FILMS WHERE LCASE(FILM_NAME) " +
                "LIKE '%" + filmNamePart.toLowerCase() + "%' ORDER BY RATE DESC;";
        return jdbcTemplate.query(sqlForFilmsWithSearchedNames, this::makeFilm);
    }

    private List<Film> getFilmsByPartOfDirectorName(String directorNamePart) {
        String sqlForFilmsWithSearchedDirectors = "SELECT * FROM FILMS WHERE FILM_ID IN " +
                "(SELECT FILM_ID FROM FILM_DIRECTORS " +
                "where DIRECTOR_ID IN (SELECT DIRECTORS.DIRECTOR_ID FROM DIRECTORS " +
                "where LCASE(DIRECTOR_NAME) like '%" + directorNamePart.toLowerCase() + "%')) ORDER BY RATE DESC;";
        return jdbcTemplate.query(sqlForFilmsWithSearchedDirectors, this::makeFilm);
    }

    private List<Film> getFilmsByPartOfTitleAndDirectorName(String query) {
        String sql = "SELECT * FROM FILMS WHERE LCASE(FILM_NAME) " +
                "LIKE '%" + query.toLowerCase() + "%' and FILM_ID IN " +
                "(SELECT FILM_ID FROM FILM_DIRECTORS " +
                "where DIRECTOR_ID IN (SELECT DIRECTORS.DIRECTOR_ID FROM DIRECTORS " +
                "where LCASE(DIRECTOR_NAME) like '%" + query.toLowerCase() + "%')) ORDER BY RATE DESC;";
        return jdbcTemplate.query(sql, this::makeFilm);

    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        List<Film> firstUserFilms = findAllFavoriteMovies(userId);
        List<Film> secondUserFilms = findAllFavoriteMovies(friendId);
        firstUserFilms.retainAll(secondUserFilms);
        return firstUserFilms.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFilmById(Long filmId) {
        String sqlQuery = "delete from FILMS where film_id = ?;";
        jdbcTemplate.update(sqlQuery, filmId);
        log.debug("Удален фильм id: " + filmId);
    }

    @Override
    public List<Film> getDirectorFilms(long directorId, SortingType sortBy) {
        String sqlQuery = "select * from FILM_DIRECTORS as FD " +
                "join FILMS F on FD.FILM_ID = F.FILM_ID  " +
                "where DIRECTOR_ID = ?";
        List<Film> result = jdbcTemplate.query(sqlQuery, this::makeFilm, directorId);
        if (sortBy.equals(SortingType.LIKES)) {
            return result.stream()
                    .sorted()
                    .collect(Collectors.toList());
        }
        return result.stream()
                .sorted(this::compare)
                .collect(Collectors.toList());
    }

    private int compare(Film p0, Film p1) {
        return (p0.getReleaseDate().compareTo(p1.getReleaseDate()));
    }

    @Override
    public List<Film> findAllFavoriteMovies(Long id) {
        String sqlQuery = "select * " +
                "from MARKS L " +
                "join FILMS F on F.FILM_ID = L.FILM_ID " +
                "where L.USER_ID = ?; ";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs, 0), id);
    }

    @Override
    public List<Film> recommendationsFilm(Long id) {
        String sql = "with GENERAL_FILMS as ( " +
                "select " +
                "    l.USER_ID user_recommendations, " +
                "    count(1) cnt_films " +
                "from MARKS l " +
                "join MARKS l2 on l2.FILM_ID = l.FILM_ID " +
                "                     and l2.USER_ID != l.USER_ID " +
                "and l2.MARK > 5 and l.MARK > 5 " +
                "where l2.USER_ID = ? " +
                "group by user_recommendations " +
                "order by cnt_films desc " +
                ") " +
                "select * " +
                "from MARKS L " +
                "join FILMS F on F.FILM_ID = L.FILM_ID " +
                "where L.USER_ID = (select user_recommendations " +
                "from GENERAL_FILMS" +
                "                   group by user_recommendations" +
                "                   limit 1) AND L.MARK > 5;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs, 0), id);
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
                .rate(rs.getDouble(RATE_COLUMN))
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
        values.put(RATE_COLUMN, film.getRate());
        values.put(MPA_RATING_ID_COLUMN, film.getMpa().getId());
        return values;
    }

    @Override
    public List<Film> findFilmByYear(Integer year) {
        String sqlQuery = "select * from FILMS where FORMATDATETIME(FILMS.RELEASE_DATE,'yyyy') = " + year;
        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm);
        if (films.isEmpty()) {
            throw new IllegalIdException(String.format("Фильмов вышедших в %d году нет в базе", year));
        }
        return films;
    }

    @Override
    public List<Film> findFilmByGenre(Long genre) {
        String sqlQuery = "select * from FILMS join FILM_GENRES FG on FILMS.FILM_ID = FG.FILM_ID " +
                "where FG.GENRE_ID = " + genre;
        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm);
        if (films.isEmpty()) {
            throw new IllegalIdException(String.format("Фильмов в жанре %d нет в базе", genre));
        }
        return films;
    }

    @Override
    public List<Film> findFilmByGenreAndYear(Long genre, Integer year) {
        String sqlQuery = "select * from FILMS join FILM_GENRES FG on FILMS.FILM_ID = FG.FILM_ID " +
                "where FG.GENRE_ID = " + genre +
                " and FORMATDATETIME(FILMS.RELEASE_DATE,'yyyy') = " + year;
        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm);
        if (films.isEmpty()) {
            throw new IllegalIdException(String.format
                    ("Фильмов в жанре %d и вышедших в %d году нет в базе", genre, year));
        }
        return films;
    }
}