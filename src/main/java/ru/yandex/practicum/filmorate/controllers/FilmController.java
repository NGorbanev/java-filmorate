package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService fc) {
        this.filmService = fc;
    }

    @PostMapping("/films")
    @Valid
    public Film postFilm(@Valid @RequestBody Film film) {
        log.info("POST request received. Body: {}", film);
        return filmService.postFilm(film);
    }

    @PutMapping("/films")
    @Valid
    public Film putFilmNoArgs(@Valid @RequestBody Film film) {
        log.info("PUT request received (no params). Body: {}", film);
        return filmService.putFilmNoArgs(film);
    }

    @PutMapping("/films/{id}")
    @Valid
    public Film putFilm(@PathVariable int id, @Valid @RequestBody Film film) {
        log.info("PUT request for filmId={} received. Body: ", film);
        return filmService.putFilm(id, film);
    }

    @GetMapping("/films")
    public Collection<Film> getFilmsAsArrayList() {
        log.info("GET request for getting all films received");
        return filmService.getFilmsAsArrayList();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable int id) {
        log.info("GET request for filmId={} received", id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    @Valid
    public Film addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("PUT request for adding like. UserID={}, filmID={}", id, userId);
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("DELETE request for deleting like. UserID={}, filmID={}", id, userId);
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getTopRatedFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        log.info("GET request for {} popular films received", count);
        return filmService.getTopLikedFilms(count);
    }
}

