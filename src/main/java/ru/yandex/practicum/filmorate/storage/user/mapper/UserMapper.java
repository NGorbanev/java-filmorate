package ru.yandex.practicum.filmorate.storage.user.mapper;

import org.springframework.jdbc.core.*;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {

    final JdbcTemplate jdbcTemplate;

    public UserMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        // todo добавить обработку SQL Exception
        User user = User.builder()
                .id(resultSet.getInt("user_id"))
                .email(resultSet.getString("email"))
                .login((resultSet.getString("login")))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
        return user;
    }
}
