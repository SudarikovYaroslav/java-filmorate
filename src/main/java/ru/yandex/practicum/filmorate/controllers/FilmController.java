package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    public static final long MAX_FILM_DESCRIPTION_LENGTH = 200L;
    public static final LocalDate FIRST_FILM_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER, 28);

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film add(@RequestBody Film film) throws InvalidFilmException {
        validate(film);
        return filmService.add(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws InvalidFilmException, FilmNotFoundException {
        validate(film);
        return filmService.update(film);
    }

    @GetMapping
    public List<Film> get() {
        return filmService.get();
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) throws FilmNotFoundException {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) throws FilmNotFoundException {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular?count={count}")
    public List<Film> getTopFilms(@PathVariable Integer count) {
        return filmService.getTopFilms(count);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(InvalidFilmException e) {
        return new ErrorResponse("Film validation error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(FilmNotFoundException e) {
        return new ErrorResponse("Not found error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(Exception e) {
        return new ErrorResponse("Internal server error", e.getMessage());
    }

    private void validate(Film film) throws InvalidFilmException {
        validateNotNull(film);

        if (film.getName() == null) {
            String message = "Объект Film некорректно инициализирован, есть null поля! id: " + film.getId();
            log.warn(message);
            throw new InvalidFilmException(message);
        }

        if (film.getName().isBlank()) {
            String message = "Пустое имя фильма при инициализации id: " + film.getId();
            log.warn(message);
            throw  new InvalidFilmException(message);
        }

        if (film.getDescription() != null && film.getDescription().length() > MAX_FILM_DESCRIPTION_LENGTH) {
            String message = "Описание длиннее " + MAX_FILM_DESCRIPTION_LENGTH + " id: " + film.getId();
            log.warn(message);
            throw new InvalidFilmException(message);
        }

        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(FIRST_FILM_BIRTHDAY)) {
            String message = "Дата релиза раньше ДР кино:" + FIRST_FILM_BIRTHDAY + " id: " + film.getId();
            log.warn(message);
            throw new InvalidFilmException(message);
        }

        if (film.getDuration() < 0) {
            String message = "Отрицательная продолжительность фильма  id: " + film.getId();
            log.warn(message);
            throw new InvalidFilmException(message);
        }

        if (film.getId() < 0) {
            String message = "У фильма отрицательный id. id: " + film.getId();
            log.warn(message);
            throw new InvalidFilmException(message);
        }
    }

    private void validateNotNull(Film film) {
        if (film == null) {
            String message = "Передан null film";
            log.warn(message);
            throw new IllegalStateException(message);
        }
    }
}
