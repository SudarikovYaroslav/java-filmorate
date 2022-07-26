package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.dao.LikeReviewsDao;

@Repository
public class LikeReviewDaoImpl implements LikeReviewsDao {

    public static final int LIKE = 1;
    public static final int DISLIKE = -1;

    private final JdbcTemplate jdbcTemplate;

    public LikeReviewDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(long reviewId, long userId) {
        String sqlQuery = "insert into LIKES_REVIEWS (review_id, user_id, like_dislike) " +
                "values (?, ?, ?)"
                ;
        jdbcTemplate.update(sqlQuery,
                reviewId,
                userId,
                (LIKE)
        );
    }
    public void addDislike(long reviewId, long userId) {
        String sqlQuery = "insert into LIKES_REVIEWS (review_id, user_id, like_dislike) " +
                "values (?, ?, ?)"
                ;
        jdbcTemplate.update(sqlQuery,
                reviewId,
                userId,
                (DISLIKE)
        );
    }

    @Override
    public void deleteLike(long reviewId, long userId) {
        String sqlQuery = "delete FROM LIKES_REVIEWS where review_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    public void deleteDislike(long reviewId, long userId) {
        String sqlQuery = "delete FROM LIKES_REVIEWS where review_id = ? and user_id = ? and like_dislike = ?";
        jdbcTemplate.update(sqlQuery, reviewId, userId, DISLIKE);
    }

    @Override
    public Integer getLikesCount(long reviewId) {
        String sqlQuery = "select SUM(like_dislike) from LIKES_REVIEWS where review_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, Integer.class, reviewId);
    }

}
