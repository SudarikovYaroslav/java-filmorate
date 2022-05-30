package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.IdGenerator;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController extends Controller<Film> {
    private static final long MAX_DESCRIPTION_LENGTH = 200L;
    private static final LocalDate FIRST_FILM_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER, 28);

    @PostMapping
    public void add(@RequestBody Film film) throws InvalidFilmException {
        validate(film);
        validateIdWhenAdd(film);
        data.put(film.getId(),film);
        log.debug("Добавлен фильм: " + film.getName());
    }

    @PutMapping
    public void update(@RequestBody Film film) throws InvalidFilmException {
        validate(film);
        validateIdWhenUpdate(film);
        data.put(film.getId(), film);
        log.debug("Обновлён фильм: " + film.getName());
    }

    @GetMapping
    public Collection<Film> get() {
        log.debug("Текущее количество фильмов: " + data.size());
        return data.values();
    }

    @Override
    protected void validate(Film film) throws InvalidFilmException {
        if (film == null) {
            log.warn("Передано пустое значение фильма");
            throw new InvalidFilmException("Передано пустое значение фильма!");
        }

        if (film.getName() == null) {
            log.warn("у переданного фильма не установлено название");
            throw new InvalidFilmException("Фильму не установлено название!");
        }

        if (film.getDescription() == null) {
            log.warn("Передана недопустимая длинна описания фильма");
            throw new InvalidFilmException("Не указано описание фильма!");
        }

        if (film.getReleaseDate() == null) {
            log.warn("Передана некорректная дата релиза фильма");
            throw new InvalidFilmException("Не указана дата выхода фильма!");
        }

        if (film.getDuration() == null) {
            log.warn("Передана некорректная продолжительность фильма");
            throw new InvalidFilmException("Не указана продолжительность фильма!");
        }

        if (film.getName().isBlank()) {
            log.warn("Передано пустое название фильма");
            throw new InvalidFilmException("Название фильма не может быть пустым!");
        }

        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            log.warn("Передана недопустимая длинна описания фильма");
            throw new InvalidFilmException(
                    "Максимальная длинна описания фильма: " + MAX_DESCRIPTION_LENGTH + " символов!");
        }

        if (film.getReleaseDate().isBefore(FIRST_FILM_BIRTHDAY)) {
            log.warn("Передана некорректная дата релиза фильма");
            throw new InvalidFilmException(
                    "Дата релиза не может быть раньше чем день рождения кино: " + FIRST_FILM_BIRTHDAY);
        }

        if (film.getDuration().getSeconds() < 0) {
            log.warn("Передана некорректная продолжительность фильма");
            throw new InvalidFilmException("Продолжительность фильма должна быть положительной!");
        }
    }

    public static long getMaxDescriptionLength() {
        return MAX_DESCRIPTION_LENGTH;
    }

    public static LocalDate getFirstFilmBirthday() {
        return FIRST_FILM_BIRTHDAY;
    }

    /**
     Для обеспечения не идемпотентности метода POST, в случае, если хранилище содержит фильм с id, таким же, как у
     передаваемого, переданному фильму присваивается новый id
     */
    private void validateIdWhenAdd(Film film) {
        if (film.getId() <= 0) {
            film.setId(IdGenerator.generateId());
            log.debug("У переданного фильма не установлен id, присвоен id = " + film.getId());
        }

        if (data.containsKey(film.getId())) {
            film.setId(IdGenerator.generateId());
            log.debug("Переданному фильму установлен id = " + film.getId());
        }
    }

    /**
     Если у переданного фильма не был установлен id то, чтобы избежать дублирования сначала проверяем хранилище
     на наличие фильма по названию,
     т.к. если фильм с таким названием был добавлен так же без id, то id ему уже был сгенерирован
     */
    private void validateIdWhenUpdate(Film film) throws InvalidFilmException {
        if (film.getId() == 0) {
            for (Film existedFilm : data.values()) {
                if (film.getName().equals(existedFilm.getName())) {
                    film.setId(existedFilm.getId());
                }
            }
            if (film.getId() <= 0) film.setId(IdGenerator.generateId());
            log.debug("У переданного фильма не установлен id, присвоен id=" + film.getId());
        }

        if (film.getId() < 0) {
            throw new InvalidFilmException("У обновляемого фильма не может быть отрицательный id!");
        }
    }
}
