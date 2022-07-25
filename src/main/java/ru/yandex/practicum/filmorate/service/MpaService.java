package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaRatingDao;

import java.util.List;

@Service
public class MpaService {

    private final MpaRatingDao mpaRatingDao;
    private final ValidationService validationService;

    @Autowired
    public MpaService(MpaRatingDao mpaRatingDao, ValidationService validationService) {
        this.mpaRatingDao = mpaRatingDao;
        this.validationService = validationService;
    }

    public List<Mpa> findAllMpaRatings() {
        return mpaRatingDao.findAllMpaRatings();
    }

    public Mpa findMpaRatingById(long id) {
        validationService.validateId(id);
        return mpaRatingDao.findMpaRatingById(id);
    }
}
