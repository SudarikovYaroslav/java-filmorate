package ru.yandex.practicum.filmorate.storage.dao;

public interface MarkDao {
    void addMark(long film, long user, int mark);

    void deleteMark(long film, long user);

    Double findAvgMark(long film);
}
