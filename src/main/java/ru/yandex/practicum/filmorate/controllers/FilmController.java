package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.IdGenerator;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController extends Controller<Film> {
    public static final long MAX_DESCRIPTION_LENGTH = 200L;
    public static final LocalDate FIRST_FILM_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER, 28);
    public static final String NULL_FILM_LOG = "Передан null film";
    public static final String NULL_FIELDS_LOG = "Объект Film некорректно инициализирован, есть null поля!";
    public static final String BLANK_NAME_LOG = "Пустое имя фильма при инициализации";
    public static final String TOO_LONG_DESCRIPTION_LOG = "Описание больше " + MAX_DESCRIPTION_LENGTH + " символов";
    public static final String BAD_RELEASE_DATE_LOG = "Дата релиза раньше дня рождения кино: " + FIRST_FILM_BIRTHDAY;
    public static final String NEGATIVE_DURATION_LOG = "Отрицательная продолжительность фильма";
    public static final String NEGATIVE_ID_LOG = "У фильма отрицательный id";
    public static final String ASSIGNED_ID_LOG = "Фильму присвоен id: ";

    @PostMapping
    public Film add(@RequestBody Film film) throws InvalidFilmException {
        isNull(film);
        validate(film);
        film.setId(IdGenerator.generateId());
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
        }

        return film;
    }

    @GetMapping
    public Collection<Film> get() {
        log.debug("Текущее количество фильмов: " + data.size());
        return data.values();
    }

    @Override
    protected void validate(Film film) throws InvalidFilmException {
        if (film.getName() == null) {
            log.warn(NULL_FIELDS_LOG);
            throw new InvalidFilmException(NULL_FIELDS_LOG);
        }

        if (film.getName().isBlank()) {
            log.warn(BLANK_NAME_LOG);
            throw  new InvalidFilmException(BLANK_NAME_LOG);
        }

        if (film.getDescription() != null && film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            log.warn(TOO_LONG_DESCRIPTION_LOG);
            throw new InvalidFilmException(TOO_LONG_DESCRIPTION_LOG);
        }

        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(FIRST_FILM_BIRTHDAY)) {
            log.warn(BAD_RELEASE_DATE_LOG);
            throw new InvalidFilmException(BAD_RELEASE_DATE_LOG);
        }

        if (film.getDuration() != null && film.getDuration().isNegative()) {
            log.warn(NEGATIVE_DURATION_LOG);
            throw new InvalidFilmException(NEGATIVE_DURATION_LOG);
        }

        if (film.getId() < 0) {
            log.warn(NEGATIVE_ID_LOG);
            throw new InvalidFilmException(NEGATIVE_ID_LOG);
        }
    }

    private void isNull(Film film) {
        log.warn(NULL_FILM_LOG);
        if (film == null) throw new NullPointerException(NULL_FILM_LOG);
    }
}
