# java-filmorate
Template repository for Filmorate project.

#### Ссылка на ER-диаграмму:
https://app.quickdatabasediagrams.com/#/d/ozfp4o

#### Примеры SQL запросов:
- Получить список названий всех фильмов  
  SELECT name FROM film;

- Получить список всех пользователей  
  SELECT * FROM users
  
- Получить топ 10 популярных фильмов  
  SELECT name  
  FROM film AS f  
  INNER JOIN like AS l ON f.film_id = like.film_id  
  GROUP BY name  
  ORDER BY COUNT(like.user.id) DESC  
  LIMIT (10);  
  
- Получить список id общих друзей пользователей user1 и user2  
  с id = 1 и id = 2  

  SELECT friend_id  
  FROM user_friend AS uf_1  
  WHERE user_id = 1  
  INNER JOIN (  
  SELECT friend_id  
  FROM user_friend  
  WHERE user_id = 2  
  ) AS uf_2 ON uf_1.friend_id = uf_2.friend_id;  