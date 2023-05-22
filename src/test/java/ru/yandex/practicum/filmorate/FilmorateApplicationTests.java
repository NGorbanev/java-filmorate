package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@SpringBootTest
class FilmorateApplicationTests {

	@Test
	void contextLoads() {
		ApplicationContext context = SpringApplication.run(FilmorateApplication.class);
		FilmStorage filmStorage = context.getBean(FilmStorage.class);
		UserStorage userStorage = context.getBean(UserStorage.class);
	}

}
