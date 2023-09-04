package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.OtherException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@Validated
public class UserService {
    private UserStorage userStorage;
    private UserValidator validator;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
        this.validator = new UserValidator(userStorage);
        log.trace("UserStorage is applied: {}", userStorage.getClass().getSimpleName());
    }

// proxy methods
    public User postUser(User user) {
        if (validator.validate(user, true)) {
            User responseUser = userStorage.postUser(user);
            log.debug("{} is posted", user);
            return responseUser;
        } else return null; // unreachable case
    }

    public User putUser(int id, User user) {
        log.debug("Request to update user information with: {}", user);
        if (user.getId() != 0 && user.getId() != id) throw new OtherException("Requested userId and userId are not equal");
        if (validator.validate(user, false)) {
            log.debug("User that will be updated: {}", userStorage.getUser(id));
            User updatedUser = userStorage.putUser(id, user);
            log.info("Put user id={} success", id);
            return updatedUser;
        } else return null; // unreachable case
    }

    public User getuser(int id) {
        User user = userStorage.getUser(id);
        log.debug("User id={} returned. Details: {}", id, user);
        return user;
    }

    public Collection<User> getUserList() {
        Collection<User> response = userStorage.getUserList();
        log.debug("User list returned. Users count = {}", response.size());
        log.info("getUserList() success");
        return response;
    }

// managing methods
    public Collection<User> addFriend(int userId, int friendId) {
        if (userId <= 0 || friendId <= 0) {
            log.warn("addFriend request for 0 or less value. id1={}, id2={}", userId, friendId);
            throw new ObjectNotFoundException("UserID shouldn't be 0 or less");
        }
        return userStorage.addFriend(userId, friendId);
    }

    public Collection<User> removeFriend(int friend1Id, int friend2Id) {
        return userStorage.removeFriend(friend1Id, friend2Id);
    }

    public List<User> getFriendsList(int userId) {
        return userStorage.getFriendsOfUser(userId);
    }

    public List<User> getCommonFriends(int userId, int friendId) {
        List<User> result = new ArrayList<>();
        List<User> usersFriends = userStorage.getFriendsOfUser(userId);
        for (User friend : userStorage.getFriendsOfUser(friendId)) {
            if (usersFriends.contains(friend)) result.add(friend);
        }
        log.info("Request getCommonFriends was served successfully");
        log.debug("User id={} has {} common friends with user id={} : {}", userId, result.size(), friendId, result);
        return result;
    }
}
