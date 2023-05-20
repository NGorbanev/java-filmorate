package ru.yandex.practicum.filmorate.storage.user;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    public User postUser(User user);
    public User putUser(int id, User user);
    public User postUserNoArgs(User user);
    public User getuser(int id);
    public Collection<User> getUserList();
    //public Collection<User> addFriend(int sourceUserId, int newFriendId);
    //public List<User> removeFriend(int sourceUserId, int exFriendId);
    //public List<User> getFriendsList(User user);

}
