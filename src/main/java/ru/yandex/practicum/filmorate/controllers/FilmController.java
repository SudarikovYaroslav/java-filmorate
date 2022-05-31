package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmIdGenerator;

import java.util.ArrayList;
import java.util.List;

import static ru.yandex.practicum.filmorate.model.Constants.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController extends Controller<Film> {

    @PostMapping
    public Film add(@RequestBody Film film) throws InvalidFilmException {
        isNull(film);
        validate(film);
        film.setId(FilmIdGenerator.generate());
        log.debug(ASSIGNED_FILM_ID_LOG + film.getId());
        data.put(film.getId(), film);
        log.debug("Добавлен фильм: " + film.getName());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws InvalidFilmException {
        isNull(film);
        validate(film);

        if (data.containsKey(film.getId())) {
            data.put(film.getId(), film);
            log.debug("Обновлён фильм: " + film.getName());
        } else {
            log.warn(UPDATE_FILM_FAIL_LOG);
            throw new InvalidFilmException(UPDATE_FILM_FAIL_LOG);
        }
        return film;
    }

    @GetMapping
    public List<Film> get() {
        log.debug("Текущее количество фильмов: " + data.size());
        return new ArrayList<>(data.values());
    }

    @Override
    protected void validate(Film film) throws InvalidFilmException {
        if (film.getName() == null) {
            log.warn(NULL_FILM_FIELDS_LOG);
            throw new InvalidFilmException(NULL_FILM_FIELDS_LOG);
        }

        if (film.getName().isBlank()) {
            log.warn(BLANK_FILM_NAME_LOG);
            throw  new InvalidFilmException(BLANK_FILM_NAME_LOG);
        }

        if (film.getDescription() != null && film.getDescription().length() > MAX_FILM_DESCRIPTION_LENGTH) {
            log.warn(LONG_FILM_DESCRIPTION_LOG);
            throw new InvalidFilmException(LONG_FILM_DESCRIPTION_LOG);
        }

        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(FIRST_FILM_BIRTHDAY)) {
            log.warn(BAD_FILM_RELEASE_DATE_LOG);
            throw new InvalidFilmException(BAD_FILM_RELEASE_DATE_LOG);
        }

        if (film.getDuration() < 0) {
            log.warn(NEGATIVE_FILM_DURATION_LOG);
            throw new InvalidFilmException(NEGATIVE_FILM_DURATION_LOG);
        }

        if (film.getId() < 0) {
            log.warn(NEGATIVE_FILM_ID_LOG);
            throw new InvalidFilmException(NEGATIVE_FILM_ID_LOG);
        }
    }

    private void isNull(Film film) {
        log.warn(NULL_FILM_LOG);
        if (film == null) throw new NullPointerException(NULL_FILM_LOG);
    }
}
