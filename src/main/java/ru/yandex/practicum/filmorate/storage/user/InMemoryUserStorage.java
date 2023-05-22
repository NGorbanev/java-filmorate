package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserValidator;

import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> userList = new HashMap<>();
    private final UserValidator validator = new UserValidator(this);
    int id = 0;

    private int generateUserID() {
        id++;
        return id;
    }

    @Override
    @Validated
    public User postUser(User user) {
        if (validator.validate(user, true)) {
            user.setId(generateUserID());
            userList.put(user.getId(), user);
            log.info("Request was successfully served");
            return user;
        }
        throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
    }

    @Override
    @Validated
    public User putUser(int id, User user) {
        if (userList.containsKey(id)) {
            if (validator.validate(user, false)) {
                user.setId(id);
                userList.put(id, user);
                return user;
            }
        } else {
            log.warn("User id={} was not found", id);
            throw new ObjectNotFoundException(String.format("User id=%s was not found", id));
        }
        throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
    }

    @Override
    @Validated
    public User postUserNoArgs(User user) {
        if (userList.containsKey(user.getId())) {
            if (validator.validate(user, false)) {
                userList.put(user.getId(), user);
                log.info("User id={} was successfully updated", user.getId());
                return userList.get(user.getId());
            }
        }
        throw new ObjectNotFoundException(String.format("User id=%s was not found", id));
    }

    @Override
    public User getuser(int id) {
        if (userList.containsKey(id)) {
            return userList.get(id);
        } else {
            log.warn("User id={} was not found", id);
            throw new ObjectNotFoundException(String.format("User id=%s was not found", id));
        }
    }

    @Override
    public Collection<User> getUserList() {
        return userList.values();
    }
}
