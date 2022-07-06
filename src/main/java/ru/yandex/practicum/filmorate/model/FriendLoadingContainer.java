package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

/**
* Вспомогательный класс-контейнер для упрощения загрузки друзей пользователя из БД
*/
@Data
@Builder
public class FriendLoadingContainer {
    private long friend_id;
    private long friendshipStatusId;
    private String friendshipStatus;
}
