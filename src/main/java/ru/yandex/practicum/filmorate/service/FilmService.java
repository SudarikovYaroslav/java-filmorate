package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IllegalIdException;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.FilmDao;
import ru.yandex.practicum.filmorate.storage.dao.LikeDao;
import ru.yandex.practicum.filmorate.storage.impl.DbFeedDaoImpl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class FilmService {
    public static final long MAX_FILM_DESCRIPTION_LENGTH = 200L;
    public static final LocalDate FIRST_FILM_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER, 28);

    private final FilmDao filmDao;
    private final LikeDao likeDao;
    private final DirectorService directorService;
    private final DbFeedDaoImpl dbFeedDaoImpl;

    @Autowired
    public FilmService(@Qualifier("dbFilmDaoImpl") FilmDao filmDao,
                       LikeDao likeDao, DirectorService directorService, DbFeedDaoImpl dbFeedDaoImpl) {
        this.filmDao = filmDao;
        this.likeDao = likeDao;
        this.directorService = directorService;
        this.dbFeedDaoImpl = dbFeedDaoImpl;
    }

    public Film add(Film film) throws InvalidFilmException {
        validate(film);
        return filmDao.save(film);
    }

    public Film update(Film film) throws InvalidFilmException, IllegalIdException {
        validate(film);
        return filmDao.update(film);
    }

    public List<Film> get() {
        return filmDao.findAll();
    }

    public Film getFilmById(long id) throws IllegalIdException {
        checkFilmId(id);
        return filmDao.findFilmById(id).orElse(null);
    }

    public List<Film> searchFilms(String query, String directorAndTitle) {
        List<Film> result = filmDao.searchFilms(query, directorAndTitle);
        result.sort((f1, f2) -> (likeDao.likesNumber(f2.getId()) - likeDao.likesNumber(f1.getId())));
        return result;
    }

    public void addLike(long filmId, long userId) throws IllegalIdException {
        checkFilmId(filmId);
        checkUserId(userId);
        dbFeedDaoImpl.saveFeed(new Feed(Instant.now().toEpochMilli(),
                userId, "LIKE", "ADD", 1, filmId));
        likeDao.addLike(filmId, userId);
    }

    public void deleteLike(long filmId, long userId) throws IllegalIdException {
        checkFilmId(filmId);
        checkUserId(userId);
        dbFeedDaoImpl.saveFeed(new Feed(Instant.now().toEpochMilli(),
                userId, "LIKE", "REMOVE", 1, filmId));
        likeDao.deleteLike(filmId, userId);
    }

    public List<Film> getTopFilms(Integer count, Long genreId, Integer year) {
        return getStreamFilmsByGenreId(getStreamFilmsByYear(filmDao.findAll().stream(), year), genreId)
                .sorted((f1, f2) -> (likeDao.likesNumber(f2.getId()) - likeDao.likesNumber(f1.getId())))
                .limit(count)
                .collect(Collectors.toList());
    }

    private Stream<Film> getStreamFilmsByGenreId(Stream<Film> filmStream, Long genreId) {
        return genreId == null ? filmStream
                : filmStream.filter(film -> film.getGenres().stream().anyMatch(g -> genreId.equals(g.getId())));
    }

    private Stream<Film> getStreamFilmsByYear(Stream<Film> filmStream, Integer year) {
        return year == null ? filmStream : filmStream.filter(film -> year.equals(film.getReleaseDate().getYear()));
    }

    public List<Film> getCommonFilms(String userId, String friendId) {
        List<Film> result = filmDao.getCommonFilms(userId, friendId);
        result.sort((f1, f2) -> (likeDao.likesNumber(f2.getId()) - likeDao.likesNumber(f1.getId())));
        return result;
    }

    public void deleteFilmById(Long filmId) {
        checkFilmId(filmId);
        filmDao.deleteFilmById(filmId);
    }

    public List<Film> getDirectorFilms(long directorId, String sortBy) {
        checkDirectorId(directorId);
        directorService.checkIfDirectorExists(directorId);
        return filmDao.getDirectorFilms(directorId, sortBy);
    }

    private void validate(Film film) throws InvalidFilmException {
        validateNotNull(film);
        checkFilmId(film.getId());
        if (film.getName() == null) {
            String message = "Объект Film некорректно инициализирован, есть null поля! id: " + film.getId();
            log.warn(message);
            throw new InvalidFilmException(message);
        }
        if (film.getName().isBlank()) {
            String message = "Пустое имя фильма при инициализации id: " + film.getId();
            log.warn(message);
            throw new InvalidFilmException(message);
        }
        if (film.getDescription() != null && film.getDescription().length() > MAX_FILM_DESCRIPTION_LENGTH) {
            String message = "Описание длиннее " + MAX_FILM_DESCRIPTION_LENGTH + " id: " + film.getId();
            log.warn(message);
            throw new InvalidFilmException(message);
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(FIRST_FILM_BIRTHDAY)) {
            String message = "Дата релиза раньше ДР кино:" + FIRST_FILM_BIRTHDAY + " id: " + film.getId();
            log.warn(message);
            throw new InvalidFilmException(message);
        }
        if (film.getDuration() < 0) {
            String message = "Отрицательная продолжительность фильма  id: " + film.getId();
            log.warn(message);
            throw new InvalidFilmException(message);
        }
        if (film.getMpa() == null) {
            throw new InvalidFilmException(String.format("у фильма id: %d не установлен mpa", film.getId()));
        }
    }

    private void validateNotNull(Film film) {
        if (film == null) {
            String message = "Передан null film";
            log.warn(message);
            throw new IllegalStateException(message);
        }
    }

    public void checkFilmId(long id) {
        if (id < 0) throw new IllegalIdException("film id:" + id + " отрицательный");
    }

    public void checkUserId(long id) {
        if (id < 0) throw new IllegalIdException("user id: " + id + " отрицательный");
    }

    public void checkDirectorId(long id) {
        if (id < 0) throw new IllegalIdException("director id: " + id + " отрицательный");
    }
}
