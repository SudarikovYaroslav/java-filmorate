package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.StorageException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dao.DirectorDao;

import java.util.List;
import java.util.Optional;

@Service
public class DirectorService {

    private final DirectorDao directorDao;
    private final ValidationService validationService;

    @Autowired
    public DirectorService(DirectorDao directorDao, ValidationService validationService) {
        this.directorDao = directorDao;
        this.validationService = validationService;
    }

    public List<Director> get() {
        return directorDao.findAll();
    }

    public Director getDirectorById(long directorId) {
        return directorDao.findDirectorById(directorId);
    }

    public Director add(Director director) {
        validationService.validateDirector(director);
        return directorDao.save(director);
    }

    public Director update(Director director) {
        checkIfDirectorExists(director.getId());
        validationService.validateDirector(director);
        return directorDao.update(director);
    }

    public void delete(long directorId) {
        directorDao.delete(directorId);
    }

    public void checkIfDirectorExists(long directorId) {
        getDirectorById(directorId);
    }
}
