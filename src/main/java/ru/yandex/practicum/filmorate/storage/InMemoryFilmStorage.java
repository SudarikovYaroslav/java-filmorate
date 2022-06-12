package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.generators.FilmIdGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film add(Film film) throws InvalidFilmException {
        film.setId(FilmIdGenerator.generate());
        log.debug("Фильму присвоен id: " + film.getId());
        films.put(film.getId(), film);
        log.debug("Добавлен фильм: " + film.getName());
        return film;
    }

    @Override
    public Film update(Film film) throws InvalidFilmException {
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

    @Override
    public Film getFilm(long id) {
        return films.get(id);
    }
}
