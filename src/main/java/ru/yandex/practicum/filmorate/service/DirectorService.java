package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IllegalIdException;
import ru.yandex.practicum.filmorate.exceptions.InvalidDirectorException;
import ru.yandex.practicum.filmorate.exceptions.StorageException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dao.DirectorDao;

import java.util.List;
import java.util.Optional;

@Service
public class DirectorService {

    private final DirectorDao directorDao;

    @Autowired
    public DirectorService(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }

    public List<Director> get() {
        return directorDao.findAll();
    }

    public Director getDirectorById(long directorId) {
        checkId(directorId);
        Optional<Director> director = directorDao.findDirectorById(directorId);
        if (director.isEmpty()) throw new StorageException(String.format("Режиссёр с id: %d не найден", directorId));
        return director.get();
    }

    public Director add(Director director) {
        validateDirector(director);
        return directorDao.save(director);
    }

    public Director update(Director director) {
        checkIfDirectorExists(director.getId());
        validateDirector(director);
        return directorDao.update(director);
    }

    public void delete(long directorId) {
        checkId(directorId);
        checkIfDirectorExists(directorId);
        directorDao.delete(directorId);
    }

    private void validateDirector(Director director) {
        if (director.getId() <= 0
                || director.getName() == null
                || director.getName().isBlank()
        ) throw new InvalidDirectorException("Недопустимы значения поле director");
    }

    private void checkId(long id) {
        if (id < 0) throw new IllegalIdException("У режиссёра не может быть отрицательный id");
    }

    private void checkIfDirectorExists(long directorId) {
        getDirectorById(directorId);
    }
}
