package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IllegalIdException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaRatingDao;

import java.util.List;

@Service
public class MpaService {

    private final MpaRatingDao mpaRatingDao;

    @Autowired
    public MpaService(MpaRatingDao mpaRatingDao) {
        this.mpaRatingDao = mpaRatingDao;
    }

    public List<Mpa> findAllMpaRatings() {
        return mpaRatingDao.findAllMpaRatings();
    }

    public Mpa findMpaRatingById(long id) {
        checkId(id);
        return mpaRatingDao.findMpaRatingById(id);
    }

    private void checkId(long id) {
        if (id < 0) throw new IllegalIdException("mpa id не может быть отрицательным");
    }
}
