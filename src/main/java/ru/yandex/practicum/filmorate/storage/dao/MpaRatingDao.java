package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaRatingDao {

    List<Mpa> findAllMpaRatings();

    Mpa findMpaRatingById(long id);
}
