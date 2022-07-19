package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        return mpaRatingDao.findMpaRatingById(id);
    }
}
