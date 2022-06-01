package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    private static final long ID = 1L;
    private static final String EMAIL = "user@mail.ru";
    private static final String LOGIN = "user";
    private static final String NAME = "Username";
    private static final LocalDate BIRTHDAY = LocalDate.of(2000, 1, 1);

    @Test
    public void createTest() throws InvalidUserException {
        UserController userController = new UserController();
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
        UserController userController = new UserController();
        User user = generateValidUser();
        int count = 3;

        for (int i = 0; i < count; i++) {
            userController.add(user);
        }
        assertEquals(count, userController.get().size());
    }

    @Test
    public void updateTest() throws InvalidUserException {
        UserController userController = new UserController();
        User zeroIdUser = generateValidUser();
        zeroIdUser.setId(0);

        for (int i = 0; i < 3; i++) {
            userController.update(zeroIdUser);
        }
        assertEquals(0, userController.get().size());
    }

    @Test
    public void addNullUserTest() {
        UserController userController = new UserController();
        User nullUser = null;

        assertThrows(IllegalStateException.class, () -> {
                    userController.add(nullUser);
                }
        );
    }

    @Test
    public void invalidIdUserTest() throws InvalidUserException {
        UserController userController = new UserController();

        User invalidIdUser = generateValidUser();
        invalidIdUser.setId(0);
        userController.add(invalidIdUser);
        assertNotEquals(0, userController.get().size());
    }

    @Test
    public void emailValidationTest() throws InvalidUserException {
        UserController userController = new UserController();
        User validUser = generateValidUser();

        userController.add(validUser);
        assertNotEquals(0, userController.get().size());
    }

    @Test
    public void incorrectEmailValidationTest() {
        // проверка почты без символа @
        UserController userController = new UserController();
        User incorrectFormatEmailUser = generateValidUser();
        incorrectFormatEmailUser.setEmail("usermail.ru");
        assertThrows(InvalidUserException.class, () -> {
                    userController.add(incorrectFormatEmailUser);
                }
        );
    }

    @Test
    public void blankEmailValidationTest() {
        UserController userController = new UserController();
        User blankEmailUser = generateValidUser();
        blankEmailUser.setEmail("");
        assertThrows(InvalidUserException.class, () -> {
                    userController.add(blankEmailUser);
                }
        );
    }

    @Test
    public void nullEmailValidationTest() {
        UserController userController = new UserController();
        User nullEmailUser = generateValidUser();
        nullEmailUser.setEmail(null);
        assertThrows(NullPointerException.class, () -> {
                    userController.add(nullEmailUser);
                }
        );
    }

    @Test
    public void loginNullValidationTest() {
        UserController userController = new UserController();
        User nullLoginUser = generateValidUser();
        nullLoginUser.setLogin(null);
        assertThrows(NullPointerException.class, () -> {
                    userController.add(nullLoginUser);
                }
        );
    }

    @Test
    public void loginWithSpaseValidationTest() {
        UserController userController = new UserController();
        User spaceContainsLoginUser = generateValidUser();
        spaceContainsLoginUser.setLogin("user invalid login");
        assertThrows(InvalidUserException.class, () -> {
                    userController.add(spaceContainsLoginUser);
                }
        );
    }

    @Test
    public void loginBlankValidationTest() {
        UserController userController = new UserController();
        User blankLoginUser = generateValidUser();
        blankLoginUser.setLogin("");
        assertThrows(InvalidUserException.class, () -> {
                    userController.add(blankLoginUser);
                }
        );
    }

    @Test
    public void nameNullValidationTest() throws InvalidUserException {
        UserController userController = new UserController();
        User userNullName = generateValidUser();
        userNullName.setName(null);
        userController.add(userNullName);
        assertEquals(LOGIN, userNullName.getName());
    }

    @Test
    public void nameBlankValidationTest() throws InvalidUserException {
        UserController userController = new UserController();
        User userBlankName = generateValidUser();
        userBlankName.setName("");
        userController.add(userBlankName);
        assertEquals(LOGIN, userBlankName.getName());
    }

    @Test
    public void birthdayInFutureValidationTest() {
        UserController userController = new UserController();
        User userFromTheFuture = generateValidUser();
        userFromTheFuture.setBirthday(LocalDate.now().plusYears(1));

        assertThrows(InvalidUserException.class, () -> {
                    userController.add(userFromTheFuture);
                }
        );
    }

    @Test
    public void birthdayNullValidationTest() {
        UserController userController = new UserController();
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
