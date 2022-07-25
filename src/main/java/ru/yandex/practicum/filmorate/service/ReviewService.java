package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.StorageException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.*;
import ru.yandex.practicum.filmorate.storage.impl.DbFeedDaoImpl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final UserDao userDao;
    private final FilmDao filmDao;
    private final FeedDao feedDao;
    private final ReviewDao reviewDao;
    private final LikeReviewsDao likeReviewsDao;

    public ReviewService(ReviewDao reviewDao, UserDao userDao, FilmDao filmDao,
                         LikeReviewsDao likeReviewsDao, DbFeedDaoImpl feedDao) {
        this.reviewDao = reviewDao;
        this.userDao = userDao;
        this.filmDao = filmDao;
        this.likeReviewsDao = likeReviewsDao;
        this.feedDao = feedDao;
    }

    public Collection<Review> findAllByFilmId(int filmId, int count) {
        List<Review> reviewsWithUseful = new ArrayList<>();
        List<Review> reviews = new ArrayList<>();
        if (filmId == 0) {
            reviews.addAll(reviewDao.findAll().stream()
                    .limit(count)
                    .collect(Collectors.toList()));
        } else {
            reviews.addAll(reviewDao.findReviewsByFilmId(filmId).stream()
                    .limit(count)
                    .collect(Collectors.toList()));
        }
        for (Review review : reviews) {
            review.setUseful(rateReviews(review.getReviewId()));
            reviewsWithUseful.add(review);
        }
        return reviewsWithUseful.stream()
                .sorted(this::compare)
                .collect(Collectors.toList());
    }

    private int compare(Review p0, Review p1) {
        return (p1.getUseful() - p0.getUseful());
    }

    public Review save(Review review) {
        if (review.getUserId() == null || review.getFilmId() == null) {
            throw new IllegalStateException("Не заполнены поля filmId или userId");
        } else if ((userDao.findUserById(review.getUserId()).isPresent())
                && (filmDao.findFilmById(review.getFilmId()).isPresent())) {
            Review newReview = reviewDao.save(review);
            newReview.setUseful(0);
            feedDao.saveFeed(new Feed(1, Instant.now().toEpochMilli(),
                    review.getUserId(), "REVIEW", "ADD", review.getReviewId()));
            return newReview;
        } else {
            throw new StorageException("Не удалось сохранить отзыв. Проверьте корректность" +
                    " заданных параметров FilmId = "
                    + review.getFilmId() + ", UserId = " + review.getUserId());
        }
    }

    public Review update(Review review) {
        if (reviewDao.findAll().contains(review)) {
            Review newReview = reviewDao.update(review);
            newReview.setUseful(rateReviews(review.getReviewId()));
            feedDao.saveFeed(new Feed(1, Instant.now().toEpochMilli(),
                    newReview.getUserId(), "REVIEW", "UPDATE", newReview.getReviewId()));
            return newReview;
        } else {
            throw new StorageException("Данного отзыва c Id = " + review.getReviewId() + " нет в БД");
        }
    }

    public Review findReviewById(long id) {
        if (reviewDao.findReviewById(id).isPresent()) {
            Review review = reviewDao.findReviewById(id).get();
            review.setUseful(rateReviews(id));
            return review;
        } else {
            throw new StorageException("Данного отзываc Id = " + id + " нет в БД");
        }
    }

    public boolean delete(long id) {
        Optional<Review> review = reviewDao.findReviewById(id);
        feedDao.saveFeed(new Feed(1, Instant.now().toEpochMilli(),
                review.get().getUserId(), "REVIEW", "REMOVE", review.get().getReviewId()));
        return reviewDao.delete(id);
    }

    public void addLike(long reviewId, long userId) {
        if (reviewDao.findReviewById(reviewId).isPresent() && userDao.findUserById(userId).isPresent()) {
            likeReviewsDao.addLike(reviewId, userId);
        } else {
            throw new StorageException("Не удалось поставить лайк отзыву. Проверьте корректность" +
                    " заданных параметров reviewId = "
                    + reviewId + ", UserId = " + userId);
        }
    }

    public void addDislike(long reviewId, long userId) {
        if (reviewDao.findReviewById(reviewId).isPresent() && userDao.findUserById(userId).isPresent()) {
            likeReviewsDao.addDislike(reviewId, userId);
        } else {
            throw new StorageException("Не удалось поставить дизлайк отзыву. Проверьте корректность" +
                    " заданных параметров reviewId = "
                    + reviewId + ", UserId = " + userId);
        }
    }

    public void deleteLike(long reviewId, long userId) {
        likeReviewsDao.deleteLike(reviewId, userId);
    }

    public void deleteDislike(long reviewId, long userId) {
        likeReviewsDao.deleteDislike(reviewId, userId);
    }

    public Integer rateReviews(long reviewId) {
        if (likeReviewsDao.likesNumber(reviewId) != null) {
            return likeReviewsDao.likesNumber(reviewId);
        }
        return 0;
    }
}