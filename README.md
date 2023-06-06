# java-filmorate

# Database storage schema for Filmorate project
![Storage database schema preview](src/dbSchema/Filmorate DB.png)

[Ссылка на диаграмму в dbdiagram.io](https://dbdiagram.io/d/647cd9ac722eb774945e4b73)

Внешние ключи поля отмечены на схеме **жирным**

Описание таблиц: 

```
Table films {
film_id integer [primary key]
film_name varchar
release_date date
duration integer
mpa integer
}

table users {
user_id integer [primary key]
email varchar
login varchar
name varchar
birthday date
}

table frindship {
friend_1_id integer
friend_2_id integer
friendship_acceptance boolean
}

table likes {
film_id integer
user_id integer
}

table mpa_ratings {
rating_id integer [primary key]
rating_name varchar
rating_description varchar
}

table gernes {
genre_id integer [primary key]
genre_name varchar
}

table film_genres {
film_id integer [primary key]
genre_id integer
}
```


## Few request examples

Get film by ID:
```sql
SELECT
    film_id,
    film_name,
    duration,
    release_date
FROM film
WHERE film_id = 1
LIMIT 5;
```
Get all user's friend's names
```sql
SELECT
    usr.name
FROM users AS usr
JOIN friendship AS frList ON usr.user_id = frList.fiend_1_id
WHERE user_id IN (
    SELECT
        friend_2_id
    FROM friendship
    WHERE firendship_acceptance = true
)
   
```

