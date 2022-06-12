package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.generators.FilmIdGenerator;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    public static final long MAX_FILM_DESCRIPTION_LENGTH = 200L;
    public static final LocalDate FIRST_FILM_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER, 28);

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film add(Film film) throws InvalidFilmException {
        validate(film);
        film.setId(FilmIdGenerator.generate());
        log.debug("Фильму присвоен id: " + film.getId());
        films.put(film.getId(), film);
        log.debug("Добавлен фильм: " + film.getName());
        return film;
    }

    @Override
    public Film update(Film film) throws InvalidFilmException {
        validate(film);

        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Обновлён фильм: " + film.getName());
        } else {
            String message = "Попытка обновить несуществующий фильм id: " + film.getId();
            log.warn(message);
            throw new InvalidFilmException(message);
        }
        return film;
    }

    @Override
    public List<Film> get() {
        log.debug("Текущее количество фильмов: " + films.size());
        return new ArrayList<>(films.values());
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
        String message = "Передан null film";
        log.warn(message);
        if (film == null) throw new IllegalStateException(message);
    }
}
