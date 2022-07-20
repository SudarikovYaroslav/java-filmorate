package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorDao {
    List<Director> findAll();

    Optional<Director> findDirectorById(long directorId);

    Director save(Director director);

    Director update(Director director);

    void delete(long directorId);
}
