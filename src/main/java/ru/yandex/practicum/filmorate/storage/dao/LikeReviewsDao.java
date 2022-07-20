package ru.yandex.practicum.filmorate.storage.dao;

import org.apache.el.stream.Optional;

public interface LikeReviewsDao {
    void addLike(long reviewId, long userId);

    void addDislike(long reviewId, long userId);

    void deleteLike(long reviewId, long userId);

    void deleteDislike(long reviewId, long userId);

    Integer likesNumber(long reviewId);
}
