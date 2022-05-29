package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
public class FilmControllerTest {
    private static final long ID = 1L;
    private static final String NAME = "Test film name";
    private static final String DESCRIPTION = "Test description";
    private static final LocalDate RELEASE_DATE = LocalDate.of(2000, 1, 1);
    private static final Duration DURATION = Duration.ofHours(2);
    private static final URI URI = java.net.URI.create("http://localhost:8080/films");
    private static final int STATUS_OK = 200;

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
        assertEquals("Название фильма не может быть пустым!", exNullName.getMessage());

        Film blankNameFilm = generateValidFilm();
        blankNameFilm.setName("");
        InvalidFilmException exBlankName = assertThrows(InvalidFilmException.class, () -> {
                filmController.add(blankNameFilm);
            }
        );
        assertEquals("Название фильма не может быть пустым!", exBlankName.getMessage());
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
                .id(ID)
                .name(NAME)
                .description(DESCRIPTION)
                .releaseDate(RELEASE_DATE)
                .duration(DURATION)
                .build();
    }
}
