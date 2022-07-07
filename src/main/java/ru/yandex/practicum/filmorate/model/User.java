package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class User {
    @Email
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private long id;
}
