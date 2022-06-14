package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film add(Film film) throws InvalidFilmException {
        return filmStorage.add(film);
    }

    public Film update(Film film) throws InvalidFilmException, FilmNotFoundException {
        return filmStorage.update(film);
    }

    public List<Film> get() {
        return filmStorage.get();
    }

    public Film getFilmById(long id) throws FilmNotFoundException {
        return filmStorage.getFilm(id);
    }

    public void addLike(long filmId, long userId) throws FilmNotFoundException {
        filmStorage.getFilm(filmId).addLike(userId);
    }

    public void deleteLike(long filmId, long userId) throws FilmNotFoundException {
        filmStorage.getFilm(filmId).deleteLike(userId);
    }

    public List<Film> getTopFilms(Integer count) {
        List<Film> result = new ArrayList<>(filmStorage.get());
        result.sort((f1, f2) -> f2.likesNumber() - f1.likesNumber());

        if (result.size() <= count) return result;
        return result.subList(0, count);
    }
}
