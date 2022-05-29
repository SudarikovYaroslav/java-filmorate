package ru.yandex.practicum.filmorate.service;

public class IdGenerator {
    private static long id = 1;

    private IdGenerator(){}

    public static long generateId() {
        return id++;
    }
}
