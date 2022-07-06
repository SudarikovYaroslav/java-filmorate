merge into GENRES (GENRE_ID, genre_name, description) values (1, 'COMEDY', 'Комедия');
merge into GENRES (GENRE_ID, genre_name, description) values (2, 'DRAMA', 'Драма');
merge into GENRES (GENRE_ID, genre_name, description) values (3, 'CARTOON', 'Мультфильм');
merge into GENRES (GENRE_ID, genre_name, description) values (4, 'THRILLER', 'Триллер');
merge into GENRES (GENRE_ID, genre_name, description) values (5, 'DOCUMENTARY', 'Документальный');
merge into GENRES (GENRE_ID, genre_name, description) values (6, 'ACTION', 'Боевик');

merge into MPA_RATINGS (MPA_RATING_ID, mpa_name, description) values (1,'G', 'у фильма нет возрастных ограничений');
merge into MPA_RATINGS (MPA_RATING_ID, mpa_name, description) values (2, 'PG', 'детям рекомендуется смотреть фильм с родителями');
merge into MPA_RATINGS (MPA_RATING_ID, mpa_name, description) values (3, 'PG_13', 'детям до 13 лет просмотр не желателен');
merge into MPA_RATINGS (MPA_RATING_ID, mpa_name, description)
    values (4, 'R', 'лицам до 17 лет просматривать фильм можно только в присутствии взрослого');
merge into MPA_RATINGS (MPA_RATING_ID, mpa_name, description) values (5, 'NC_17', 'лицам до 18 лет просмотр запрещён');

merge into FRIENDSHIP_STATUSES (FRIENDSHIP_STATUS_ID, status_name) values (1, 'неподтверждённая');
merge into FRIENDSHIP_STATUSES (FRIENDSHIP_STATUS_ID, status_name) values (2, 'подтверждённая');
