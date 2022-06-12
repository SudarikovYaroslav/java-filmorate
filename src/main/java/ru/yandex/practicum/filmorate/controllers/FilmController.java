package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

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
}
