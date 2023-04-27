package ru.yandex.practicum.filmorate.controllers;

import lombok.NonNull;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFound;
import ru.yandex.practicum.filmorate.exceptions.ValidatorException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

@RestController
public class UserController {
    HashMap<Integer, User> userList = new HashMap<>();
    Logger log = Logger.getLogger(getClass().getName());
    int id = 0;

    private int generateUserID() {
        id++;
        return id;
    }

    private boolean validator(User user) {
        if (user == null) return false;
        // email check
        boolean hasAt = false;
        boolean hasSpace = false; // for login check
        if (user.getEmail().isEmpty()) throw new ValidatorException("Email shouldn't be empty");
        for (char letter : user.getEmail().toCharArray()) {
            if (letter == '@') hasAt = true;
        }
        if (!hasAt) throw new ValidatorException("Email should have a \"@\" digit");

        // login check
        for (char letter : user.getLogin().toCharArray()) {
            if (letter == ' ') hasSpace = true;
        }
        if (user.getLogin().isEmpty() || hasSpace)throw new ValidatorException("Login shouldn't have any spaces or appear empty");

        // userName check
        if (user.getName() == null || user.getName().isEmpty()) user.setName(user.getLogin());

        //birthday check
        LocalDate userBirthday = LocalDate.parse(user.getBirthday(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Instant userBD = Instant.ofEpochSecond(userBirthday.toEpochSecond(LocalTime.of(00, 00),
                ZoneOffset.of("+00:00"))
        );
        if (Instant.now().isBefore(userBD)) throw new ValidatorException("User birthday must be in the past");
        return true;
    }

    @PostMapping("/users")
    public User posttUser(@RequestBody User user) {
        log.info("POST request for creating user received: " + user.toString());
        if (validator(user)) {
            for (User us : userList.values()) {
                if (user.getLogin().equals(us.getLogin())) throw new ResponseStatusException(HttpStatusCode.valueOf(409),
                        "Login " + user.getLogin() + " already exists");
            }
            user.setId(generateUserID());
            userList.put(user.getId(), user);
            log.info("Request was successfully served");
            return user;
        } else {
            log.warning("Request was rejected as validator check failed");
            return null;
        }
    }

    @PutMapping("/users/{id}")
    public User putUser (@PathVariable int id, @RequestBody User user) {
        log.info("PUT request for updating user " + id + " received. User=" + user.toString());
        if (userList.containsKey(id)) {
            if (validator(user)) {
                user.setId(id);
                userList.put(id, user);
                return user;
            } else {
                throw new ValidatorException("Validation failed");
            }
        } else {
            log.warning("User id=" + id + " was not found");
            throw new ResponseStatusException(HttpStatusCode.valueOf(404), "User id=" + id + " was not found");
        }
    }


    @NonNull
    @PutMapping("/users")
    public User postUserNoArgs(@RequestBody User user) {
        log.info("PUT request for updating user " + id + " received. User=" + user.toString());
        if (userList.containsKey(user.getId())) {
            if (validator(user)) {
                userList.put(user.getId(), user);
                log.info("User id=" + user.getId() + " was successfully updated");
                return userList.get(user.getId());
            }
        } else {
            throw new ObjectNotFound("User id=" + user.getId() + " was not found");
        }
        throw new ObjectNotFound("User id=" + user.getId() + " was not found");
    }

    @GetMapping("/users/{id}")
    public User getuser(@PathVariable int id) {
        if (userList.containsKey(id)) {
            return userList.get(id);
        } else {
            log.warning("User id=" + id + " was not found");
            throw new ResponseStatusException(HttpStatusCode.valueOf(404), "User id=" + id + " was not found");
        }
    }

    @GetMapping("/users")
    public ArrayList<User> getReturnList() {
        if (userList.size() != 0) {
            return new ArrayList<>(userList.values());
        }
        else {
            log.warning("Filmlist is empty");
            throw new ResponseStatusException(HttpStatusCode.valueOf(418), "User list is empty");
        }
    }
}
