package ru.yandex.practicum.filmorate.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MpaRating {

   private final Map<String, String> ratingsDescriptions;

    public MpaRating() {
        ratingsDescriptions = new HashMap<>();
        addNewRating("G", "у фильма нет возрастных ограничений");
        addNewRating("PG", "детям рекомендуется смотреть фильм с родителями");
        addNewRating("PG-13", "детям до 13 лет просмотр не желателен");
        addNewRating("R", "лицам до 17 лет просматривать фильм можно только в присутствии взрослого");
        addNewRating("NC-17", "лицам до 18 лет просмотр запрещён");
    }

    public Set<String> getRatings() {
        return  ratingsDescriptions.keySet();
    }

    public String getDescription(String rating) {
        return  ratingsDescriptions.get(rating);
    }

    public void addNewRating(String rating, String description) {
        ratingsDescriptions.put(rating, description);
    }
}
