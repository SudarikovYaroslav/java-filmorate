package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IllegalIdException;
import ru.yandex.practicum.filmorate.exceptions.InvalidDirectorException;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

@Slf4j
@Service
public class ValidationService {

    public static final long MAX_FILM_DESCRIPTION_LENGTH = 200L;
    public static final LocalDate FIRST_FILM_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER, 28);

    public void validate(Film film) {
        validateNotNull(film);
        checkFilmId(film.getId());
        if (film.getName() == null) {
            String message = "Объект Film некорректно инициализирован, есть null поля! id: " + film.getId();
            log.warn(message);
            throw new InvalidFilmException(message);
        }
        if (film.getName().isBlank()) {
            String message = "Пустое имя фильма при инициализации id: " + film.getId();
            log.warn(message);
            throw new InvalidFilmException(message);
        }
        if (film.getDescription() != null && film.getDescription().length() > MAX_FILM_DESCRIPTION_LENGTH) {
            String message = "Описание длиннее " + MAX_FILM_DESCRIPTION_LENGTH + " id: " + film.getId();
            log.warn(message);
            throw new InvalidFilmException(message);
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(FIRST_FILM_BIRTHDAY)) {
            String message = "Дата релиза раньше ДР кино:" + FIRST_FILM_BIRTHDAY + " id: " + film.getId();
            log.warn(message);
            throw new InvalidFilmException(message);
        }
        if (film.getDuration() < 0) {
            String message = "Отрицательная продолжительность фильма  id: " + film.getId();
            log.warn(message);
            throw new InvalidFilmException(message);
        }
        if (film.getMpa() == null) {
            throw new InvalidFilmException(String.format("у фильма id: %d не установлен mpa", film.getId()));
        }
    }

    protected void validate(User user) {
        validateNotNull(user);
        if(user.getId() < 0) {
            throw new IllegalIdException("id пользователя не может быть отрицательным");
        }
        if (user.getEmail() == null
                || user.getLogin() == null
                || user.getBirthday() == null
        ) {
            String message = "Некорректно инициализирован пользователь, есть null поля id: " + user.getId();
            log.warn(message);
            throw new NullPointerException(message);
        }
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            String message = "Некорректный адрес email id: " + user.getId();
            log.warn(message);
            throw new InvalidUserException(message);
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            String message = "Логин пустой или содержит пробелы id: " + user.getId();
            log.warn(message);
            throw new InvalidUserException(message);
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Пользователю присвоено имя: " + user.getName());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            String message = "День рождения указан в будущем id: " + user.getId();
            log.warn(message);
            throw new InvalidUserException(message);
        }
    }

    public void validateDirector(Director director) {
        if (director.getId() <= 0
                || director.getName() == null
                || director.getName().isBlank()
        ) {
            throw new InvalidDirectorException("Недопустимы значения поле director");
        }
    }

    public void validateNotNull(Film film) {
        if (film == null) {
            String message = "Передан null film";
            log.warn(message);
            throw new IllegalStateException(message);
        }
    }

    private void validateNotNull(User user) {
        if (user == null) {
            String message = "Передан null user";
            log.warn(message);
            throw new IllegalStateException(message);
        }
    }

    public void checkId(long id) {
        if (id <= 0) throw new IllegalIdException("id должен быть больше нуля");
    }

    public void checkNegativeIds(long... ids) {
        for (long id : ids) {
            if (id <= 0 ) throw new IllegalIdException("user id:" + id + " отрицательный");
        }
    }

    public void checkFilmId(long id) {
        if (id < 0) throw new IllegalIdException("film id:" + id + " отрицательный");
    }

    public void checkUserId(long id) {
        if (id < 0) throw new IllegalIdException("user id: " + id + " отрицательный");
    }

    public void checkDirectorId(long id) {
        if (id < 0) throw new IllegalIdException("director id: " + id + " отрицательный");
    }
}
