package ru.yandex.practicum.filmorate.storage.film.mapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilmMapper implements RowMapper<Film> {
    JdbcTemplate jdbcTemplate;

    public FilmMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    List<Film> films = new ArrayList<>();
    boolean workedOut = false;
    StringBuilder filmsForRequest = new StringBuilder();
    Film film;
    List<Integer> likes = new ArrayList<>();

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
       Film film = Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("film_description"))
                .releaseDate((rs.getDate("release_date").toLocalDate()))
                .duration(rs.getInt("duration"))
                .mpa(Mpa.builder()
                        .id(rs.getInt("rating_id"))
                        .name(rs.getString("rating_name"))
                        .description(rs.getString("rating_description"))
                        .build())
                .build();
        return setGenresList(getLikesSet(film));

    }

    private Film getLikesSet(Film film) {
        Set<Integer> likers = new HashSet<>((jdbcTemplate.queryForList(
                "SELECT u.USER_ID FROM USERS u RIGHT JOIN likes l ON l.USER_ID = u.USER_ID WHERE l.FILM_id = ?",
                int.class, film.getId())));
        film.setLikeSet(likers);
        return film;

    }

    private Film setGenresList(Film film) {
        List<Genre> genreList = new ArrayList<>(jdbcTemplate.query(
                "SELECT g.genre_id, g.genre_name " +
                        "FROM genres g " +
                        "LEFT JOIN film_genres fg ON g.genre_id = fg.genre_id " +
                        "LEFT JOIN films f ON f.film_id = fg.film_id " +
                        "WHERE f.film_id = ?;", new GenreMapper(), film.getId()));
        film.setGenres(genreList);
        return film;
    }
}
