package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

@RestController
public class FilmController {
    private static final long MAX_DESCRIPTION_LENGTH = 200L;
    private static final LocalDate FIRST_FILM_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER, 28);

    private final Set<Film> films = new HashSet<>();

    @PostMapping
    public void addFilm(@RequestBody Film film) throws InvalidFilmException {
        validateFilm(film);
        films.add(film);
    }

    @PutMapping
    public void updateFilm(@RequestBody Film film) throws InvalidFilmException {
        validateFilm(film);
        films.add(film);
    }

    @GetMapping
    public Set<Film> getFilms() {
        return films;
    }

    private void validateFilm(Film film) throws InvalidFilmException {
        if (film == null) throw new NullPointerException("Передано пустое значение фильма!");

        if (film.getName() == null || film.getName().isBlank()) throw new InvalidFilmException(
                "Название фильма не может быть пустым!");

        if (film.getDescription() != null && film.getDescription().length() > MAX_DESCRIPTION_LENGTH)
            throw new InvalidFilmException(
                    "Максимальная длинна описания фильма: " + MAX_DESCRIPTION_LENGTH + " символов!");

        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(FIRST_FILM_BIRTHDAY))
            throw new InvalidFilmException(
                "Дата релиза не может быть раньше чем день рождения кино: " + FIRST_FILM_BIRTHDAY);

        if (film.getDuration() != null && film.getDuration().getSeconds() < 0)
            throw new InvalidFilmException("Продолжительность фильма должна быть положительной!");
    }
}
