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
    private MpaRating mpaRating;
    private Set<Long> likes = new HashSet<>();
    private List<Genre> genres = new ArrayList<>();

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
        genres.add(g);
    }

    public void deleteGenre(Genre g) {
        genres.remove(g);
    }
}
