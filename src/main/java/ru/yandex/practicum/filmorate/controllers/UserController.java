package ru.yandex.practicum.filmorate.controllers;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService us) {
        this.userService = us;
    }

    @PostMapping("/users")
    @Valid
    public User postUser(@Valid @RequestBody User user) {
        log.info(String.format("POST request for creating user=%s received", user.getEmail()));
        return userService.postUser(user);
    }

    @PutMapping("/users/{id}")
    @Valid
    public User putUser(@PathVariable int id, @Valid @RequestBody User user) {
        log.info(String.format("PUT request for userId=%s received", id));
        return userService.putUser(id, user);
    }

    @NonNull
    @PutMapping("/users")
    @Valid
    public User postUserNoArgs(@Valid @RequestBody User user) {
        log.info(String.format("PUT request for userId=%s received (no params)", user.getId()));
        return userService.putUser(user.getId(), user);
    }

    @GetMapping("/users/{id}")
    public User getuser(@PathVariable int id) {
        log.info(String.format("GET request for userdId=%s received", id));
        return userService.getuser(id);
    }

    @GetMapping("/users")
    public Collection<User> getUserList() {
        log.info(String.format("GET request for all users received"));
        return userService.getUserList();
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    @Valid
    public List<User> addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info(String.format("PUT request for making friends received: userId=%s, friendId=%s", id, friendId));
        return userService.addFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriendList(@PathVariable int id) {
        log.info(String.format("GET request for all userId=%s friends received", id));
        return userService.getFriendsList(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info(String.format("GET request for common friends received: userId=%s, friendId=%s", id, otherId));
        return userService.getCommonFriends(id, otherId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    @Valid
    public List<User> deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.removeFriend(id, friendId);
    }

}
