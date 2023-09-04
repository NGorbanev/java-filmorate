package ru.yandex.practicum.filmorate.storage.film.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class ServiceFieldsMapper implements RowMapper<HashMap<Integer, Integer>> {
    @Override
    public HashMap<Integer, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
        HashMap<Integer, Integer> genreMap = new HashMap<>();
        genreMap.put(rs.getInt("FILM_ID"), rs.getInt("GENRE_ID"));
        return genreMap;
    }
}
