package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IllegalIdException;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.FilmDao;
import ru.yandex.practicum.filmorate.storage.dao.LikeDao;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FilmService {
    private final FilmDao filmDao;
    private final LikeDao likeDao;

    @Autowired
    public FilmService(@Qualifier("dbFilmDaoImpl") FilmDao filmDao,
                       LikeDao likeDao) {
        this.filmDao = filmDao;
        this.likeDao = likeDao;
    }

    public Film add(Film film) throws InvalidFilmException {
        return filmDao.save(film);
    }

    public Film update(Film film) throws InvalidFilmException, IllegalIdException {
        return filmDao.update(film);
    }

    public List<Film> get() {
        return filmDao.findAll();
    }

    public Film getFilmById(long id) throws IllegalIdException {
        return filmDao.findFilmById(id).orElse(null);
    }

    public void addLike(long filmId, long userId) throws IllegalIdException {
        likeDao.addLike(filmId, userId);
    }

    public void deleteLike(long filmId, long userId) throws IllegalIdException {
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
}
