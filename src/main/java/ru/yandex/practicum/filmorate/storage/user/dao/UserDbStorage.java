package ru.yandex.practicum.filmorate.storage.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.mapper.UserMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.Map;


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
            stmt.setDate (4, Date.valueOf(user.getBirthday()));
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
        log.trace("Updating user id={} friendlist...", id);
        // friend list update
        if (user.getFriends() != null) {
            log.trace("User id={} 'friends' field is not null", id);
            //SqlRowSet friendsRow;
            for (int friendId : user.getFriends()) {
                SqlRowSet friendsRow;
                log.trace("Looking for friend id={} at userId={} friend list..", friendId, id);
                friendsRow = jdbcTemplate.queryForRowSet(
                        "SELECT friend_2_id FROM friendship WHERE (friend_1_id = ? AND friend_2_id = ?)", id, friendId);
                //List<Map<String, Object>> friendsRow = jdbcTemplate.queryForList("SELECT friend_2_id FROM friendship WHERE friend_1_id = ?", id);
                if (friendsRow.next()) {
                    log.trace("User id={} already has user id={} at friend list. No need to add.", id, friendId);
                } else {
                    log.trace("Friend id={} is not found at user id={} friend list. Adding..", friendId, id);
                    jdbcTemplate.update("INSERT INTO friendship(friend_1_id, friend_2_id) VALUES (?, ?)", id, friendId);
                    log.trace("Friend id={} is added to user's id={} friendship table", friendId, id);
                }
            }
            log.debug("Friend list for user id={} updated", id);
            return user;
        } else {
            log.trace("User id={} 'friends' field is null. No need to update friendship table", id);
            return user;
        }

        /*
        user.setId(id);
        User updatedUser;
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", id);
        if (userRows.next()) {
            updatedUser = jdbcTemplate.queryForObject("select * from users where user_id = ?",
                        new UserMapper(jdbcTemplate), user.getId());
            String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?;";
            jdbcTemplate.update(sqlQuery,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    Date.valueOf(user.getBirthday()),
                    id);

            if (user.getFriends() != null) {
                log.trace("Updating friend list for user id={}", user.getId());
                SqlRowSet friendRows;
                for (int friendId : user.getFriends()) {
                    friendRows = jdbcTemplate.queryForRowSet(
                            //"SELECT friend_1_id FROM friendship WHERE friend_2_id = ?", friendId);
                            "SELECT friend_2_id FROM friendship WHERE friend_1_id = ?", friendId);
                    if (friendRows.next()) {
                        log.trace("Friend id={} of user id={} found, no need to add", friendId, user.getId());
                    } else {
                        log.trace("Adding friend id={} to user id={}", friendId, user.getId());
                        jdbcTemplate.update("INSERT INTO friendship(friend_1_id, friend_2_id) VALUES (?, ?)",id, friendId);
                        log.trace("Friend added");
                    }
                }
            }
            log.info("User {} was successfully updated", user.getLogin());
            return user;
        } else throw new ObjectNotFoundException(String.format("User id=%s was not found", id));
        */
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
    public Collection<User> getUserList() {
        String query = "SELECT * FROM users;";
        log.info("GetAllUsers() success");
        return jdbcTemplate.query(query, new UserMapper(jdbcTemplate));
    }
}
