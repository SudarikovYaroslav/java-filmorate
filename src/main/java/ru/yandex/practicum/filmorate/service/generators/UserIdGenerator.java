package ru.yandex.practicum.filmorate.service.generators;

public class UserIdGenerator {
    private static long id = 1;

    private UserIdGenerator(){}

    public static long generate() {
        return id++;
    }
}
