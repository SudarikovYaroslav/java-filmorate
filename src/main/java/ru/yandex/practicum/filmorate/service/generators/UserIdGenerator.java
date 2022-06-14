package ru.yandex.practicum.filmorate.service.generators;

public class UserIdGenerator {
    private static long id = 1;

    public UserIdGenerator(){}

    public long generate() {
        return id++;
    }
}
