package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.time.Month;

public class Constants {
    //Film
    public static final long MAX_FILM_DESCRIPTION_LENGTH = 200L;
    public static final LocalDate FIRST_FILM_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER, 28);
    public static final String NULL_FILM_LOG = "Передан null film";
    public static final String NULL_FILM_FIELDS_LOG = "Объект Film некорректно инициализирован, есть null поля!";
    public static final String BLANK_FILM_NAME_LOG = "Пустое имя фильма при инициализации";
    public static final String LONG_FILM_DESCRIPTION_LOG = "Описание длиннее " + MAX_FILM_DESCRIPTION_LENGTH;
    public static final String BAD_FILM_RELEASE_DATE_LOG = "Дата релиза раньше ДР кино:" + FIRST_FILM_BIRTHDAY;
    public static final String NEGATIVE_FILM_DURATION_LOG = "Отрицательная продолжительность фильма";
    public static final String NEGATIVE_FILM_ID_LOG = "У фильма отрицательный id";
    public static final String ASSIGNED_FILM_ID_LOG = "Фильму присвоен id: ";
    public static final String UPDATE_FILM_FAIL_LOG = "Попытка обновить несуществующий фильм";

    //User
    public static final String NULL_USER_LOG = "Передан null user";
    public static final String NULL_USER_FIELDS_LOG = "Некорректно инициализирован пользователь, есть null поля";
    public static final String BAD_USER_EMAIL_LOG = "Некорректный адрес email";
    public static final String BAD_USER_LOGIN_LOG = "Логин пустой или содержит пробелы";
    public static final String ASSIGNED_USER_NAME_LOG = "Пользователю присвоено имя: ";
    public static final String BAD_USER_BIRTHDAY_LOG = "День рождения указан в будущем";
    public static final String NEGATIVE_USER_ID_LOG = "У пользователя отрицательный id";
    public static final String ASSIGNED_USER_ID_LOG = "Пользователю присвоен id: ";

    private Constants(){}
}
