package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFound;
import ru.yandex.practicum.filmorate.exceptions.ValidatorException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@Slf4j
public class FilmController {
    private static final int maxDescriptionLength = 200;
    private static final LocalDate minFilmDate = LocalDate.of(1895,12,28);
    private final HashMap<Integer, Film> films = new HashMap<>();
    private int id = 1;

    private int generateId() {
        return id++;
    }

    private boolean validator(Film film) {
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
            throw new ValidatorException("Film duration must me more than 0");
        }
        return true;
    }

    @PostMapping("/films")
    public Film postFilm(@RequestBody Film film) {
        if (log.isInfoEnabled()) {
            log.info("Film POST request received: " + film.toString());
        }
        if (validator(film)) {
            film.setId(generateId());
            films.put(film.getId(), film);
            log.info("Request was successfully operated");
            return film;
        }
        return null;
    }


    //@NonNull
    @PutMapping("/films")
    public Film putFilmNoArgs(@RequestBody Film film) {
        log.info("Film PUT request received: {}", film.toString());
        if (films.containsKey(film.getId())) {
            if (validator(film)) {
                films.put(film.getId(), film);
                log.info("Request was successfully operated");
                return films.get(film.getId());
            }
        } else {
            throw new ObjectNotFound("Film id=" + film.getId() + " not found");
        }
        return null;
    }

    @PutMapping("/films/{id}")
    public Film putFilm(@PathVariable int id, @RequestBody Film film) {
        log.info("Film PUT request received: " + film.toString());
        if (!films.containsKey(id)) {
            log.warn("Film id={} was not found", id);
            throw new ObjectNotFound("Film for update is not found");
        }
        if (validator(film)) {
            film.setId(id);
            films.put(id, film);
            log.info("Film id={} was updated", id);
            return films.get(id);
        }
        throw new ObjectNotFound("Film id=" + id + " not found");
    }

    @GetMapping("/films")
    public Collection<Film> getFilmsAsArrayList() {
        if (films.size() > 0) {
            return films.values();
        } else throw new ResponseStatusException(HttpStatus.valueOf(418), "Film list is empty");
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable int id) {
        if (films.containsKey(id)) return films.get(id);
        else throw new ObjectNotFound("Film id=" + id + " not found");
    }
}

