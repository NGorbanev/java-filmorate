package ru.yandex.practicum.filmorate.storage.user;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    public User postUser(User user);

    public User putUser(int id, User user);

    public User getUser(int id);

    public Collection<User> getUserList();

    public Collection<User> getFriendsOfUser(int userId);

    public Collection<User> addFriend(int userId, int friendId);

    public Collection<User> removeFriend(int userId, int friendId);
}
