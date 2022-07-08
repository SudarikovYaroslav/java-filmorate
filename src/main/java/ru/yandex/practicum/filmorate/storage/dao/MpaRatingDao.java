package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

public interface MpaRatingDao {

    List<MpaRating> findAllMpaRatings();

    MpaRating findMpaRatingById(long id);
}
