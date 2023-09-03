package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
@Qualifier("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private int id = 1;

    private int generateId() {
        return id++;
    }

    final List<Genre> genreList = List.of(
            Genre.builder().id(1).name("Комедия").build(),
            Genre.builder().id(2).name("Эротика").build(),
            Genre.builder().id(3).name("Мультфильм").build(),
            Genre.builder().id(4).name("Игровой").build(),
            Genre.builder().id(5).name("Боевик").build(),
            Genre.builder().id(6).name("Ужасы").build(),
            Genre.builder().id(7).name("Семейный").build(),
            Genre.builder().id(8).name("Драма").build());


    final List<Mpa> mpaList = List.of(
            Mpa.builder().id(1).name("G").build(),
            Mpa.builder().id(2).name("PG").build(),
            Mpa.builder().id(3).name("PG-13").build(),
            Mpa.builder().id(4).name("R").build(),
            Mpa.builder().id(5).name("NC-17").build());

    @Override
    public Film postFilm(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Request was successfully operated");
        return film;
    }

    @Override
    public Film putFilm(int id, Film film) {
         film.setId(id);
         films.put(id, film);
         log.info("Film id={} was updated", id);
         return films.get(id);

    }

    @Override
    public Collection<Film> getFilmsAsArrayList() {
        return films.values();
    }

    @Override
    public Film getFilmById(int id) {
            return films.get(id);
    }

    @Override
    public Genre getGenreById(int id) {
        return genreList.get(id);
    }

    @Override
    public List<Genre> getAllGenres() {
        return genreList;
    }

    @Override
    public Mpa getMpaById(int id) {
        return mpaList.get(id);
    }

    @Override
    public List<Mpa> getAllMpa() {
        return mpaList;
    }
}
