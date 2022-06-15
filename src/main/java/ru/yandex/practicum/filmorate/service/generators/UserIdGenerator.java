package ru.yandex.practicum.filmorate.service.generators;

import org.springframework.stereotype.Component;

@Component
public class UserIdGenerator {
    private static long id = 1;

    public long generate() {
        return id++;
    }
}
