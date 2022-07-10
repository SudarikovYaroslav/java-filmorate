# <img src= https://mir-cdn.behance.net/v1/rendition/projects/230/3da78b54757441.Y3JvcCw4NzIsNjgyLDAsMA.jpg> FILMORATE

---

***Сервис по поиску топ фильмов для просмотра.***

---

### ER-диаграмма:
![](ER-diagram.png)
https://app.quickdatabasediagrams.com/#/d/ozfp4o
  

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
  from film as f  
  join like as l on f.film_id = l.film_id  
  group by name  
  order by count(like.user_id) desc  
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