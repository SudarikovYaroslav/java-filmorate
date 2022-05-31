package ru.yandex.practicum.filmorate.model;

import lombok.Builder;

import java.time.LocalDate;
import java.util.Objects;

// Не стал использовать @Data от lombok, потому, что наставник и лектор на вебинаре от ЯП сказали, что из-за неё
// бывают сложно вылавливаемые ошибки.
@Builder
public class Film {
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;

    public Film(long id, String name, String description, LocalDate releaseDate, long duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return Objects.equals(id, film.id) && Objects.equals(name, film.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
