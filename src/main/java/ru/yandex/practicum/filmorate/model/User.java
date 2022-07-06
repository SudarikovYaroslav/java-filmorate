package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.HashMap;
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
    private Map<Long, FriendshipStatus> friends = new HashMap<>();

    public void addFriend(long userId) {
        FriendshipStatus friendshipStatus = new FriendshipStatus();
        friendshipStatus.setStatus("неподтверждённая");
        friendshipStatus.setStatus_id(1);
        friends.put(id, friendshipStatus);
    }

    public void deleteFriend(long userId) {
        friends.remove(id);
    }

    public Set<Long> getFriends() {
        return friends.keySet();
    }
}
