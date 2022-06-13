package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film add(Film film) throws InvalidFilmException;

    Film update(Film film) throws InvalidFilmException, FilmNotFoundException;

    List<Film> get();

    Film getFilm(long id) throws FilmNotFoundException;
}