package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
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
    private final FilmIdGenerator filmIdGenerator;

    @Autowired
    public InMemoryFilmStorage(FilmIdGenerator filmIdGenerator) {
        this.filmIdGenerator = filmIdGenerator;
    }

    @Override
    public Film add(Film film) throws InvalidFilmException {
        film.setId(filmIdGenerator.generate());
        log.debug("Фильму присвоен id: " + film.getId());
        films.put(film.getId(), film);
        log.debug("Добавлен фильм: " + film.getName() + " id: " + film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Обновлён фильм: " + film.getName());
        } else {
            String message = "Попытка обновить несуществующий фильм id: " + film.getId();
            log.warn(message);
            throw new FilmNotFoundException(message);
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
        if (!films.containsKey(id)) throw new FilmNotFoundException("Фильм с id: " + id + " не найден");
        return films.get(id);
    }
}
