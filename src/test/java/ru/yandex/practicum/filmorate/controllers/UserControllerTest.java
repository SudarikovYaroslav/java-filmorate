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
        assertEquals(1, userController.get().size());
    }

    @Test
    public void addNullUserTest() {
        UserController userController = new UserController();
        User nullUser = null;

        NullPointerException ex = assertThrows(NullPointerException.class, () -> {
                    userController.add(nullUser);
                }
        );
        assertEquals(UserController.NULL_USER_LOG, ex.getMessage());
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

        User nullEmailUser = generateValidUser();
        nullEmailUser.setEmail(null);
        NullPointerException exNullEmail = assertThrows(NullPointerException.class, () -> {
                    userController.add(nullEmailUser);
                }
        );
        assertEquals(UserController.NULL_FIELDS_LOG, exNullEmail.getMessage());

        User blankEmailUser = generateValidUser();
        blankEmailUser.setEmail("");
        InvalidUserException exBlankEmail = assertThrows(InvalidUserException.class, () -> {
                    userController.add(blankEmailUser);
                }
        );
        assertEquals(UserController.BAD_EMAIL_LOG, exBlankEmail.getMessage());

        // проверка почты без символа @
        User incorrectFormatEmailUser = generateValidUser();
        incorrectFormatEmailUser.setEmail("usermail.ru");
        InvalidUserException exIncorrectFormatEmail = assertThrows(InvalidUserException.class, () -> {
                    userController.add(incorrectFormatEmailUser);
                }
        );
        assertEquals(UserController.BAD_EMAIL_LOG, exIncorrectFormatEmail.getMessage());
    }

    @Test
    public void loginValidationTest() {
        UserController userController = new UserController();

        User nullLoginUser = generateValidUser();
        nullLoginUser.setLogin(null);
        NullPointerException nullLoginUserEx = assertThrows(NullPointerException.class, () -> {
                    userController.add(nullLoginUser);
                }
        );
        assertEquals(UserController.NULL_FIELDS_LOG, nullLoginUserEx.getMessage());

        User blankLoginUser = generateValidUser();
        blankLoginUser.setLogin("");
        InvalidUserException blankLoginUserEx = assertThrows(InvalidUserException.class, () -> {
                    userController.add(blankLoginUser);
                }
        );
        assertEquals(UserController.BAD_LOGIN_LOG, blankLoginUserEx.getMessage());

        User spaceContainsLoginUser = generateValidUser();
        spaceContainsLoginUser.setLogin("user invalid login");
        InvalidUserException spaceLoginUserEx = assertThrows(InvalidUserException.class, () -> {
                    userController.add(spaceContainsLoginUser);
                }
        );
        assertEquals(UserController.BAD_LOGIN_LOG, spaceLoginUserEx.getMessage());
    }

    @Test
    public void nameValidationTest() throws InvalidUserException {
        UserController userController = new UserController();

        User userNullName = generateValidUser();
        userNullName.setName(null);
        userController.add(userNullName);
        assertEquals(LOGIN, userNullName.getName());

        User userBlankName = generateValidUser();
        userBlankName.setName("");
        userController.add(userBlankName);
        assertEquals(LOGIN, userBlankName.getName());
    }

    @Test
    public void birthdayValidationTest() {
        UserController userController = new UserController();

        User userFromTheFuture = generateValidUser();
        userFromTheFuture.setBirthday(LocalDate.now().plusYears(1));

        InvalidUserException birthdayInFutureEx = assertThrows(InvalidUserException.class, () -> {
                    userController.add(userFromTheFuture);
                }
        );
        assertEquals(UserController.BAD_BIRTHDAY_LOG, birthdayInFutureEx.getMessage());

        User newerBirthUser = generateValidUser();
        newerBirthUser.setBirthday(null);
        NullPointerException neverBirthdayEx = assertThrows(NullPointerException.class, () -> {
                    userController.add(newerBirthUser);
                }
        );
        assertEquals(UserController.NULL_FIELDS_LOG, neverBirthdayEx.getMessage());
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
