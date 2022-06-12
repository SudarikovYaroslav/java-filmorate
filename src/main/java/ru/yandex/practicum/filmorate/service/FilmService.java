package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {
    private static final String NULL_VALUE_MESSAGE = "передано null значение";
    private static final int TOP_FILMS_COUNT = 10;

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film add(Film film) throws InvalidFilmException {
        return filmStorage.add(film);
    }

    public Film update(Film film) throws InvalidFilmException {
        return filmStorage.update(film);
    }

    public List<Film> get() {
        return filmStorage.get();
    }

    public void addLike(User user, Film film) {
        if (user == null || film == null) throw new IllegalArgumentException(NULL_VALUE_MESSAGE);
        film.addLike(user);
    }

    public void deleteLike(User user, Film film) {
        if (user == null || film == null) throw new IllegalArgumentException(NULL_VALUE_MESSAGE);
        film.deleteLike(user);
    }

    public List<Film> getTopFilms() {
        List<Film> result = new ArrayList<>(filmStorage.get());
        result.sort(Comparator.comparingInt(Film::likes));

        if (result.size() <= TOP_FILMS_COUNT) return result;
        return result.subList(0, TOP_FILMS_COUNT);
    }
}
