package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.exceptions.ValidatorException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidator {

    private static final int maxDescriptionLength = 200;
    private static final LocalDate minFilmDate = LocalDate.of(1895,12,28);

    public boolean validate(Film film) {
        if (film == null) return false;
        LocalDate releaseDate = film.getReleaseDate();

        // name shouldn't be empty
        if (film.getName().isEmpty()) {
            throw new ValidatorException("Filmname shouldn't be empty");
        }

        // max description lenght shouldn't me more than x
        if (film.getDescription().length() > maxDescriptionLength) {
            throw new ValidatorException("Film description length is more than " + maxDescriptionLength + " symbols");
        }

        // release date not earlier than y
        if (releaseDate.isBefore(minFilmDate)) {
            throw new ValidatorException("The film shouldn't be released before " + minFilmDate);
        }

        // duration check
        if (film.getDuration() <= 0) {
            throw new ValidatorException("Film duration must be more than 0");
        }
        return true;
    }

}
