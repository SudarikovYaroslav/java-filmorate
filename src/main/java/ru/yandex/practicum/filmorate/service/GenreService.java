package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;

import java.util.List;

@Service
public class GenreService {
    private final GenreDao genreDao;
    private final ValidationService validationService;

    @Autowired
    public GenreService(GenreDao genreDao, ValidationService validationService) {
        this.genreDao = genreDao;
        this.validationService = validationService;
    }

    public List<Genre> findAllGenres() {
        return genreDao.findAllGenres();
    }

    public Genre findGenreById(long id) {
        validationService.checkId(id);
        return genreDao.findGenreById(id);
    }
}
