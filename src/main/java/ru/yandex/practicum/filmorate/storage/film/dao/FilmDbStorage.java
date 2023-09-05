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
import java.util.*;

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
        String sqlQuery = "INSERT into FILMS(FILM_NAME, RELEASE_DATE, DURATION, MPA, FILM_DESCRIPTION) VALUES (?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setDate(2, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(3, film.getDuration());
            stmt.setInt(4, film.getMpa().getId());
            stmt.setString(5, film.getDescription());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        film.setMpa(updateMpa(film));
        film.setGenres(updateGenres(film));
        log.info("Film {} was successfully added", film.getName());
        return checkLikeSet(film);
    }

    @Override
    public Film putFilm(int id, Film film) {
        String query = "UPDATE films SET " +
                "film_name = ?, " +
                "film_description = ?, " +
                "release_date = ?, " +
                "duration = ?, " +
                "mpa = ? " +
                "WHERE film_id = ?";
        int countLines = jdbcTemplate.update(query,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                id);
        if (countLines == 0) {
            log.warn("Film id={} was not found", id);
            throw new ObjectNotFoundException(String.format("Film '%s' id=%s was not found", film.getName(), id));
        } else {
            // likes update
            if (film.getLikeSet() != null) {
                log.trace("getLikesSet for film id={} is not null", id);
                for (int like : film.getLikeSet()) {
                    jdbcTemplate.update("INSERT INTO LIKES(film_id, user_id) VALUES (?, ?)", film.getId(), like);
                    log.trace("Likes are updated at database");
                }
            } else log.trace("getLikesSet is null for film id={}. Nothing to update", id);
            film.setMpa(updateMpa(film));
            film.setGenres(updateGenres(film));
            log.info("Film {} was updated", film.getName());
            return checkLikeSet(film);
        }
    }

    @Override
    public Collection<Film> getFilmsAsArrayList() {
        String query = "SELECT f.*, mpa.* FROM films f JOIN mpa_ratings mpa ON f.mpa = mpa.rating_id";
        Collection<Film> films = jdbcTemplate.query(query, new FilmMapper(jdbcTemplate));

        return updateLikesAndGenres(films);
    }

    @Override
    public Collection<Film> getTopRatedFilms(int neededAmount) {
        String query =
                "SELECT f.*, mpa.*, COUNT(l.user_id) " +
                        "FROM likes l " +
                        "JOIN films f ON f.film_id = l.film_id " +
                        "JOIN mpa_ratings mpa ON f.mpa = mpa.rating_id " +
                        "GROUP BY f.film_id " +
                        "ORDER BY COUNT(l.user_id) DESC " +
                        "LIMIT ?";
        Collection<Film> films = jdbcTemplate.query(query, new FilmMapper(jdbcTemplate), neededAmount);

        // I had to add this part because of Postman tests required it..
        if (films.size() < neededAmount) {
            Collection<Film> missedAmount = jdbcTemplate.query(
                    "SELECT f.*, mpa.* " +
                            "FROM films f " +
                            "JOIN mpa_ratings mpa ON f.mpa = mpa.rating_id " +
                            "LIMIT ?", new FilmMapper(jdbcTemplate), neededAmount - films.size());
            missedAmount.removeIf(films::contains);
            films.addAll(missedAmount);
        }
        return updateLikesAndGenres(films);
    }

    private Collection<Film> updateLikesAndGenres(Collection<Film> films) {
        String idsIn = String.join(",", Collections.nCopies(films.size(), "?"));
        Object[] idsVal = films.stream().map(Film::getId).toArray();
        String sql = "SELECT l.FILM_id, u.USER_ID FROM USERS u RIGHT JOIN likes l ON l.USER_ID = u.USER_ID WHERE l.FILM_id in (" + idsIn + ")";

        Map<Integer, Set<Integer>> filmLikes = new HashMap<>();
        jdbcTemplate.query(sql,
                (rs) -> {
            filmLikes.computeIfAbsent(rs.getInt("FILM_ID"),
                    k -> new HashSet<>()).add(rs.getInt("USER_ID")); },
                idsVal);

        sql = "SELECT f.film_id, g.genre_id, g.genre_name FROM genres g LEFT JOIN film_genres fg ON g.genre_id = fg.genre_id LEFT JOIN films f ON f.film_id = fg.film_id WHERE f.film_id in (" + idsIn + ")";

        Map<Integer, List<Genre>> filmGenres = new HashMap<>();
        jdbcTemplate.query(sql,
                (rs) -> {
            filmGenres.computeIfAbsent(rs.getInt("FILM_ID"),
                    k -> new ArrayList<>()).add(new Genre(rs.getInt("GENRE_ID"),
                    rs.getString("GENRE_NAME"))); },
                idsVal);

        for (Film f : films) {
            f.setLikeSet(filmLikes.getOrDefault(f.getId(), new HashSet<>()));
            f.setGenres(filmGenres.getOrDefault(f.getId(), new ArrayList<>()));
        }
        return films;
    }

    @Override
    public Film getFilmById(int id) {
        Film film;
        try {
            film = jdbcTemplate.queryForObject(
                    "SELECT f.*, mpa.* " +
                            "FROM films f " +
                            "JOIN mpa_ratings mpa ON mpa.rating_id = f.mpa " +
                            "WHERE f.film_id = ?", new FilmMapper(jdbcTemplate), id
            );
            log.trace("Film id={} was found", id);
            ArrayList<Film> foundOne = new ArrayList<>();
            foundOne.add(film);
            foundOne = (ArrayList<Film>) updateLikesAndGenres(foundOne);
            return foundOne.get(0);
        } catch (EmptyResultDataAccessException ex) {
            throw new ObjectNotFoundException(String.format("Film id=%s was not found", id));
        }
    }

    // actions with likes
    private Film checkLikeSet(Film film) {
        if (film.getLikeSet() == null) film.setLikeSet(new HashSet<>());
        log.trace("Check likes complited. Likes amount: {}", film.getLikeSet().size());
        return film;
    }

    // actions with rating and genres
    private Mpa updateMpa(Film film) {
        Mpa mpa;
        try {
            mpa = jdbcTemplate.queryForObject("SELECT mpa_ratings.* FROM mpa_ratings WHERE rating_id = ?",
                    new MpaMapper(), film.getMpa().getId());
            return mpa;
        } catch (EmptyResultDataAccessException ex) {
            throw new ObjectNotFoundException(String.format("Mpa rating id=%s was not found", film.getMpa().getId()));
        }
    }

    private List<Genre> updateGenres(Film film) {
        if (film.getGenres() == null) {
            log.debug("Film id={} has no genres to store", film.getId());
            log.trace("Empty genre list for film id={} is prepared", film.getId());
            return film.getGenres();
        } else {
            log.debug("Film id={} has {} genres at genre list", film.getId(), film.getGenres().size());
            List<Genre> genreList = new ArrayList<>();
            log.trace("Updating genre tables..");
            jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
            for (Genre g : film.getGenres()) {
                if (!genreList.contains(g)) {
                    genreList.add(g);
                    jdbcTemplate.update("INSERT INTO film_genres(film_id, genre_id) VALUES (?, ?)", film.getId(), g.getId());
                } else log.debug("Genre duplicate found with genre id={}", g.getId());
            }
            for (Genre g : genreList) {
                g.setName(getGenreById(g.getId()).getName());
                log.trace("Genre {} added to film id={}", g.getName(), film.getId());
            }
            log.trace("Genre updating succeed");
            return genreList;
        }
    }

    @Override
    public Genre getGenreById(int id) {
        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject(
                    "SELECT * FROM genres WHERE genre_id = ?", new GenreMapper(), id);
            log.trace("Request get genre id={} processed", id);
            return genre;
        } catch (EmptyResultDataAccessException ex) {
            throw new ObjectNotFoundException(String.format("Genre id=%s not found", id));
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query("SELECT * FROM genres", new GenreMapper());
    }

    @Override
    public Mpa getMpaById(int id) {
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