package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.HashSet;
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
    private Set<Long> friends;

    public void addFriend(User user) {
        friends.add(user.getId());
    }

    public void deleteFriend(User user) {
        friends.remove(user.getId());
    }
}
