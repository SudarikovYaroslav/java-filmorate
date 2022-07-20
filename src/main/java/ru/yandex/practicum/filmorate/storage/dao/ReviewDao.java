package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewDao {
    Review save(Review review);

    Review update(Review review);

    Collection<Review> findAll();

    Optional<Review> findReviewById(long id);

    Boolean delete(long id);

    Collection<Review> findReviewsByFilmId (long filmId);
}
