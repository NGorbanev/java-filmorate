package ru.yandex.practicum.filmorate.storage.film.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.film.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.film.mapper.MpaMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
//@Primary
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        log.info("filmDbStorage is used");
    }

    // base actions
    @Override
    public Film postFilm(Film film) {
        if (film.getMpa() == 0) film.setMpa(1);
        String sqlQuery = "INSERT into FILMS(FILM_NAME, RELEASE_DATE, DURATION, MPA, FILM_DESCRIPTION) VALUES (?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setDate(2, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(3, film.getDuration());
            stmt.setInt(4, film.getMpa());
            stmt.setString(5, film.getDescription());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        log.info("Film {} was successfully added", film.getName());
        return checkLikeSet(film);
    }

    @Override
    public Film putFilm(int id, Film film) {
        if (film.getMpa() == 0) film.setMpa(1);
        String query = "UPDATE films SET film_name = ?, film_description = ?, release_date = ?, duration = ?, mpa = ? " +
                    "WHERE film_id = ?";
        int countLines = jdbcTemplate.update(query,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa(),
                    id);
        if (countLines == 0) {
            log.info("Film id={} was not found", id);
            throw new ObjectNotFoundException(String.format("Film '%s' id=%s was not found", film.getName(), id));
        } else {
            // likes update
            if (film.getLikeSet() != null) {
                log.trace("getLikesSet for fimlm id={} is not null", id);
                for (int like : film.getLikeSet()) {
                    jdbcTemplate.update("INSERT INTO LIKES(film_id, user_id) VALUES (?, ?)", film.getId(), like);
                    log.trace("Likes are updated at database");
                }
            } else log.trace("getLikesSet is null for film id={}. Nothing to update", id);

            log.info("Film {} was updated", film.getName());
            return checkLikeSet(film);
        }
    }

    @Override
    public Collection<Film> getFilmsAsArrayList() {
        return jdbcTemplate.query("SELECT * FROM films", new FilmMapper(jdbcTemplate));
    }

    @Override
    public Film getFilmById(int id) {
        Film film;
        try {
            film = jdbcTemplate.queryForObject(
                    "SELECT f.*, m.RATING_NAME, m.RATING_DESCRIPTION " +
                            "FROM FILMS f JOIN MPA_RATINGS m ON f.MPA = m.RATING_ID WHERE film_id = ?",
                    new FilmMapper(jdbcTemplate), id);
            return film;
        } catch (EmptyResultDataAccessException ex) {
            throw new ObjectNotFoundException(String.format("Film id=%s was not found", id));
        }
    }

    // actions with likes
    private Film checkLikeSet(Film film) {
        if (film.getLikeSet() == null) film.setLikes(new HashSet<>());
        return film;
    }

    // actions with rating and genres
    @Override
    public Genre getGenreById(int id) {
        Genre genre;
        try {
            log.info("Request processed");
            return genre = jdbcTemplate.queryForObject(
                    "SELECT * FROM genres WHERE genre_id = ?", new GenreMapper(), id);
        } catch (EmptyResultDataAccessException ex) {
            throw new ObjectNotFoundException(String.format("Genre id=%s not found", id));
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query("SELECT * FROM genres", new GenreMapper());
    }

    @Override
    public Mpa getMpaById (int id) {
        Mpa mpa;
        try {
            return mpa = jdbcTemplate.queryForObject("SELECT * FROM mpa_ratings WHERE rating_id = ?", new MpaMapper(), id);
        } catch (EmptyResultDataAccessException ex) {
            throw new ObjectNotFoundException(String.format("MPA rating with id=%s not found", id));
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        return jdbcTemplate.query("SELECT * FROM mpa_ratings", new MpaMapper());
    }

}
