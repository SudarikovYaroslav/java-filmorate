package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IllegalIdException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;

import java.util.List;

@Service
public class GenreService {
    private final GenreDao genreDao;

    @Autowired
    public GenreService(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public List<Genre> findAllGenres() {
        return genreDao.findAllGenres();
    }

    public Genre findGenreById(long id) {
        checkId(id);
        return genreDao.findGenreById(id);
    }

    private void checkId(long id) {
        if (id < 0) throw new IllegalIdException("У жанра не может быть отрицательный id");
    }
}
