package ru.yandex.practicum.filmorate.service.generators;

public class FilmIdGenerator {
    private static long id = 1;

    private FilmIdGenerator(){}

    public static long generate() {
        return id++;
    }
}
