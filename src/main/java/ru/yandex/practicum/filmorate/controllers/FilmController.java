package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmStorage filmStorage = new InMemoryFilmStorage();

    @PostMapping
    public Film add(@RequestBody Film film) throws InvalidFilmException {
        return filmStorage.add(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws InvalidFilmException {
        return filmStorage.update(film);
    }

    @GetMapping
    public List<Film> get() {
        return filmStorage.get();
    }
}
