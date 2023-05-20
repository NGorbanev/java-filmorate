package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

@SpringBootTest
class FilmorateApplicationTests {

	@Test
	void contextLoads() {
		ApplicationContext context = SpringApplication.run(FilmorateApplication.class);
		InMemoryFilmStorage InMemoryFilmStorage = context.getBean(InMemoryFilmStorage.class);
		InMemoryUserStorage inMemoryUserStorage = context.getBean(InMemoryUserStorage.class);
	}

}
