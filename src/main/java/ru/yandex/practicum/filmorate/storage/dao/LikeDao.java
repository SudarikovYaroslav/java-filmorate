package ru.yandex.practicum.filmorate.storage.dao;

import java.util.Optional;

public interface LikeDao {
    void addLike(long film, long user);
    void deleteLike(long film, long user);
    int likesNumber(long film);
}
