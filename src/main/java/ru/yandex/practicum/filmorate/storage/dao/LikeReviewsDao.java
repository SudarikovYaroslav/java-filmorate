package ru.yandex.practicum.filmorate.storage.dao;

public interface LikeReviewsDao {
    void addLike(long reviewId, long userId);

    void addDislike(long reviewId, long userId);

    void deleteLike(long reviewId, long userId);

    void deleteDislike(long reviewId, long userId);

    Integer getLikesCount(long reviewId);
}
