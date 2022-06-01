package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDate;

@Data
@Builder
public class User {
    @Email
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private long id;
}
