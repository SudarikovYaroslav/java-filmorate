package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class User {
    @Email
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private long id;
    private final Set<Long> friends = new HashSet<>();

    // Временная мапа для дружбы, чтобы не менять код в сервисе. При подключении БД поля
    // confirmationOfFriendship и friends будут удалены
    private final Map<Long, Friendship> confirmationOfFriendship = new HashMap<>();

    public void addFriend(long id) {
        friends.add(id);
        confirmationOfFriendship.put(id, new Friendship().setUnconfirmed(true));
    }

    public void deleteFriend(long id) {
        friends.remove(id);
    }

    public void confirmFriendshipWith(long userId) {
        if (confirmationOfFriendship.containsKey(userId)) {
            confirmationOfFriendship.get(userId).setConfirmed(true);
        }
    }
}
