package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.StorageException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.ReviewDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class DbReviewDaoImpl implements ReviewDao {

    public static final String REVIEW_COLUMN = "review_id";
    public static final String CONTENT_COLUMN = "content";
    public static final String POSITIVE_STATUS_COLUMN = "positive_status";
    public static final String USER_ID_COLUMN = "user_id";
    public static final String FILM_ID_COLUMN = "film_id";

    private final JdbcTemplate jdbcTemplate;

    public DbReviewDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Review makeReview(ResultSet rs, int rowNum) throws SQLException {
        return new Review(rs.getLong(REVIEW_COLUMN),
                rs.getString(CONTENT_COLUMN),
                rs.getBoolean(POSITIVE_STATUS_COLUMN),
                rs.getLong(USER_ID_COLUMN),
                rs.getLong(FILM_ID_COLUMN), 0);
    }

    @Override
    public Review save(Review review) {
        String sqlQuery = "insert into REVIEWS (content, positive_status, user_id, film_id) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"review_id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            return stmt;
        }, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return review;
    }

    @Override
    public Review update(Review review) {
        String sqlQuery = "update REVIEWS set " + "content = ?, positive_status = ? " + "where review_id = ?";
        jdbcTemplate.update(sqlQuery
                , review.getContent()
                , review.getIsPositive()
                , review.getReviewId());
        return findReviewById(review.getReviewId()).get();
    }

    @Override
    public Collection<Review> findAll() {
        String sql = "select * from REVIEWS";
        return jdbcTemplate.query(sql, this::makeReview);
    }

    @Override
    public Optional<Review> findReviewById(long id) {
        String sqlQuery = "select * from REVIEWS where review_id = ?";
        List<Review> reviews = jdbcTemplate.query(sqlQuery, this::makeReview, id);
        if (reviews.size() != 1) {
            throw new StorageException("Отзыва с таким id нет в базе данных");
        }
        return Optional.of(reviews.get(0));
    }

    @Override
    public Boolean delete(long id) {
        String sqlQuery = "delete from REVIEWS where review_id = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public Collection<Review> findReviewsByFilmId(long filmId) {
        String sqlQuery = "select * from REVIEWS " + "where FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::makeReview, filmId);
    }
}
