package ru.yandex.practicum.filmorate.service.generators;

public class FilmIdGenerator {
    private static long id = 1;

    public FilmIdGenerator(){}

    public long generate() {
        return id++;
    }
}
