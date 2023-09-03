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
        log.trace("User service is set up: {}", this.userService.getClass().getSimpleName());
    }

    @PostMapping("/users")
    @Valid
    public User postUser(@Valid @RequestBody User user) {
        log.info("POST request for creating user={} received", user.getEmail());
        return userService.postUser(user);
    }

    @PutMapping("/users/{id}")
    @Valid
    public User putUser(@PathVariable int id, @Valid @RequestBody User user) {
        log.info("PUT request for userId={} received", id);
        return userService.putUser(id, user);
    }

    @NonNull
    @PutMapping("/users")
    @Valid
    public User postUserNoArgs(@Valid @RequestBody User user) {
        log.info("PUT request for userId={} received (no params)", user.getId());
        return userService.putUser(user.getId(), user);
    }

    @GetMapping("/users/{id}")
    public User getuser(@PathVariable int id) {
        log.info("GET request for userdId={} received", id);
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
        log.info("PUT request for making friends received: userId={}, friendId={}", id, friendId);
        return userService.addFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriendList(@PathVariable int id) {
        log.info("GET request for all userId={} friends received", id);
        return userService.getFriendsList(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("GET request for common friends received: userId={}, friendId={}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    @Valid
    public List<User> deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("DELETE request for user id={} to delete from friendlist of user id={}", friendId, id);
        return userService.removeFriend(id, friendId);
    }

}
