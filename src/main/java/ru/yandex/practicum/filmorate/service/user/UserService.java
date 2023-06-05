package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
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
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
        this.validator = new UserValidator(userStorage);
    }

// proxy methods
    public User postUser(User user) {
        if (validator.validate(user, true)) {
            return userStorage.postUser(user);
        } else return user;
    }

    public User putUser(int id, User user) {
        if (validator.validate(user, false)) {
            return userStorage.putUser(id, user);
        } else return user;
    }

    public User postUserNoArgs(User user) {
        if (validator.validate(user, false)) {
            return userStorage.postUserNoArgs(user);
        } else return user;
    }

    public User getuser(int id) {
        return userStorage.getUser(id);
    }

    public Collection<User> getUserList() {
        return userStorage.getUserList();
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
        userStorage.putUser(friend1Id, user1);
        userStorage.putUser(friend2Id, user2);
        log.info("Request addFriend was served successfully");
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
        return new ArrayList<>(List.of(user1, user2));

    }

    public List<User> getFriendsList(int userId) {
        User user = userStorage.getUser(userId);
        List<User> result = new ArrayList<>();
        for (int friendId : user.getFriends()) {
            result.add(userStorage.getUser(friendId));
        }
        log.info("Request getFriendList was served successfully");
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
        return result;

    }
}
