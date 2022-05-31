package ru.yandex.practicum.filmorate.service;

public class UserIdGenerator {
    private static long id = 1;

    private UserIdGenerator(){}

    public static long generate() {
        return id++;
    }
}
