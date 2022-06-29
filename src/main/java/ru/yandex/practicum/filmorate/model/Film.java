package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class Film {
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    private final Set<Long> likes = new HashSet<>();
    private List<Genre> genre = new ArrayList<>();
    private final MpaRating mpaRating;

    public void addLike(long userId) {
        likes.add(userId);
    }

    public void deleteLike(long userId) {
        likes.remove(userId);
    }

    public int likesNumber() {
        return likes.size();
    }

    public void addGenre(Genre g) {
        genre.add(g);
    }

    public void deleteGenre(Genre g) {
        genre.remove(g);
    }
}
