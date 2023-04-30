package ru.yandex.practicum.filmorate.controllers;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFound;
import ru.yandex.practicum.filmorate.exceptions.ValidatorException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.*;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@RestController
public class UserController {
    private final HashMap<Integer, User> userList = new HashMap<>();
    int id = 0;

    private int generateUserID() {
        id++;
        return id;
    }

    private boolean validator(User user) {
        if (user == null) return false;

        // email check
        if (user.getEmail().isEmpty()) throw new ValidatorException("Email shouldn't be empty");
        if (!user.getEmail().contains("@")) throw new ValidatorException("Email should have a \"@\" sign");

        // login check
        if (user.getLogin().contains(" ") || user.getLogin().isEmpty()) {
            throw new ValidatorException("Login shouldn't have any spaces or appear empty");
        }

        // userName check
        if (user.getName() == null || user.getName().isEmpty()) user.setName(user.getLogin());

        //birthday check
        if (LocalDate.now().isBefore(user.getBirthday())) throw new ValidatorException("User birthday must be in the past");

        // unique login check
        for (User us : userList.values()) {
            if (user.getLogin().equals(us.getLogin())) throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Login " + user.getLogin() + " already exists");
        }


        return true;
    }

    @PostMapping("/users")
    public User postUser(@RequestBody User user) {
        log.info("POST request for creating user received: {}", user);
        if (validator(user)) {
            user.setId(generateUserID());
            userList.put(user.getId(), user);
            log.info("Request was successfully served");
            return user;
        }
        return null;
    }

    @PutMapping("/users/{id}")
    public User putUser(@PathVariable int id, @RequestBody User user) {
        log.info("PUT request for updating user {} received. User={}", user.getId(), user);
        if (userList.containsKey(id)) {
            if (validator(user)) {
                user.setId(id);
                userList.put(id, user);
                return user;
            }
        } else {
            log.warn("User id={} was not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User id=" + id + " was not found");
        }
    }


    @NonNull
    @PutMapping("/users")
    public User postUserNoArgs(@RequestBody User user) {
        log.info("PUT request for updating user {} received. User={}", id, user.toString());
        if (userList.containsKey(user.getId())) {
            if (validator(user)) {
                userList.put(user.getId(), user);
                log.info("User id={} was successfully updated", user.getId());
                return userList.get(user.getId());
            }
        }
        throw new ObjectNotFound("User id=" + user.getId() + " was not found");
    }

    @GetMapping("/users/{id}")
    public User getuser(@PathVariable int id) {
        if (userList.containsKey(id)) {
            return userList.get(id);
        } else {
            log.warn("User id={} was not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User id=" + id + " was not found");
        }
    }

    @GetMapping("/users")
    public Collection<User> getReturnList() {
        if (userList.size() != 0) {
            return userList.values();
        } else {
            log.warn("Filmlist is empty");
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "User list is empty");
        }
    }
}
