package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.generators.FilmIdGenerator;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmControllerTest {
    private static final String NAME = "Test film name";
    private static final String DESCRIPTION = "Test description";
    private static final LocalDate RELEASE_DATE = LocalDate.of(2000, 1, 1);
    private static final long DURATION = 120L;
    private static FilmController filmController;

    @BeforeEach
    public void preparation() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage()));
    }

    @Test
    public void addTest() throws InvalidFilmException {
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
    public void updateTest() throws InvalidFilmException, FilmNotFoundException {
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
        Film invalidIdFilm = generateValidFilm();
        invalidIdFilm.setId(0);
        filmController.add(invalidIdFilm);
        assertNotEquals(0, filmController.get().size());
    }

    @Test
    public void filmDescriptionValidationTest() throws InvalidFilmException {
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
        assertThrows(IllegalStateException.class, () -> {
                    filmController.add(null);
                }
        );
    }

    @Test
    public void addLikeTest() throws InvalidFilmException, FilmNotFoundException {
        Film film = generateValidFilm();
        filmController.add(film);
        long filmId = film.getId();
        long user1Id = 1;
        long user2Id = 2;
        filmController.addLike(filmId, user1Id);
        filmController.addLike(filmId, user2Id);

        assertEquals(2, film.likesNumber());
    }

    @Test
    public void deleteLikeTest() throws InvalidFilmException, FilmNotFoundException {
        Film film = generateValidFilm();
        filmController.add(film);
        long filmId = film.getId();
        long user1Id = 1;
        long user2Id = 2;
        filmController.addLike(filmId, user1Id);
        filmController.addLike(filmId, user2Id);

        filmController.deleteLike(filmId, user1Id);
        assertEquals(1, film.likesNumber());
    }

    @Test
    public void getTopFilmsDefaultCountTest() throws InvalidFilmException, FilmNotFoundException {
        List<Film> testFilmsWithLikes = generateFilmsListWithLikes();

        for (Film film : testFilmsWithLikes) {
            filmController.add(film);
        }

        assertEquals(15, filmController.get().size());
        assertEquals(FilmService.TOP_FILMS_DEFAULT_COUNT, filmController.getTopFilms(null).size());
    }

    @Test
    public void getTopFilmsWithSetCountTest() throws InvalidFilmException {
        int count1 = 1;
        int count2 = 3;
        int count3 = 7;

        List<Film> testFilmsWithLikes = generateFilmsListWithLikes();

        for (Film film : testFilmsWithLikes) {
            filmController.add(film);
        }

        assertEquals(15, filmController.get().size());
        assertEquals(count1, filmController.getTopFilms(count1).size());
        assertEquals(count2, filmController.getTopFilms(count2).size());
        assertEquals(count3, filmController.getTopFilms(count3).size());

    }

    @Test
    public void getTopFilmsSortingTest() throws InvalidFilmException {
        List<Film> testFilmsWithLikes = generateFilmsListWithLikes();

        for (Film film : testFilmsWithLikes) {
            filmController.add(film);
        }

        List<Film> topFilms = filmController.getTopFilms(null);
        Film mostLikeFilm = topFilms.get(0);
        Film listLikedFilm = topFilms.get(topFilms.size() - 1);
        assertTrue(mostLikeFilm.likesNumber() >= listLikedFilm.likesNumber());
    }

    private List<Integer> generateUsersIdList() {
        List<Integer> usersIds = new ArrayList<>();

        for (int i = 1; i <= 15; i++) {
            usersIds.add(i);
        }
        return usersIds;
    }

    private List<Film> generateFilmsListWithLikes() {
        // создаём 15 фильмов и добавляем каждому лайки
        List<Integer> usersId = generateUsersIdList();
        List<Film> filmsList = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            String filmName = "TestFilm: " + i;
            Film film = generateValidFilm();
            film.setName(filmName);

            for (int j = 0; j < i; j++) {
                film.addLike(usersId.get(j));
            }
            filmsList.add(film);
        }
        return filmsList;
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
                .name(NAME)
                .description(DESCRIPTION)
                .releaseDate(RELEASE_DATE)
                .duration(DURATION)
                .build();
    }
}
