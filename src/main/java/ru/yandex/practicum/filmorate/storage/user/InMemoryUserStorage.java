package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> userList = new HashMap<>();
    int id = 0;

    private int generateUserID() {
        id++;
        return id;
    }

    @Override
    public User postUser(User user) {
        user.setId(generateUserID());
        userList.put(user.getId(), user);
        log.info("Request was successfully served");
        return user;
    }

    @Override
    public User putUser(int id, User user) {
        if (userList.containsKey(id)) {
            user.setId(id);
            userList.put(id, user);
            return user;
        } else {
            log.warn("User id={} was not found", id);
            throw new ObjectNotFoundException(String.format("User id=%s was not found", id));
        }
    }

    @Override
    public User postUserNoArgs(User user) {
        if (userList.containsKey(user.getId())) {
            userList.put(user.getId(), user);
            log.info("User id={} was successfully updated", user.getId());
            return userList.get(user.getId());
        }
        throw new ObjectNotFoundException(String.format("User id=%s was not found", id));
    }

    @Override
    public User getUser(int id) {
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
