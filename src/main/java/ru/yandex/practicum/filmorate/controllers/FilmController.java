package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.sorts.SortingType;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Validated
@RequestMapping("/films")
public class FilmController {
    public static final String TOP_FILMS_COUNT = "10";

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film add(@RequestBody Film film) {
        return filmService.add(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
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

    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilms(@PathVariable long directorId,
                                       @RequestParam("sortBy") SortingType sortBy) {
        return filmService.getDirectorFilms(directorId, sortBy);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addMark(@PathVariable long id, @PathVariable long userId,
                        @RequestParam(defaultValue = "10") @Min(1) @Max(10) int mark) {
        filmService.addMark(id, userId, mark);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteMark(@PathVariable long id, @PathVariable long userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = TOP_FILMS_COUNT) Integer count,
                                  @RequestParam(required = false) Long genreId,
                                  @RequestParam(required = false) Integer year) {
        return filmService.getTopFilms(count, genreId, year);
    }

    @DeleteMapping(value = "/{filmId}")
    public void deleteFilmById(@PathVariable Long filmId) {
        filmService.deleteFilmById(filmId);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(HttpServletRequest request) {
        return filmService.searchFilms(request.getParameter("query"), request.getParameter("by"));
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(HttpServletRequest request) {
        return filmService.getCommonFilms(request.getParameter("userId"), request.getParameter("friendId"));
    }

}
