package ru.yandex.practicum.filmorate.storage.dao;

public interface LikesDao {
    void addLike(long film, long user);
    boolean deleteLike(long film, long user);
    int likesNumber(long film);
}
