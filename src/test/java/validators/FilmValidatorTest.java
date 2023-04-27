package validators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidatorException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.format.DateTimeParseException;

public class FilmValidatorTest {

    FilmController fc = new FilmController();

    private Film createFilm(String name, String description, String releaseDate, int duration) {
        return new Film(name, description, releaseDate, duration);
    }

    @Test
    public void filmNameTest() {
        // empty filename
        Film film = createFilm("", "Test description", "2013-12-11", 15);
        ValidatorException thrown = Assertions.assertThrowsExactly(ValidatorException.class, () -> fc.postFilm(film));
        Assertions.assertEquals("400 BAD_REQUEST \"Filmname shouldn't be empty\"", thrown.getMessage());
    }

    @Test
    public void maxDescriptionLengthTest() {
        //prepare long description
        String longDescription = "" +
                "Этот фильм создан для того, чтобы представить себе как описание может быть длиннее чем 200 символов. " +
                "В обычной жизни невозможно описать что-то несуществующее более чем одним словом, но этот фильм этим " +
                "и уникален. Тут 252 символа накопилось, с пробелами";
        Film film = createFilm("AnyName", longDescription, "2013-12-11", 15);
        ValidatorException thrown = Assertions.assertThrowsExactly(ValidatorException.class, () -> fc.postFilm(film));
        Assertions.assertEquals(
                "400 BAD_REQUEST \"Film description length is more than 200 symbols\"",
                thrown.getMessage());

        //test on 200 symbols description
        film.setId(1);
        film.setDescription(film.getDescription().substring(0,200));
        System.out.println("Symbols count: " + film.getDescription().length());
        Assertions.assertEquals(fc.postFilm(film), film);
    }

    @Test
    public void releaseDateTest() {
        // day before release
        Film film = createFilm("EarlyBird", "Testing release date", "1895-12-27", 300);
        ValidatorException thrown = Assertions.assertThrowsExactly(ValidatorException.class, () -> fc.postFilm(film));
        Assertions.assertEquals("400 BAD_REQUEST \"The film shouldn't be released before 1895-12-28." +
                "The \"EarlyBird\" was released at 1895-12-27\"",
                thrown.getMessage());

        // no date
        film.setReleaseDate("");
        DateTimeParseException ex = Assertions.assertThrowsExactly(DateTimeParseException.class, () -> fc.postFilm(film));
        Assertions.assertEquals("Text '' could not be parsed at index 0",
                ex.getMessage());

        // date = 28.12.1987
        film.setReleaseDate("1985-12-28");
        film.setId(2);
        Assertions.assertEquals(film, fc.postFilm(film));
    }

    @Test
    public void durationTest() {
        // duration is less than 0
        Film film = createFilm("EarlyBird", "Testing release date", "1895-12-29", -5);
        ValidatorException thrown = Assertions.assertThrowsExactly(ValidatorException.class, () -> fc.postFilm(film));
        Assertions.assertEquals("400 BAD_REQUEST \"Film duration must me more than 0\"", thrown.getMessage());

        //duration is 0
        film.setDuration(0);
        thrown = Assertions.assertThrowsExactly(ValidatorException.class, () -> fc.postFilm(film));
        Assertions.assertEquals("400 BAD_REQUEST \"Film duration must me more than 0\"", thrown.getMessage());

        //duration is > 0
        film.setDuration(1);
        film.setId(3);
        Assertions.assertEquals(film, fc.postFilm(film));
    }


}
