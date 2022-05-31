package ru.yandex.practicum.filmorate.service;

public class FilmIdGenerator {
    private static long id = 1;

    private FilmIdGenerator(){}

    public static long generate() {
        return id++;
    }
}
