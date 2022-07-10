package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
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
    public Film update(@RequestBody Film film) throws InvalidFilmException {
        validate(film);
        return filmService.update(film);
    }

    @GetMapping
    public List<Film> get() {
        return filmService.get();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {
        checkFilmId(id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId)
            throws FilmNotFoundException, UserNotFoundException {
        checkFilmId(id);
        checkUserId(userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId)
            throws FilmNotFoundException, UserNotFoundException {
        checkFilmId(id);
        checkUserId(userId);
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getTopFilms(count);
    }

    private void validate(Film film) throws InvalidFilmException {
        validateNotNull(film);

        checkFilmId(film.getId());

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

        if (film.getMpa() == null) {
            throw new InvalidFilmException(String.format("у фильма id: %d не установлен mpa", film.getId()));
        }
    }

    private void validateNotNull(Film film) {
        if (film == null) {
            String message = "Передан null film";
            log.warn(message);
            throw new IllegalStateException(message);
        }
    }

    public void checkFilmId(long id) {
        if (id < 0 ) throw new FilmNotFoundException("film id:" + id + " не найден");
    }

    public void checkUserId(long id) {
        if (id <= 0) throw new UserNotFoundException("user id: " + id + " не найден");
    }
}
