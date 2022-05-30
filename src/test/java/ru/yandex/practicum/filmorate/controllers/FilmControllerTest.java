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
        Film film = generateValidFilm();

        for (int i = 0; i < 3; i++) {
            filmController.add(film);
        }
        assertEquals(1, filmController.get().size());
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
        filmController.update(film);
        assertEquals(2, filmController.get().size());
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
        assertEquals(FilmController.NULL_FIELDS_LOG, exNullName.getMessage());

        Film blankNameFilm = generateValidFilm();
        blankNameFilm.setName("");
        InvalidFilmException exBlankName = assertThrows(InvalidFilmException.class, () -> {
                    filmController.add(blankNameFilm);
                }
        );
        assertEquals(FilmController.BLANK_NAME_LOG, exBlankName.getMessage());
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
        assertEquals(FilmController.TOO_LONG_DESCRIPTION_LOG, ex.getMessage());

        Film maxLengthDescriptionFilm = generateValidFilm();
        maxLengthDescriptionFilm.setDescription(generateMaxLengthDescription());
        filmController.add(maxLengthDescriptionFilm);
        assertNotEquals(0, filmController.get().size());
    }

    @Test
    public void releaseValidationTest() throws InvalidFilmException {
        FilmController filmController = new FilmController();
        Film firstFilmEver = generateValidFilm();

        firstFilmEver.setReleaseDate(FilmController.FIRST_FILM_BIRTHDAY);
        filmController.add(firstFilmEver);
        assertNotEquals(0, filmController.get().size());

        Film beforeEverFilm = generateValidFilm();
        beforeEverFilm.setReleaseDate(FilmController.FIRST_FILM_BIRTHDAY.minusDays(1));
        InvalidFilmException ex = assertThrows(InvalidFilmException.class, () -> {
                    filmController.add(beforeEverFilm);
                }
        );
        assertEquals(FilmController.BAD_RELEASE_DATE_LOG, ex.getMessage());
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
        assertEquals(FilmController.NEGATIVE_DURATION_LOG, ex.getMessage());
    }

    @Test
    public void addNullFilmTest() {
        Film film = null;
        FilmController filmController = new FilmController();

        NullPointerException ex = assertThrows(NullPointerException.class, () -> {
                    filmController.add(film);
                }
        );
        assertEquals(FilmController.NULL_FILM_LOG, ex.getMessage());
    }

    /**
     * Метод генерирует описание к фильму длинной на 1 символ больше максимально допустимой
     */
    private String generateTooLongDescription() {
        StringBuilder resultBuilder = new StringBuilder();

        for (int i = 0; i < FilmController.MAX_DESCRIPTION_LENGTH + 1; i++) {
            resultBuilder.append("1");
        }
        return resultBuilder.toString();
    }


    private String generateMaxLengthDescription() {
        StringBuilder resultBuilder = new StringBuilder();

        for (int i = 0; i < FilmController.MAX_DESCRIPTION_LENGTH; i++) {
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
