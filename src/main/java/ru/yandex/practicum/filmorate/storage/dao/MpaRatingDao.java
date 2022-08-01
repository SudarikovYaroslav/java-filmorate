package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaRatingDao {
    List<Mpa> findAllMpaRatings();

    Mpa findMpaRatingById(long id);
}
