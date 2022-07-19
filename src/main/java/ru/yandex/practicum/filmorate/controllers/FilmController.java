package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.IllegalIdException;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    public static final String TOP_FILMS_COUNT = "10";

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film add(@RequestBody Film film) throws InvalidFilmException {
        return filmService.add(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws InvalidFilmException {
        return filmService.update(film);
    }

    @GetMapping
    public List<Film> get() {
        return filmService.get();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId)
            throws IllegalIdException {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId)
            throws IllegalIdException {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = TOP_FILMS_COUNT) Integer count) {
        return filmService.getTopFilms(count);
    }

    @DeleteMapping(value = "/{filmId}")
    public void deleteFilmById(@PathVariable Long filmId) {
        filmService.deleteFilmById(filmId);
    }
}
