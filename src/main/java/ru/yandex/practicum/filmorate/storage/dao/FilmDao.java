package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.exceptions.IllegalIdException;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.sorts.SortingType;

import java.util.List;
import java.util.Optional;

public interface FilmDao {
    Film save(Film film) throws InvalidFilmException;

    Film update(Film film) throws InvalidFilmException, IllegalIdException;

    List<Film> findAll();

    Optional<Film> findFilmById(long id) throws IllegalIdException;

    void deleteFilmById(Long filmId);

    List<Film> getDirectorFilms(long directorId, SortingType sortBy);

    List<Film> findAllFavoriteMovies(Long id);

    List<Film> recommendationsFilm(Long id);

    List<Film> searchFilms(String query, String directorAndTitle);

    List<Film> getCommonFilms(String userId, String friendId);
}
