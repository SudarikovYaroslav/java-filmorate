package ru.yandex.practicum.filmorate.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Genre {
    private final Map<String, String> genres;

    public Genre() {
        genres = new HashMap<>();
        addGenre("COMEDY", "Комедия");
        addGenre("DRAMA", "Драма");
        addGenre("CARTOON", "Мультфильм");
        addGenre("THRILLER", "Триллер");
        addGenre("DOCUMENTARY", "Документальный");
        addGenre("ACTION", "Боевик");
    }

    public Set<String> getGenreNames() {
        return genres.keySet();
    }

    public String getGenreDescription(String genre) {
        return genres.get(genre);
    }

    public void addGenre(String genre, String description) {
        genres.put(genre, description);
    }
}
