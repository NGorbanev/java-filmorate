package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

@SpringBootApplication
public class FilmorateApplication {
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(FilmorateApplication.class, args);
		InMemoryFilmStorage inMemoryFilmStorage = context.getBean(InMemoryFilmStorage.class);
		InMemoryUserStorage inMemoryUserStorage = context.getBean(InMemoryUserStorage.class);

	}

}
