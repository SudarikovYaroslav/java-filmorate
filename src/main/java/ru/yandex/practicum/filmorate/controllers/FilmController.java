package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private static final long MAX_DESCRIPTION_LENGTH = 200L;
    private static final LocalDate FIRST_FILM_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER, 28);

    private final Set<Film> films = new HashSet<>();

    @PostMapping
    public void addFilm(@RequestBody Film film) throws InvalidFilmException {
        validateFilm(film);
        films.add(film);
        log.debug("Добавлен фильм: " + film.getName());
    }

    @PutMapping
    public void updateFilm(@RequestBody Film film) throws InvalidFilmException {
        validateFilm(film);
        films.add(film);
        log.debug("Обновлён фильм: " + film.getName());
    }

    @GetMapping
    public Set<Film> getFilms() {
        log.debug("Текущее количество фильмов: " + films.size());
        return films;
    }

    private void validateFilm(Film film) throws InvalidFilmException {
        if (film == null) {
            log.warn("Передано пустое значение фильма");
            throw new NullPointerException("Передано пустое значение фильма!");
        }

        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Передано пустое название фильма");
            throw new InvalidFilmException("Название фильма не может быть пустым!");
        }

        if (film.getDescription() != null && film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            log.warn("Передана недопустимая длинна описания фильма");
            throw new InvalidFilmException(
                    "Максимальная длинна описания фильма: " + MAX_DESCRIPTION_LENGTH + " символов!");
        }

        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(FIRST_FILM_BIRTHDAY)) {
            log.warn("Передана некорректная дата релиза фильма");
            throw new InvalidFilmException(
                    "Дата релиза не может быть раньше чем день рождения кино: " + FIRST_FILM_BIRTHDAY);
        }

        if (film.getDuration() != null && film.getDuration().getSeconds() < 0) {
            log.warn("Передана отрицательная продолжительность фильма");
            throw new InvalidFilmException("Продолжительность фильма должна быть положительной!");
        }
    }

    public static long getMaxDescriptionLength() {
        return MAX_DESCRIPTION_LENGTH;
    }
}
