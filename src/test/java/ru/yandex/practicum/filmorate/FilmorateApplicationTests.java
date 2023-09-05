package ru.yandex.practicum.filmorate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
	private final UserDbStorage userStorage;
	private final FilmDbStorage filmDbStorage;
	User testUser;
	Film testFilm;


	@Autowired
	public FilmorateApplicationTests(UserDbStorage userDbStorage, FilmDbStorage filmDbStorage) {
		this.testUser = User.builder().email("test@mail.ru")
				.login("TestUser")
				.name("TestName")
				.birthday(LocalDate.of(1955,2,8))
				.build();
		this.testFilm = Film.builder()
				.name("Test movie")
				.description("This movie is about hard java study process :)")
				.releaseDate(LocalDate.of(2022,9,11))
				.duration(300)
				.mpa(Mpa.builder().id(5).build())
				.build();
		this.userStorage = userDbStorage;
		this.filmDbStorage = filmDbStorage;
	}

	@Test
	public void testPostAndGetUser() {
		userStorage.postUser(testUser);
		Optional<User> userOptional = Optional.ofNullable(userStorage.getUser(1));
		Assertions.assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						Assertions.assertThat(user).hasFieldOrPropertyWithValue("id", 1)
				);
	}

	@Test
	public void testPutUser() {
		User editedUser = userStorage.putUser(1, testUser);
		editedUser.setLogin("EditedLogin");
		Assertions.assertThat(editedUser).hasFieldOrPropertyWithValue("login", "EditedLogin");
	}

	@Test
	public void testGetUserList() {
		User secondUser = User.builder()
				.email("second@email.com")
				.login("AnotherLogin")
				.name("Some Name")
				.birthday(LocalDate.of(1998, 8, 20))
				.build();
		userStorage.postUser(secondUser);
		Collection<User> userList = userStorage.getUserList();
		Assertions.assertThat(userList).isNotEmpty().hasSize(2);
	}

	@Test
	public void testPostFilmAndGetFilmById() {
		filmDbStorage.postFilm(testFilm);
		Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.getFilmById(1));
		Assertions.assertThat(filmOptional)
				.isPresent()
				.hasValueSatisfying(
						film -> Assertions.assertThat(film).hasFieldOrPropertyWithValue("id", 1)
				);
	}

	@Test
	public void testGetAllFilms() {
		Film secondFilm = Film.builder()
				.name("Test movie part 2")
				.description("This film is about reaching success while study")
				.releaseDate(LocalDate.of(2023,9,1))
				.duration(3)
				.mpa(Mpa.builder().id(2).build())
				.build();
		filmDbStorage.postFilm(secondFilm);
		filmDbStorage.postFilm(testFilm);
		Collection<Film> filmList = filmDbStorage.getFilmsAsArrayList();
		Assertions.assertThat(filmList).hasSize(2);
	}
}
