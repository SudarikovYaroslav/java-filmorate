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
    public void addNullUserTest() {
        UserController userController = new UserController();
        User nullUser = null;

        InvalidUserException ex = assertThrows(InvalidUserException.class, () -> {
                    userController.add(nullUser);
                }
        );
        assertEquals("Передано пустое значение пользователя!", ex.getMessage());
    }

    @Test
    public void invalidIdUserTest() throws InvalidUserException {
        UserController userController = new UserController();

        User invalidIdUser = generateValidUser();
        invalidIdUser.setId(null);
        userController.add(invalidIdUser);
        assertNotEquals(0, userController.get().size());
    }

    @Test
    public void emailValidationTest() throws InvalidUserException {
        UserController userController = new UserController();
        User validUser = generateValidUser();
        String exReferenceMessage = "Электронная почта не может быть пустой и должна содержать символ @";

        userController.add(validUser);
        assertNotEquals(0, userController.get().size());

        User nullEmailUser = generateValidUser();
        nullEmailUser.setEmail(null);
        InvalidUserException exNullEmail = assertThrows(InvalidUserException.class, () -> {
                    userController.add(nullEmailUser);
                }
        );
        assertEquals(exReferenceMessage, exNullEmail.getMessage());

        User blankEmailUser = generateValidUser();
        blankEmailUser.setEmail("");
        InvalidUserException exBlankEmail = assertThrows(InvalidUserException.class, () -> {
                    userController.add(nullEmailUser);
                }
        );
        assertEquals(exReferenceMessage, exBlankEmail.getMessage());

        // проверка почты без символа @
        User incorrectFormatEmailUser = generateValidUser();
        incorrectFormatEmailUser.setEmail("usermail.ru");
        InvalidUserException exIncorrectFormatEmail = assertThrows(InvalidUserException.class, () -> {
                    userController.add(incorrectFormatEmailUser);
                }
        );
        assertEquals(exReferenceMessage, exIncorrectFormatEmail.getMessage());
    }

    @Test
    public void loginValidationTest() {
        UserController userController = new UserController();
        String exReferenceMessage = "Логин не может быть пустым и содержать пробелы!";

        User nullLoginUser = generateValidUser();
        nullLoginUser.setLogin(null);
        InvalidUserException nullLoginUserEx = assertThrows(InvalidUserException.class, () -> {
                    userController.add(nullLoginUser);
                }
        );
        assertEquals(exReferenceMessage, nullLoginUserEx.getMessage());

        User blankLoginUser = generateValidUser();
        blankLoginUser.setLogin("");
        InvalidUserException blankLoginUserEx = assertThrows(InvalidUserException.class, () -> {
                    userController.add(blankLoginUser);
                }
        );
        assertEquals(exReferenceMessage, blankLoginUserEx.getMessage());

        User spaceContainsLoginUser = generateValidUser();
        spaceContainsLoginUser.setLogin("user invalid login");
        InvalidUserException spaceLoginUserEx = assertThrows(InvalidUserException.class, () -> {
                    userController.add(spaceContainsLoginUser);
                }
        );
        assertEquals(exReferenceMessage, spaceLoginUserEx.getMessage());
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
        assertEquals("Дата рождения не может быть в будущем!", birthdayInFutureEx.getMessage());

        User newerBirthUser = generateValidUser();
        newerBirthUser.setBirthday(null);
        InvalidUserException neverBirthdayEx = assertThrows(InvalidUserException.class, () -> {
                    userController.add(newerBirthUser);
                }
        );
        assertEquals("Не указана дата рождения пользователя!", neverBirthdayEx.getMessage());
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
