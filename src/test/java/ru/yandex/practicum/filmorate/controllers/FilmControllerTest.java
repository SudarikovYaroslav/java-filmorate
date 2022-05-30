package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.IdGenerator;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmControllerTest {
    private static final String NAME = "Test film name";
    private static final String DESCRIPTION = "Test description";
    private static final LocalDate RELEASE_DATE = LocalDate.of(2000, 1, 1);
    private static final Duration DURATION = Duration.ofHours(2);

    @Test
    public void addTest() throws InvalidFilmException {
        FilmController filmController = new FilmController();
        Film zeroIdFilm = generateValidFilm();
        zeroIdFilm.setId(0);
        int count = 3;

        for (int i = 0; i < count; i++) {
            filmController.add(zeroIdFilm);
        }
        assertEquals(count, filmController.get().size());
    }

    @Test
    public void updateTest() throws InvalidFilmException {
        FilmController filmController = new FilmController();
        Film film = generateValidFilm();
        filmController.add(film);

        for (int i = 0; i < 3; i++) {
            filmController.update(film);
        }
        assertEquals(1, filmController.get().size());

        film.setId(0);
        InvalidFilmException ex = assertThrows(InvalidFilmException.class, () -> {
                filmController.update(film);
            }
        );
        assertEquals("Обновление невозможно. Фильма с id = " + film.getId() + " не найдено", ex.getMessage());
    }

    @Test
    public void nameValidationTest() throws InvalidFilmException {
        Film film = generateValidFilm();
        FilmController filmController = new FilmController();
        filmController.add(film);
        assertNotEquals(0, filmController.get().size());

        Film nullNameFilm = generateValidFilm();
        nullNameFilm.setName(null);
        InvalidFilmException exNullName = assertThrows(InvalidFilmException.class, () -> {
                    filmController.add(nullNameFilm);
                }
        );
        assertEquals("Фильму не установлено название!", exNullName.getMessage());

        Film blankNameFilm = generateValidFilm();
        blankNameFilm.setName("");
        InvalidFilmException exBlankName = assertThrows(InvalidFilmException.class, () -> {
                    filmController.add(blankNameFilm);
                }
        );
        assertEquals("Название фильма не может быть пустым!", exBlankName.getMessage());
    }

    @Test
    public void filmIdInvalidTest() throws InvalidFilmException {
        FilmController filmController = new FilmController();

        Film invalidIdFilm = generateValidFilm();
        invalidIdFilm.setId(0);
        filmController.add(invalidIdFilm);
        assertNotEquals(0, filmController.get().size());
    }

    @Test
    public void filmDescriptionValidationTest() throws InvalidFilmException {
        FilmController filmController = new FilmController();

        Film tooLongDescriptionFilm = generateValidFilm();
        tooLongDescriptionFilm.setDescription(generateTooLongDescription());

        InvalidFilmException ex = assertThrows(InvalidFilmException.class, () -> {
                    filmController.add(tooLongDescriptionFilm);
                }
        );
        assertEquals("Максимальная длинна описания фильма: " + FilmController.getMaxDescriptionLength()
                + " символов!", ex.getMessage());

        Film maxLengthDescriptionFilm = generateValidFilm();
        maxLengthDescriptionFilm.setDescription(generateMaxLengthDescription());
        filmController.add(maxLengthDescriptionFilm);
        assertNotEquals(0, filmController.get().size());
    }

    @Test
    public void releaseValidationTest() throws InvalidFilmException {
        FilmController filmController = new FilmController();
        Film firstFilmEver = generateValidFilm();

        firstFilmEver.setReleaseDate(FilmController.getFirstFilmBirthday());
        filmController.add(firstFilmEver);
        assertNotEquals(0, filmController.get().size());

        Film beforeEverFilm = generateValidFilm();
        beforeEverFilm.setReleaseDate(FilmController.getFirstFilmBirthday().minusDays(1));
        InvalidFilmException ex = assertThrows(InvalidFilmException.class, () -> {
                    filmController.add(beforeEverFilm);
                }
        );
        assertEquals("Дата релиза не может быть раньше чем день рождения кино: "
                + FilmController.getFirstFilmBirthday(), ex.getMessage());
    }

    @Test
    public void durationValidationTest() throws InvalidFilmException {
        FilmController filmController = new FilmController();
        Film zeroDurationFilm = generateValidFilm();
        zeroDurationFilm.setDuration(Duration.ofMillis(0));

        filmController.add(zeroDurationFilm);
        assertNotEquals(0, filmController.get().size());

        Film negativeDurationFilm = generateValidFilm();
        negativeDurationFilm.setDuration(Duration.ofMillis(-1));
        InvalidFilmException ex = assertThrows(InvalidFilmException.class, () -> {
                    filmController.add(negativeDurationFilm);
                }
        );
        assertEquals("Продолжительность фильма должна быть положительной!", ex.getMessage());
    }

    @Test
    public void addNullFilmTest() {
        Film film = null;
        FilmController filmController = new FilmController();

        InvalidFilmException ex = assertThrows(InvalidFilmException.class, () -> {
                    filmController.add(film);
                }
        );
        assertEquals("Передано пустое значение фильма!", ex.getMessage());
    }

    @Test
    public void addNullDescriptionFilmTest() {
        Film film = generateValidFilm();
        film.setDescription(null);
        FilmController filmController = new FilmController();

        InvalidFilmException ex = assertThrows(InvalidFilmException.class, () -> {
                    filmController.add(film);
                }
        );
        assertEquals("Не указано описание фильма!", ex.getMessage());
    }

    @Test
    public void addNullReleaseFilmTest() {
        Film film = generateValidFilm();
        film.setReleaseDate(null);
        FilmController filmController = new FilmController();

        InvalidFilmException ex = assertThrows(InvalidFilmException.class, () -> {
                    filmController.add(film);
                }
        );
        assertEquals("Не указана дата выхода фильма!", ex.getMessage());
    }

    @Test
    public void addNullDurationFilmTest() {
        Film film = generateValidFilm();
        film.setDuration(null);
        FilmController filmController = new FilmController();

        InvalidFilmException ex = assertThrows(InvalidFilmException.class, () -> {
                    filmController.add(film);
                }
        );
        assertEquals("Не указана продолжительность фильма!", ex.getMessage());
    }


    /**
     * Метод генерирует описание к фильму длинной на 1 символ больше максимально допустимой
     */
    private String generateTooLongDescription() {
        StringBuilder resultBuilder = new StringBuilder();

        for (int i = 0; i < FilmController.getMaxDescriptionLength() + 1; i++) {
            resultBuilder.append("1");
        }
        return resultBuilder.toString();
    }


    private String generateMaxLengthDescription() {
        StringBuilder resultBuilder = new StringBuilder();

        for (int i = 0; i < FilmController.getMaxDescriptionLength(); i++) {
            resultBuilder.append("1");
        }
        return resultBuilder.toString();
    }

    private Film generateValidFilm() {
        return Film.builder()
                .id(IdGenerator.generateId())
                .name(NAME)
                .description(DESCRIPTION)
                .releaseDate(RELEASE_DATE)
                .duration(DURATION)
                .build();
    }
}
