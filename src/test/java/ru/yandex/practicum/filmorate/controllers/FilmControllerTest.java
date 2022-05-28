package ru.yandex.practicum.filmorate.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Assertions;
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

        // этот кусочек просто для проверки работы GET запроса
        HttpRequest getRequest = HttpRequest.newBuilder().GET().uri(URI).build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(getResponse.statusCode(), STATUS_OK);
        //
        /*
        HttpResponse<String> validResponse = client.send(validNameRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(STATUS_OK, validResponse.statusCode());

        assertThrows(InvalidFilmException.class,
                () -> {
                    client.send(nullNameRequest, HttpResponse.BodyHandlers.ofString());
                }
        );

        assertThrows(InvalidFilmException.class,
                () -> {
                    client.send(blankNameRequest, HttpResponse.BodyHandlers.ofString());
                }
        );*/
    }

    @Test
    public void filmDescriptionValidationTest() {

    }

    @Test
    public void releaseValidationTest() {

    }

    @Test
    public void durationValidationTest() {

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
