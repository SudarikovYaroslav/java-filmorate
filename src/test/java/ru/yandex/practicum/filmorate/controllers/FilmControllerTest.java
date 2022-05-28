package ru.yandex.practicum.filmorate.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.adapters.LocalDateAdapter;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FilmControllerTest {
    private static final long ID = 1L;
    private static final String NAME = "Test film name";
    private static final String DESCRIPTION = "Test description";
    private static final LocalDate RELEASE_DATE = LocalDate.of(2000, 1, 1);
    private static final Duration DURATION = Duration.ofHours(2);
    private static final URI URI = java.net.URI.create("http://localhost:8080/films");
    private static final int STATUS_OK = 200;

    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    public void nameValidationTest() throws IOException, InterruptedException {
        Film validNameFilm = generateValidFilm();
        Film nullNameFilm = generateValidFilm();
        nullNameFilm.setName(null);
        Film blankNameFilm = generateValidFilm();
        blankNameFilm.setName("");

        String validNameJson = gson.toJson(validNameFilm);
        String nullNameJson = gson.toJson(nullNameFilm);
        String blankNameJson = gson.toJson(blankNameFilm);

        System.out.println(validNameJson);

        HttpRequest.BodyPublisher validNameBody = HttpRequest.BodyPublishers.ofString(validNameJson);
        HttpRequest.BodyPublisher nullNameBody = HttpRequest.BodyPublishers.ofString(nullNameJson);
        HttpRequest.BodyPublisher blankNameBody = HttpRequest.BodyPublishers.ofString(blankNameJson);

        HttpRequest validNameRequest = HttpRequest.newBuilder().POST(validNameBody).uri(URI).build();
        HttpRequest nullNameRequest = HttpRequest.newBuilder().POST(nullNameBody).uri(URI).build();
        HttpRequest blankNameRequest = HttpRequest.newBuilder().POST(blankNameBody).uri(URI).build();

        HttpResponse<String> validResponse = client.send(validNameRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(STATUS_OK, validResponse.statusCode());

        InvalidFilmException exNullName = assertThrows(InvalidFilmException.class,
                () -> {
                    client.send(nullNameRequest, HttpResponse.BodyHandlers.ofString());
                }
        );
        assertEquals("Название фильма не может быть пустым!", exNullName.getMessage());

        InvalidFilmException exBlankName = assertThrows(InvalidFilmException.class,
                () -> {
                    client.send(blankNameRequest, HttpResponse.BodyHandlers.ofString());
                }
        );
        assertEquals("Название фильма не может быть пустым!", exBlankName.getMessage());
    }

    @Test
    public void filmDescriptionValidationTest() throws IOException, InterruptedException {
        Film validDescriptionFilm = generateValidFilm();
        Film tooLongDescriptionFilm = generateValidFilm();
        tooLongDescriptionFilm.setDescription(generateTooLongDescription());

        String validFilmJson = gson.toJson(validDescriptionFilm);
        String invalidFilmJson = gson.toJson(tooLongDescriptionFilm);

        HttpRequest.BodyPublisher validFilmBody = HttpRequest.BodyPublishers.ofString(validFilmJson);
        HttpRequest.BodyPublisher invalidFilmBody = HttpRequest.BodyPublishers.ofString(invalidFilmJson);

        HttpRequest validFilmRequest = HttpRequest.newBuilder().uri(URI).POST(validFilmBody).build();
        HttpRequest invalidFilmRequest = HttpRequest.newBuilder().uri(URI).POST(invalidFilmBody).build();

        HttpResponse<String> validFilmResponse = client.send(validFilmRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(STATUS_OK, validFilmResponse.statusCode());

        InvalidFilmException ex = assertThrows(InvalidFilmException.class, () -> {
                    client.send(invalidFilmRequest, HttpResponse.BodyHandlers.ofString());
                }
        );

        assertEquals("Максимальная длинна описания фильма: " + FilmController.getMaxDescriptionLength() +
                " символов!", ex.getMessage());
    }

    @Test
    public void releaseValidationTest() throws IOException, InterruptedException {
        Film validFilm = generateValidFilm();
        Film invalidReleaseFilm = generateValidFilm();
        invalidReleaseFilm.setReleaseDate(LocalDate.of(1895, Month.DECEMBER, 27));

        String validFilmJson = gson.toJson(validFilm);
        String invalidFilmJson = gson.toJson(invalidReleaseFilm);

        HttpRequest.BodyPublisher validFilmBody = HttpRequest.BodyPublishers.ofString(validFilmJson);
        HttpRequest.BodyPublisher invalidFilmBody = HttpRequest.BodyPublishers.ofString(invalidFilmJson);

        HttpRequest validFilmRequest = HttpRequest.newBuilder().uri(URI).POST(validFilmBody).build();
        HttpRequest invalidFilmRequest = HttpRequest.newBuilder().uri(URI).POST(invalidFilmBody).build();

        HttpResponse<String> validResponse = client.send(validFilmRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(STATUS_OK, validResponse.statusCode());

        InvalidFilmException ex = assertThrows(InvalidFilmException.class, () -> {
                client.send(invalidFilmRequest, HttpResponse.BodyHandlers.ofString());
            }
        );
        assertEquals("Дата релиза не может быть раньше чем день рождения кино: "
                + FilmController.getFirstFilmBirthday(), ex.getMessage());
    }

    @Test
    public void durationValidationTest() throws IOException, InterruptedException {
        Film validFilm = generateValidFilm();
        Film negativeDurationFilm = generateValidFilm();
        negativeDurationFilm.setDuration(Duration.ofHours(-1));

        String validFilmJson = gson.toJson(validFilm);
        String invalidFilmJson = gson.toJson(negativeDurationFilm);

        HttpRequest.BodyPublisher validFilmBody = HttpRequest.BodyPublishers.ofString(validFilmJson);
        HttpRequest.BodyPublisher invalidFilmBody =  HttpRequest.BodyPublishers.ofString(invalidFilmJson);

        HttpRequest validFilmRequest = HttpRequest.newBuilder().uri(URI).POST(validFilmBody).build();
        HttpRequest invalidFilmRequest = HttpRequest.newBuilder().uri(URI).POST(invalidFilmBody).build();

        HttpResponse<String> validFilmResponse = client.send(validFilmRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(STATUS_OK, validFilmResponse.statusCode());

        InvalidFilmException ex = assertThrows(InvalidFilmException.class, () -> {
                client.send(invalidFilmRequest, HttpResponse.BodyHandlers.ofString());
            }
        );
        assertEquals("Продолжительность фильма должна быть положительной!", ex.getMessage());
    }

    private String generateTooLongDescription() {
        StringBuilder resultBuilder = new StringBuilder();

        for (int i = 0; i < FilmController.getMaxDescriptionLength() + 1; i++) {
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
