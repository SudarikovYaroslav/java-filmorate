package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedDao {
    List<Feed> getUserFeedList(Long id);

    void saveFeed(Feed feed);
}
