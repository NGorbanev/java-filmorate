package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.EmptyResultDataAccessException;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.film.mapper.MpaMapper;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    public Film postFilm(Film film);

    public Film putFilm(int id, Film film);

    public Collection<Film> getFilmsAsArrayList();

    public Film getFilmById(int id);

    public Genre getGenreById(int id);
    public List<Genre> getAllGenres();
    public Mpa getMpaById (int id);
    public List<Mpa> getAllMpa();
}
