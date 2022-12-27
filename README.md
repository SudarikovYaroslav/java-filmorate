# <img src= https://mir-cdn.behance.net/v1/rendition/projects/230/3da78b54757441.Y3JvcCw4NzIsNjgyLDAsMA.jpg> FILMORATE

---

***Сервис по поиску топ фильмов для просмотра.***

---
*Веб сервис написана на Java 11, на основе Spring Boot. Реализует REST API. В работе использованы Lombok, 
JUnit тестирование.*

**Клиент-серверное приложение позволяет работать с фильмами, давать им оценки и получать топ фильмов по году,
жанру, осуществлять поиск по режиссёру.**

### ER-диаграмма:
![](ER-diagram.png)

#### Примеры SQL запросов:
- Получить список названий всех фильмов  
  ```` SQL
  select film_name from FILMS;
  
- Получить список всех пользователей  
  ```` SQL
  select * from USERS;

- Получить топ 10 популярных фильмов  
  ```` SQL
  select film_name  
  from FILMS as f  
  left join LIKES as l on f.film_id = l.film_id  
  group by f.film_name  
  order by count(l.user_id) desc  
  limit (10);

- Получить список id общих друзей пользователей user1 и user2  
  с id = 1 и id = 2 (id подтверждённой дружбы - 2)
  ```` SQL
  slect friend_id
  from USER_FRIENDS
  where user_id = 1,
        friendship_status_id = 2
      intersect
  select friend_id
  from USER_FRIENDS
  where user_id = 2 
        friendship_status_id = 2;  
  
***Запустить сервис можно в IntelliJ IDEA:***
- в начале необходимо инициализировать БД выполнив
  java-filmorate/src/main/java/ru/yandex/practicum/filmorate/resources/schema.sql -> "run"
- затем запустите приложение
  java-filmorate/src/main/java/ru/yandex/practicum/filmorate/FilmorateApplication -> "run"