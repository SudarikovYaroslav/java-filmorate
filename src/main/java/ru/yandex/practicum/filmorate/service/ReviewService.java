package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.StorageException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.LikeReviewsDao;
import ru.yandex.practicum.filmorate.storage.dao.ReviewDao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewDao reviewDao;
    private final UserService userService;
    private final FilmService filmService;
    private final LikeReviewsDao likeReviewsDao;

    public ReviewService(ReviewDao reviewDao, UserService userService, FilmService filmService,
                         LikeReviewsDao likeReviewsDao) {
        this.reviewDao = reviewDao;
        this.userService = userService;
        this.filmService = filmService;
        this.likeReviewsDao = likeReviewsDao;
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

    public Review save(Review review) throws StorageException {
        if (review.getUserId() == null || review.getFilmId() == null){
            throw new IllegalStateException("Не заполнены поля filmId или userId");
        }
        else if ((userService.getUserById(review.getUserId()) != null)
                && (filmService.getFilmById(review.getFilmId()) != null)) {
            Review newReview = reviewDao.save(review);
            newReview.setUseful(0);
            return newReview;
        } else {
            throw new StorageException("Не удалось сохранить отзыв. Проверьте корректность" +
                    " заданных параметров FilmId = "
                    + review.getFilmId() + ", UserId = " + review.getUserId());
        }
    }

    public Review update(Review review) throws StorageException {
        if (reviewDao.findAll().contains(review)) {
            Review newReview = reviewDao.update(review);
            newReview.setUseful(rateReviews(review.getReviewId()));
            return newReview;
        } else {
            throw new StorageException("Данного отзыва нет в БД");
        }
    }

    public Review findReviewById(long id) throws StorageException {
        if (reviewDao.findReviewById(id).isPresent()) {
            Review review = reviewDao.findReviewById(id).get();
            review.setUseful(rateReviews(id));
            return review;
        } else {
            throw new StorageException("Данного отзыва нет в БД");
        }
    }

    public boolean delete(long id) {
        return reviewDao.delete(id);
    }

    public void addLike(long reviewId, long userId) throws StorageException {
        if (reviewDao.findReviewById(reviewId).isPresent() && userService.getUserById(userId) != null) {
            likeReviewsDao.addLike(reviewId, userId);
        } else {
            throw new StorageException("Не удалось поставить лайк отзыву. Проверьте корректность" +
                    " заданных параметров reviewId = "
                    + reviewId + ", UserId = " + userId);
        }
    }

    public void addDislike(long reviewId, long userId) throws StorageException {
        if (reviewDao.findReviewById(reviewId).isPresent() && userService.getUserById(userId) != null) {
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
