# java-filmorate

# Database storage schema for Filmorate project
![Storage database schema preview](src/dbSchema/Filmorate DB.png)

[Ссылка на диаграмму в dbdiagram.io](https://dbdiagram.io/d/647cd9ac722eb774945e4b73)

Внешние ключи поля отмечены на схеме **жирным**

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

