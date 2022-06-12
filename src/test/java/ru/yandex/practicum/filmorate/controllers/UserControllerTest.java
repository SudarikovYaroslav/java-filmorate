package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    private static final long ID = 1L;
    private static final String EMAIL = "user@mail.ru";
    private static final String LOGIN = "user";
    private static final String NAME = "Username";
    private static final LocalDate BIRTHDAY = LocalDate.of(2000, 1, 1);
    private static UserController userController;

    @BeforeEach
    public void preparation() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    public void createTest() throws InvalidUserException {
        User user = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946,8,2))
                .build()
        ;

        userController.add(user);
        assertEquals(1, userController.get().size());
        userController.add(user);
        assertEquals(2, userController.get().size());
    }

    @Test
    public void addTest() throws InvalidUserException {
        User user = generateValidUser();
        int count = 3;

        for (int i = 0; i < count; i++) {
            userController.add(user);
        }
        assertEquals(count, userController.get().size());
    }

    @Test
    public void updateTest() {
        User zeroIdUser = generateValidUser();
        zeroIdUser.setId(0);

        assertThrows(UserNotFoundException.class, () -> {
            userController.update(zeroIdUser);
        });
    }

    @Test
    public void addNullUserTest() {
        User nullUser = null;
        assertThrows(IllegalStateException.class, () -> {
                    userController.add(nullUser);
                }
        );
    }

    @Test
    public void invalidIdUserTest() throws InvalidUserException {
        User invalidIdUser = generateValidUser();
        invalidIdUser.setId(0);
        userController.add(invalidIdUser);
        assertNotEquals(0, userController.get().size());
    }

    @Test
    public void emailValidationTest() throws InvalidUserException {
        User validUser = generateValidUser();

        userController.add(validUser);
        assertNotEquals(0, userController.get().size());
    }

    @Test
    public void incorrectEmailValidationTest() {
        // проверка почты без символа @
        User incorrectFormatEmailUser = generateValidUser();
        incorrectFormatEmailUser.setEmail("usermail.ru");
        assertThrows(InvalidUserException.class, () -> {
                    userController.add(incorrectFormatEmailUser);
                }
        );
    }

    @Test
    public void blankEmailValidationTest() {
        User blankEmailUser = generateValidUser();
        blankEmailUser.setEmail("");
        assertThrows(InvalidUserException.class, () -> {
                    userController.add(blankEmailUser);
                }
        );
    }

    @Test
    public void nullEmailValidationTest() {
        User nullEmailUser = generateValidUser();
        nullEmailUser.setEmail(null);
        assertThrows(NullPointerException.class, () -> {
                    userController.add(nullEmailUser);
                }
        );
    }

    @Test
    public void loginNullValidationTest() {
        User nullLoginUser = generateValidUser();
        nullLoginUser.setLogin(null);
        assertThrows(NullPointerException.class, () -> {
                    userController.add(nullLoginUser);
                }
        );
    }

    @Test
    public void loginWithSpaseValidationTest() {
        User spaceContainsLoginUser = generateValidUser();
        spaceContainsLoginUser.setLogin("user invalid login");
        assertThrows(InvalidUserException.class, () -> {
                    userController.add(spaceContainsLoginUser);
                }
        );
    }

    @Test
    public void loginBlankValidationTest() {
        User blankLoginUser = generateValidUser();
        blankLoginUser.setLogin("");
        assertThrows(InvalidUserException.class, () -> {
                    userController.add(blankLoginUser);
                }
        );
    }

    @Test
    public void nameNullValidationTest() throws InvalidUserException {
        User userNullName = generateValidUser();
        userNullName.setName(null);
        userController.add(userNullName);
        assertEquals(LOGIN, userNullName.getName());
    }

    @Test
    public void nameBlankValidationTest() throws InvalidUserException {
        User userBlankName = generateValidUser();
        userBlankName.setName("");
        userController.add(userBlankName);
        assertEquals(LOGIN, userBlankName.getName());
    }

    @Test
    public void birthdayInFutureValidationTest() {
        User userFromTheFuture = generateValidUser();
        userFromTheFuture.setBirthday(LocalDate.now().plusYears(1));

        assertThrows(InvalidUserException.class, () -> {
                    userController.add(userFromTheFuture);
                }
        );
    }

    @Test
    public void birthdayNullValidationTest() {
        User newerBirthUser = generateValidUser();
        newerBirthUser.setBirthday(null);
        assertThrows(NullPointerException.class, () -> {
                    userController.add(newerBirthUser);
                }
        );
    }

    private User generateValidUser() {
        return User.builder()
                .id(ID)
                .email(EMAIL)
                .login(LOGIN)
                .name(NAME)
                .birthday(BIRTHDAY)
                .build();
    }
}
