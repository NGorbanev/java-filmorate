package ru.yandex.practicum.filmorate.storage.film.mapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class FilmMapper implements RowMapper<Film> {
    JdbcTemplate jdbcTemplate;

    public FilmMapper (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("film_description"))
                .releaseDate((rs.getDate("release_date").toLocalDate()))
                .duration(rs.getInt("duration"))
                .mpa(rs.getInt("mpa"))
                .build();
        return getLikesSet(film);
    }

    /*
    private User loadFriendSet(User user) {
        Set<Integer> friendSet = new HashSet<>(jdbcTemplate.queryForList(
                "SELECT friend_2_id FROM friendship WHERE friend_1_id = ?;", Integer.class, user.getId()));
        user.setFriends(friendSet);
        return user;
    }
     */

    private Film getLikesSet(Film film) {
        Set<Integer> likers = new HashSet<>((jdbcTemplate.queryForList(
                "SELECT u.USER_ID FROM USERS u RIGHT JOIN LIKES l ON l.USER_ID = u.USER_ID WHERE l.FILM_id = ?",
                int.class, film.getId())));
        film.setLikes(likers);
        return film;
    }
}
