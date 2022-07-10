package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.LikesDao;
import ru.yandex.practicum.filmorate.storage.dao.MpaRatingDao;

import java.util.List;

@Service
public class FilmService {

    private final FilmStorageDao filmStorageDao;
    private final LikesDao likesDao;
    private final GenreDao genreDao;

    @Autowired
    public FilmService(@Qualifier("filmDbStorageDaoImpl") FilmStorageDao filmStorageDao,
                       LikesDao likesDao,
                       GenreDao genreDao,
                       MpaRatingDao mpaRatingDao) {
        this.filmStorageDao = filmStorageDao;
        this.likesDao = likesDao;
        this.genreDao = genreDao;
    }

    public Film add(Film film) throws InvalidFilmException {
        return filmStorageDao.save(film);
    }

    public Film update(Film film) throws InvalidFilmException, FilmNotFoundException {
        return filmStorageDao.update(film);
    }

    public List<Film> get() {
        return filmStorageDao.findAll();
    }

    public Film getFilmById(long id) throws FilmNotFoundException {
        return filmStorageDao.findFilmById(id).orElse(null);
    }

    public void addLike(long filmId, long userId) throws FilmNotFoundException {
        likesDao.addLike(filmId, userId);
    }

    public void deleteLike(long filmId, long userId) throws FilmNotFoundException {
        likesDao.deleteLike(filmId, userId);
    }

    public List<Film> getTopFilms(Integer count) {
        List<Film> result = filmStorageDao.findAll();
        result.sort((f1, f2) -> (likesDao.likesNumber(f2.getId()) - likesDao.likesNumber(f1.getId())));

        if (result.size() <= count) return result;
        return result.subList(0, count);
    }
}
