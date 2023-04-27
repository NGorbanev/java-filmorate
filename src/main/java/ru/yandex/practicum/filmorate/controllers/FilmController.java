package ru.yandex.practicum.filmorate.controllers;

import lombok.NonNull;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFound;
import ru.yandex.practicum.filmorate.exceptions.ValidatorException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

@RestController
public class FilmController {

    Logger log = Logger.getLogger(this.getClass().getName());

    private final int maxDescriptionLength = 200;
    LocalDate dt = LocalDate.of(1895,12,28);
    private final Instant minFilmDate =
            Instant.ofEpochSecond(dt.toEpochSecond(LocalTime.of(00,00),
            ZoneOffset.of("+00:00")));
    HashMap<Integer, Film> films = new HashMap<>();
    private int id = 1;

    private int generateId() {
        return id++;
    }

    private boolean validator(Film film) {
        if (film == null) return false;
        LocalDate relDate = LocalDate.parse(film.getReleaseDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Instant filmDate =
                Instant.ofEpochSecond(relDate.toEpochSecond(LocalTime.of(00,00),
                ZoneOffset.of("+00:00")));

        // name shouldn't be empty
        if (film.getName().isEmpty()) {
            throw new ValidatorException("Filmname shouldn't be empty");
        }

        // max description lenght shouldn't me more than x
        if (film.getDescription().length() > maxDescriptionLength){
            throw new ValidatorException("Film description length is more than " + maxDescriptionLength + " symbols");
        }

        // release date not earlier than y
        if (filmDate.isBefore(minFilmDate)) {
            throw new ValidatorException("The film shouldn't be released before " +
                    LocalDate.ofInstant(minFilmDate, ZoneOffset.of("+00:00")) + "." +
                    "The \"" + film.getName() + "\" was released at " + relDate);
        }

        if (film.getDuration() <= 0) {
            throw new ValidatorException("Film duration must me more than 0");
        }
        return true;
    }

    @PostMapping("/films")
    public Film postFilm(@RequestBody Film film){
        log.info("Film POST request received: " + film.toString());
        if (validator(film)) {
            film.setId(generateId());
            films.put(film.getId(), film);
            log.info("Request was successfully operated");
            return film;
        }
        return null;
    }


    @NonNull
    @PutMapping("/films")
    public Film putFilmNoArgs(@RequestBody Film film) {
        log.info("Film PUT request received: " + film.toString());
        if (validator(film)){
            if (films.containsKey(film.getId())){
                films.put(film.getId(), film);
                log.info("Request was successfully operated");
                return films.get(film.getId());
            } else {
                throw new ObjectNotFound("Film id=" + film.getId() + " not found");
            }
        }
        return null;
    }

    @PutMapping("/films/{id}")
    public Film putFilm(@PathVariable int id, @RequestBody Film film){
        log.info("Film PUT request received: " + film.toString());
        if (validator(film)){
            if (!films.containsKey(id)) {
                log.warning("Film id=" + id + " was not found");
                throw new ObjectNotFound("Film for update is not found");
            }
            film.setId(id);
            films.put(id, film);
            log.info("Film id=" + id + " was updated");
            return films.get(id);
        }
        throw new ObjectNotFound("Film id=" + id + " not found");
    }

    @GetMapping("/films")
    public ArrayList<Film> getFilmsAtArrayList(){
        if (films.size() > 0) {
            return new ArrayList<>(films.values());
        } else throw new ResponseStatusException(HttpStatusCode.valueOf(418), "Film list is empty");
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable int id) {
        if (films.containsKey(id)) return films.get(id);
        else throw new ObjectNotFound("Film id=" + id + " not found");
    }
}

