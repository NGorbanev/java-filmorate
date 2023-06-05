package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    public Film postFilm(Film film);

    public Film putFilmNoArgs(Film film);

    public Film putFilm(int id, Film film);

    public Collection<Film> getFilmsAsArrayList();

    public Film getFilmById(int id);
}
