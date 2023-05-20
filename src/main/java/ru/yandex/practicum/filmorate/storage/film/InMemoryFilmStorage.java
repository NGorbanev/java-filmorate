package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private int id = 1;

    private int generateId() {
        return id++;
    }

    @Override
    public Film postFilm(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Request was successfully operated");
        return film;
    }

    @Override
    public Film putFilmNoArgs(Film film) {
            films.put(film.getId(), film);
            log.info("Request was successfully operated");
            return films.get(film.getId());
    }

    @Override
    public Film putFilm(int id, Film film) {
         film.setId(id);
         films.put(id, film);
         log.info("Film id={} was updated", id);
         return films.get(id);

    }

    @Override
    public Collection<Film> getFilmsAsArrayList(){
        return films.values();
    }

    @Override
    public Film getFilmById(int id) {
            return films.get(id);
    }
}
