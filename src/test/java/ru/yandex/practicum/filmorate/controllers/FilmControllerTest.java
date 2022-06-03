package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmIdGenerator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmControllerTest {
    private static final String NAME = "Test film name";
    private static final String DESCRIPTION = "Test description";
    private static final LocalDate RELEASE_DATE = LocalDate.of(2000, 1, 1);
    private static final long DURATION = 120L;

    @Test
    public void addTest() throws InvalidFilmException {
        FilmController filmController = new FilmController();

        Film film1 = generateValidFilm();
        film1.setId(0);
        film1.setName("Film one");

        Film film2 = generateValidFilm();
        film2.setId(0);
        film2.setName("Film two");

        Film film3 = generateValidFilm();
        film3.setId(0);
        film3.setName("Film three");

        filmController.add(film1);
        filmController.add(film2);
        filmController.add(film3);

        assertEquals(3, filmController.get().size());
    }

    @Test
    public void updateTest() throws InvalidFilmException {
        FilmController filmController = new FilmController();
        Film goalFilm = generateValidFilm();
        filmController.add(goalFilm);

        Film modifiedGoalFilm = generateValidFilm();
        modifiedGoalFilm.setName("Modified name");
        long id = goalFilm.getId();
        modifiedGoalFilm.setId(id);

        filmController.update(modifiedGoalFilm);
        assertEquals(goalFilm.getId(), modifiedGoalFilm.getId());

        for (Film f : filmController.get()) {
            if (f.getId() == id) {
                assertEquals("Modified name", f.getName());
            }
        }
    }

    @Test
    public void nameValidationTest() throws InvalidFilmException {
        Film film = generateValidFilm();
        FilmController filmController = new FilmController();
        filmController.add(film);
        assertNotEquals(0, filmController.get().size());

        Film nullNameFilm = generateValidFilm();
        nullNameFilm.setName(null);
        assertThrows(InvalidFilmException.class, () -> {
                    filmController.add(nullNameFilm);
                }
        );

        Film blankNameFilm = generateValidFilm();
        blankNameFilm.setName("");
        assertThrows(InvalidFilmException.class, () -> {
                    filmController.add(blankNameFilm);
                }
        );
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

        assertThrows(InvalidFilmException.class, () -> {
                    filmController.add(tooLongDescriptionFilm);
                }
        );

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
        assertThrows(InvalidFilmException.class, () -> {
                    filmController.add(beforeEverFilm);
                }
        );
    }

    @Test
    public void durationValidationTest() throws InvalidFilmException {
        FilmController filmController = new FilmController();
        Film zeroDurationFilm = generateValidFilm();
        zeroDurationFilm.setDuration(0);

        filmController.add(zeroDurationFilm);
        assertNotEquals(0, filmController.get().size());

        Film negativeDurationFilm = generateValidFilm();
        negativeDurationFilm.setDuration(-1);
        assertThrows(InvalidFilmException.class, () -> {
                    filmController.add(negativeDurationFilm);
                }
        );
    }

    @Test
    public void addNullFilmTest() {
        Film film = null;
        FilmController filmController = new FilmController();

        assertThrows(IllegalStateException.class, () -> {
                    filmController.add(film);
                }
        );
    }

    /**
     * Метод генерирует описание к фильму длинной на 1 символ больше максимально допустимой
     */
    private String generateTooLongDescription() {
        StringBuilder resultBuilder = new StringBuilder();

        for (int i = 0; i < FilmController.MAX_FILM_DESCRIPTION_LENGTH + 1; i++) {
            resultBuilder.append("1");
        }
        return resultBuilder.toString();
    }


    private String generateMaxLengthDescription() {
        StringBuilder resultBuilder = new StringBuilder();

        for (int i = 0; i < FilmController.MAX_FILM_DESCRIPTION_LENGTH; i++) {
            resultBuilder.append("1");
        }
        return resultBuilder.toString();
    }

    private Film generateValidFilm() {
        return Film.builder()
                .id(FilmIdGenerator.generate())
                .name(NAME)
                .description(DESCRIPTION)
                .releaseDate(RELEASE_DATE)
                .duration(DURATION)
                .build();
    }
}
