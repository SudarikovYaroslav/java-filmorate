package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class Film {
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    private final Set<Long> likes;

    public void addLike(User user) {
        likes.add(user.getId());
    }

    public void deleteLike(User user) {
        likes.remove(user.getId());
    }

    public int likes() {
        return likes.size();
    }
}
