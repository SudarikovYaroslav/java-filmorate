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
import java.util.stream.Stream;

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
        feedDao.saveFeed(new Feed(1, Instant.now().toEpochMilli(),
                userId, "LIKE", "ADD", filmId));
        markDao.addMark(filmId, userId, mark);
    }

    public void deleteLike(long filmId, long userId) {
        filmDao.findFilmById(filmId);
        userDao.findUserById(userId);
        feedDao.saveFeed(new Feed(1, Instant.now().toEpochMilli(),
                userId, "LIKE", "REMOVE", filmId));
        markDao.deleteMark(filmId, userId);
    }

    public List<Film> getTopFilms(Integer count, Long genreId, Integer year) {
        return getStreamFilmsByGenreId(getStreamFilmsByYear(filmDao.findAll().stream(), year), genreId)
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
        return filmDao.getCommonFilms(userId, friendId);
    }

    public void deleteFilmById(Long filmId) {
        filmDao.findFilmById(filmId);
        filmDao.deleteFilmById(filmId);
    }

    public List<Film> getDirectorFilms(long directorId, SortingType sortBy) {
        directorDao.findDirectorById(directorId);
        return filmDao.getDirectorFilms(directorId, sortBy);
    }

}
