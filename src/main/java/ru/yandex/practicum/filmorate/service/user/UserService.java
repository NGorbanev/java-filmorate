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
            log.info("User was posted");
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
    public List<User> addFriend(int friend1Id, int friend2Id) {
        if (friend1Id <= 0 || friend2Id <= 0) {
            log.warn("addFriend request for 0 or less value. id1={}, id2={}", friend1Id, friend2Id);
            throw new ObjectNotFoundException("UserID shouldn't be 0 or less");
        }
        User user1 = userStorage.getUser(friend1Id);
        User user2 = userStorage.getUser(friend2Id);
        user1.addFriend(user2);
        user2.addFriend(user1);
        //userStorage.putUser(friend1Id, user1);
        //userStorage.putUser(friend2Id, user2);
        userStorage.putUser(user1.getId(), user1);
        userStorage.putUser(user2.getId(), user2);
        log.info("Request addFriend was served successfully");
        log.debug("User id={} now has {} friends: {}", user1.getId(), user1.getFriends().size(), user1.getFriends());
        log.debug("User id={} now has {} friends: {}", user2.getId(), user2.getFriends().size(), user2.getFriends());
        return new ArrayList<User>(List.of(user1, user2));
    }

    public List<User> removeFriend(int friend1Id, int friend2Id) {
        User user1 = userStorage.getUser(friend1Id);
        User user2 = userStorage.getUser(friend2Id);
        user1.removeFriend(user2);
        user2.removeFriend(user1);
        userStorage.putUser(friend1Id, user1);
        userStorage.putUser(friend2Id, user2);
        log.info("Request removeFriend was served successfully");
        log.debug("User id={} now has {} friends: {}", user1.getId(), user1.getFriends().size(), user1.getFriends());
        log.debug("User id={} now has {} friends: {}", user2.getId(), user2.getFriends().size(), user2.getFriends());
        return new ArrayList<>(List.of(user1, user2));

    }

    public List<User> getFriendsList(int userId) {
        User user = userStorage.getUser(userId);
        List<User> result = new ArrayList<>();
        for (int friendId : user.getFriends()) {
            result.add(userStorage.getUser(friendId));
        }
        log.info("Request getFriendList was served successfully");
        log.debug("User id={} has {} friends: {}", userId, result.size(), result);
        return result;
    }

    public List<User> getCommonFriends(int userId, int friendId) {
        List<User> result = new ArrayList<>();
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        for (int usersFriend : user.getFriends()) {
            if (friend.getFriends().contains(usersFriend)) result.add(userStorage.getUser(usersFriend));
        }
        log.info("Request getCommonFriends was served successfully");
        log.debug("User id={} has {} common friends with user id={} : {}", userId, result.size(), friendId, result);
        return result;

    }
}
