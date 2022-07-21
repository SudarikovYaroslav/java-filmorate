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

    public List<Film> getTopFilms(Integer count) {
        List<Film> result = filmDao.findAll();
        result.sort((f1, f2) -> (likeDao.likesNumber(f2.getId()) - likeDao.likesNumber(f1.getId())));
        if (result.size() <= count) return result;
        return result.subList(0, count);
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
