merge into GENRES (GENRE_ID, genre_name) values (1, 'Комедия');
merge into GENRES (GENRE_ID, genre_name) values (2, 'Драма');
merge into GENRES (GENRE_ID, genre_name) values (3, 'Мультфильм');
merge into GENRES (GENRE_ID, genre_name) values (4, 'Триллер');
merge into GENRES (GENRE_ID, genre_name) values (5, 'Документальный');
merge into GENRES (GENRE_ID, genre_name) values (6, 'Боевик');

merge into MPA_RATINGS (MPA_RATING_ID, mpa_name) values (1,'G');
merge into MPA_RATINGS (MPA_RATING_ID, mpa_name) values (2, 'PG');
merge into MPA_RATINGS (MPA_RATING_ID, mpa_name) values (3, 'PG-13');
merge into MPA_RATINGS (MPA_RATING_ID, mpa_name) values (4, 'R');
merge into MPA_RATINGS (MPA_RATING_ID, mpa_name) values (5, 'NC-17');

merge into FRIENDSHIP_STATUSES (FRIENDSHIP_STATUS_ID, status_name) values (1, 'неподтверждённая');
merge into FRIENDSHIP_STATUSES (FRIENDSHIP_STATUS_ID, status_name) values (2, 'подтверждённая');