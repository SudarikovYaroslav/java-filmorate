package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.dao.FeedDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class DbFeedDaoImpl implements FeedDao {

    private final JdbcTemplate jdbcTemplate;

    public DbFeedDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void saveFeed(Feed feed) {
        String sqlQuery = "insert into FEEDS(feed_time,user_id, event_type, operation, entity_id)" +
                " VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"EVENT_ID"});
            stmt.setLong(1, feed.getTimestamp());
            stmt.setLong(2, feed.getUserId());
            stmt.setString(3, feed.getEventType());
            stmt.setString(4, feed.getOperation());
            stmt.setLong(5, feed.getEntityId());
            return stmt;
        }, keyHolder);
    }

    @Override
    public List<Feed> getUserFeedList(Long id) {
        String sqlQuery = "select* from FEEDS where user_id = ?";
        return jdbcTemplate.query(sqlQuery, this::makeFeed, id);
    }

    private Feed makeFeed(ResultSet rs, int rowNum) throws SQLException {
        return new Feed(rs.getLong("event_id"),
                rs.getLong("feed_time"),
                rs.getLong("user_id"),
                rs.getString("event_type"),
                rs.getString("operation"),
                rs.getLong("entity_id"));
    }
}