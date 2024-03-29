package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.IllegalIdException;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.DbFilmDaoImpl;
import ru.yandex.practicum.filmorate.storage.impl.DbUserDaoImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final DbUserDaoImpl userStorage;
    private final DbFilmDaoImpl filmStorage;

    @Test
    public void testSaveUser() {
        userStorage.save(generateUser());
        Optional<User> userOptional = userStorage.findUserById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("login", "dolore")
                );
    }

    @Test
    public void testUpdateUser() throws InvalidUserException {
        userStorage.update(generateUpdatedUser());
        Optional<User> userOptional = userStorage.findUserById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user)
                        .hasFieldOrPropertyWithValue("login", "doloreUpdate")
                );
    }

    @Test
    public void testFindAllUser() {
        userStorage.save(generateFriend());
        List<User> users = userStorage.findAll();
        assertEquals(2, users.size());
    }

    @Test
    public void findUserById() {
        userStorage.save(generateUser());
        Optional<User> user = userStorage.findUserById(1);
        user.ifPresent(value -> assertEquals(1, value.getId()));
    }

    @Test
    public void deleteUserById() {
        userStorage.save(generateUser());
        userStorage.deleteUserById(1L);
        final IllegalIdException exception = assertThrows(IllegalIdException.class, () -> {
            userStorage.findUserById(1);
        });
        assertEquals(
                "Пользователь 1 не найден",
                exception.getMessage()
        );
    }

    @Test
    public void testSaveFilm() {
        filmStorage.save(generateFilm());
        Optional<Film> filmOptional = filmStorage.findFilmById(1);
        filmOptional.ifPresent(value -> assertEquals("labore nulla", value.getName()));
    }

    @Test
    public void updateFilmTest() throws InvalidFilmException {
        filmStorage.update(generateUpdatedFilm());
        Optional<Film> filmOptional = filmStorage.findFilmById(1);
        filmOptional.ifPresent(value -> assertEquals("Film Updated", value.getName()));
    }

    @Test
    public void testFindAllFilms() {
        filmStorage.save(generateFriendFilm());
        List<Film> films = filmStorage.findAll();
        assertEquals(2, films.size());
    }

    @Test
    public void testFindFilmById() {
        filmStorage.save(generateFilm());
        Optional<Film> filmOptional = filmStorage.findFilmById(1);
        filmOptional.ifPresent(value -> assertEquals(1, value.getId()));
    }

    @Test
    public void deleteFilmById() {
        filmStorage.save(generateFilm());
        filmStorage.deleteFilmById(1L);
        final IllegalIdException exception = assertThrows(IllegalIdException.class, () -> {
            filmStorage.findFilmById(1);
        });
        assertEquals(
                "Фильм 1 не найден",
                exception.getMessage()
        );
    }

    private User generateUser() {
        return User.builder()
                .login("dolore")
                .name("Nick name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946,8,20))
                .build();
    }

    private User generateUpdatedUser() {
        return User.builder()
                .login("doloreUpdate")
                .name("est adipisicing")
                .id(1)
                .email("mail@yandex.ru")
                .birthday(LocalDate.of(1976,9,20))
                .build();
    }

    private User generateFriend() {
        return User.builder()
                .login("friend")
                .name("friend adipisicing")
                .email("friend@mail.ru")
                .birthday(LocalDate.of(1976,8,20))
                .build();
    }

    private Film generateFilm() {
        return Film.builder()
                .name("labore nulla")
                .releaseDate(LocalDate.of(1979,4,17))
                .description("Duis in consequat esse")
                .duration(100)
                .mpa(Mpa.builder().id(1L).name("G").build())
                .build();
    }

    private Film generateUpdatedFilm() {
        return Film.builder()
                .id(1)
                .name("Film Updated")
                .releaseDate(LocalDate.of(1989,4,17))
                .description("New film update decription")
                .duration(190)
                .mpa(Mpa.builder().id(5L).name("NC-17").build())
                .build();
    }

    private Film generateFriendFilm() {
        return Film.builder()
                .name("New film")
                .releaseDate(LocalDate.of(1999,4,30))
                .description("New film about friends")
                .duration(120)
                .mpa(Mpa.builder().id(3L).name("PG-13").build())
                .genres(List.of(Genre.builder().id(1).name("Комедия").build()))
                .build();
    }
}

