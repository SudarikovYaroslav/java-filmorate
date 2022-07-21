package ru.yandex.practicum.filmorate.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Feed {
    private long timestamp;
    private long userId;
    private String eventType;
    private String operation;
    private long eventId;
    private long entityId;
}