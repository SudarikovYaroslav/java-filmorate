package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.sorts.SortingType;
import ru.yandex.practicum.filmorate.storage.dao.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmDao filmDao;
    private final MarkDao markDao;
    private final FeedDao feedDao;
    private final DirectorDao directorDao;
    private final UserDao userDao;
    private final ValidationService validationService;

    @Autowired
    public FilmService(FilmDao filmDao,
                       MarkDao markDao,
                       FeedDao feedDao,
                       DirectorDao directorDao,
                       UserDao userDao, ValidationService validationService) {
        this.filmDao = filmDao;
        this.markDao = markDao;
        this.directorDao = directorDao;
        this.feedDao = feedDao;
        this.userDao = userDao;
        this.validationService = validationService;
    }

    public Film add(Film film) {
        validationService.validate(film);
        return filmDao.save(film);
    }

    public Film update(Film film) {
        validationService.validate(film);
        return filmDao.update(film);
    }

    public List<Film> get() {
        return filmDao.findAll();
    }

    public Film getFilmById(long id) {
        return filmDao.findFilmById(id).orElse(null);
    }

    public List<Film> searchFilms(String query, String directorAndTitle) {
        return filmDao.searchFilms(query, directorAndTitle);
    }

    public void addMark(long filmId, long userId, int mark) {
        filmDao.findFilmById(filmId);
        userDao.findUserById(userId);
        feedDao.saveFeed(new Feed(Instant.now().toEpochMilli(),
                userId, "LIKE", "ADD", filmId));
        markDao.addMark(filmId, userId, mark);
    }

    public void deleteLike(long filmId, long userId) {
        filmDao.findFilmById(filmId);
        userDao.findUserById(userId);
        feedDao.saveFeed(new Feed(Instant.now().toEpochMilli(),
                userId, "LIKE", "REMOVE", filmId));
        markDao.deleteMark(filmId, userId);
    }

    public List<Film> getTopFilms(Integer count, Long genreId, Integer year) {
        if (genreId == null && year == null) {
            return filmDao.findAll().stream().limit(count).collect(Collectors.toList());
        } else if (genreId == null) {
            return filmDao.findFilmByYear(year).stream().limit(count).collect(Collectors.toList());
        } else if (year == null) {
            return filmDao.findFilmByGenre(genreId).stream().limit(count).collect(Collectors.toList());
        } else {
            return filmDao.findFilmByGenreAndYear(genreId, year).stream().limit(count).collect(Collectors.toList());
        }
    }
    public List<Film> getCommonFilms(long userId, long friendId) {
        return filmDao.getCommonFilms(userId, friendId);
    }

    public void deleteFilmById(Long filmId) {
        filmDao.deleteFilmById(filmId);
    }

    public List<Film> getDirectorFilms(long directorId, SortingType sortBy) {
        directorDao.findDirectorById(directorId);
        return filmDao.getDirectorFilms(directorId, sortBy);
    }

}
