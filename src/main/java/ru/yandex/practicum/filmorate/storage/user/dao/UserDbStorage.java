package ru.yandex.practicum.filmorate.storage.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.OtherException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.mapper.UserMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;


@Slf4j
@Component
//@Primary
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        log.info("userDbStorage is used");
    }

    @Override
    public User postUser(User user) {
        String sqlQuery = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ? ,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        log.info("User {} was successfully added", user.getLogin());
        return user;
    }

    @Override
    public User putUser(int id, User user) {
        // user update
        log.debug("Request for updating user {} received", user);
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?;";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                id);
        log.trace("User id={} fields updated", user.getId());
        return user;
    }

    @Override
    public Collection<User> addFriend(int userId, int friendId) {
        try {
            log.trace("Adding user id={} to friends of user id={}", friendId, userId);
            jdbcTemplate.update("INSERT INTO FRIENDSHIP (FRIEND_1_ID, FRIEND_2_ID) VALUES (?, ?);", userId, friendId);
        } catch (DataAccessException ex) {
            throw new OtherException(String.format(
                    "Couldn't add friend id=%s to user id=%s. Seems that they are already friends", friendId, userId));
        }
        return getFriendsOfUser(userId);
    }

    @Override
    public Collection<User> removeFriend(int userId, int friendId) {
        try {
            log.trace("Breaking friendship between user id={} and user id={}", userId, friendId);
            jdbcTemplate.update("DELETE FROM friendship WHERE friend_1_id = ? and friend_2_id = ?", userId, friendId);
        } catch (DataAccessException ex) {
            throw new OtherException(String.format(
                    "Couldn't remove user id=%s from friends of user id=%s. They might not being friends", friendId, userId));
        }
        return getFriendsOfUser(userId);
    }

    @Override
    public Collection<User> getCommonFriends(int userId, int friendId) {
        Collection<User> commnFriends;
        try {
            commnFriends = jdbcTemplate.query(
                    "SELECT u.* FROM users u, friendship f, friendship o " +
                            "WHERE u.user_id = f.friend_2_id and u.user_id = o.friend_2_id and f.friend_1_id = ? and o.friend_1_id = ?",
                    new UserMapper(jdbcTemplate) , userId, friendId);
        } catch (DataAccessException ex) {
            throw new ObjectNotFoundException(String.format(
                    "Couldn't get common friends of user id=%s and id=%s", userId, friendId));
        }
        return commnFriends;
    }

    @Override
    public User getUser(int id) {
        User user;
        try {
            user = jdbcTemplate.queryForObject("SELECT * FROM users WHERE user_id = ?;", new UserMapper(jdbcTemplate), id);
            log.info("User found: id={}", id);
            return user;
        } catch (EmptyResultDataAccessException ex) {
            throw new ObjectNotFoundException(String.format("User id=%s was not found", id));
        }
    }

    @Override
    public List<User> getFriendsOfUser(int userId) {
        List<User> friends = jdbcTemplate.query(
                "SELECT u.* " +
                        "FROM users u " +
                        "LEFT JOIN friendship f ON f.friend_2_id = u.user_id " +
                        "WHERE friend_1_id = ?", new UserMapper(jdbcTemplate), userId);
        log.trace("User's id={} frineds received. Friends amount {}", userId, friends.size());
        return friends;
    }

    @Override
    public Collection<User> getUserList() {
        String query = "SELECT * FROM users;";
        log.info("GetAllUsers() success");
        return jdbcTemplate.query(query, new UserMapper(jdbcTemplate));
    }
}