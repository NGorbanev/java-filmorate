package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

@Data
@EqualsAndHashCode
public class User {
    int id;
    @NonNull String email;
    @NonNull String login;
    String name;
    @NonNull LocalDate birthday;
    Set<Integer> friendList = new TreeSet<>();

    public void addFriend(User friend) {
        if (friend.getClass() == User.class) friendList.add(friend.getId());
        else throw new RuntimeException("Only users can be added as friends");
    }

    public void removeFriend(User friend) {
        if (!friendList.contains(friend.getId())) throw new RuntimeException(
                String.format("User %s hasn't been at %s friendlist", friend.getName(), this.name));
        else friendList.remove(friend.getId());
    }
}
