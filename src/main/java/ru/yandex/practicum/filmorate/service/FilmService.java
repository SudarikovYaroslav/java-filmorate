package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.sorts.SortingType;
import ru.yandex.practicum.filmorate.storage.dao.FeedDao;
import ru.yandex.practicum.filmorate.storage.dao.FilmDao;
import ru.yandex.practicum.filmorate.storage.dao.LikeDao;
import ru.yandex.practicum.filmorate.storage.impl.DbFeedDaoImpl;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FilmService {

    private final FilmDao filmDao;
    private final LikeDao likeDao;
    private final FeedDao feedDao;
    private final DirectorService directorService;
    private final ValidationService validationService;

    @Autowired
    public FilmService(@Qualifier("dbFilmDaoImpl") FilmDao filmDao,
                       LikeDao likeDao,
                       DirectorService directorService,
                       FeedDao feedDao,
                       ValidationService validationService) {
        this.filmDao = filmDao;
        this.likeDao = likeDao;
        this.directorService = directorService;
        this.feedDao = feedDao;
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
        validationService.validateFilmId(id);
        return filmDao.findFilmById(id).orElse(null);
    }

    public List<Film> searchFilms(String query, String directorAndTitle) {
        List<Film> result = filmDao.searchFilms(query, directorAndTitle);
        result.sort((f1, f2) -> (likeDao.likesNumber(f2.getId()) - likeDao.likesNumber(f1.getId())));
        return result;
    }

    public void addLike(long filmId, long userId) {
        validationService.validateFilmId(filmId);
        validationService.validateUserId(userId);
        feedDao.saveFeed(new Feed(1, Instant.now().toEpochMilli(),
                userId, "LIKE", "ADD", filmId));
        likeDao.addLike(filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        validationService.validateFilmId(filmId);
        validationService.validateUserId(userId);
        feedDao.saveFeed(new Feed(1, Instant.now().toEpochMilli(),
                userId, "LIKE", "REMOVE", filmId));
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
        validationService.validateFilmId(filmId);
        filmDao.deleteFilmById(filmId);
    }

    public List<Film> getDirectorFilms(long directorId, SortingType sortBy) {
        validationService.validateDirectorId(directorId);
        directorService.checkIfDirectorExists(directorId);
        return filmDao.getDirectorFilms(directorId, sortBy);
    }
}
