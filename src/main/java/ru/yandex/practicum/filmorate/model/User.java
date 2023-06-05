package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.exceptions.OtherException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode
public class User {
    int id;
    @NonNull String email;
    @NonNull String login;
    String name;
    @NonNull LocalDate birthday;
    private final Set<Integer> friends = new HashSet<>();

    public User addFriend(User friend) {
        if (!friends.add(friend.getId())) throw new OtherException(
                String.format("User id=%s is already a friend of id=%s", friend.getId(), this.getId()));
        return this;
    }

    public User removeFriend(User friend) {
        if (friends.remove(friend.getId())) return this;
        else throw new OtherException(
                        String.format("User %s hasn't been at %s friendlist", friend.getName(), this.name));
    }
}
