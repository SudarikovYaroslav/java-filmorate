package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.generators.UserIdGenerator;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    private static final String EMAIL = "user@mail.ru";
    private static final String LOGIN = "user";
    private static final String NAME = "Username";
    private static final LocalDate BIRTHDAY = LocalDate.of(2000, 1, 1);
    private static UserController userController;

    @BeforeEach
    public void preparation() {
        userController = new UserController(new UserService(new InMemoryUserStorage(new UserIdGenerator())));
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

    @Test
    public void addFriendTest() throws InvalidUserException {
        User user1 = generateValidUser();
        user1.setName("user1");
        user1.setLogin("user1");

        User user2 = generateValidUser();
        user2.setName("user2");
        user2.setLogin("user2");

        userController.add(user1);
        userController.add(user2);
        long user1Id = user1.getId();
        long user2Id = user2.getId();
        userController.addFriend(user1Id, user2Id);

        assertTrue(user1.getFriends().contains(user2Id));
        assertTrue(user2.getFriends().contains(user1Id));
    }

    @Test
    public void deleteFriedTest() throws InvalidUserException {
        User user1 = generateValidUser();
        user1.setName("user1");
        user1.setLogin("user1");

        User user2 = generateValidUser();
        user2.setName("user2");
        user2.setLogin("user2");

        userController.add(user1);
        userController.add(user2);
        long user1Id = user1.getId();
        long user2Id = user2.getId();
        userController.addFriend(user1Id, user2Id);

        userController.deleteFriend(user1Id, user2Id);
        assertEquals(0, user1.getFriends().size());
        assertEquals(0, user2.getFriends().size());
    }

    @Test
    public void getUserFriendsTest() throws InvalidUserException {
        User user1 = generateValidUser();
        user1.setName("user1");
        user1.setLogin("user1");

        User user2 = generateValidUser();
        user2.setName("user2");
        user2.setLogin("user2");

        User user3 = generateValidUser();
        user3.setName("user3");
        user3.setLogin("user3");

        userController.add(user1);
        userController.add(user2);
        userController.add(user3);
        long user1Id = user1.getId();
        long user2Id = user2.getId();
        long user3Id = user3.getId();

        userController.addFriend(user1Id, user2Id);
        userController.addFriend(user1Id, user3Id);

        assertTrue(userController.getUserFriends(user1Id).contains(user2));
        assertTrue(userController.getUserFriends(user1Id).contains(user3));
    }

    @Test
    public void getCommonFriendsTest() throws InvalidUserException {
        User user1 = generateValidUser();
        user1.setName("user1");
        user1.setLogin("user1");

        User user2 = generateValidUser();
        user2.setName("user2");
        user2.setLogin("user2");

        User commonFriend = generateValidUser();
        commonFriend.setName("commonFriend");
        commonFriend.setLogin("commonFriend");

        userController.add(user1);
        userController.add(user2);
        userController.add(commonFriend);
        long user1Id = user1.getId();
        long user2Id = user2.getId();
        long commonFriendId = commonFriend.getId();

        userController.addFriend(user1Id, commonFriendId);
        userController.addFriend(user2Id,commonFriendId);

        assertTrue(userController.getCommonFriends(user1Id, user2Id).contains(commonFriend));
    }

    private User generateValidUser() {
        return User.builder()
                .email(EMAIL)
                .login(LOGIN)
                .name(NAME)
                .birthday(BIRTHDAY)
                .build();
    }
}
