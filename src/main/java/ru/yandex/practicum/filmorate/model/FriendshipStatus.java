package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipStatus {
    private long id;
    private String status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendshipStatus friendshipStatus = (FriendshipStatus) o;
        return id == friendshipStatus.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
