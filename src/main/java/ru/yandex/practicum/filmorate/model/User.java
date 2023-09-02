package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.exceptions.OtherException;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class User {
    int id;
    String email;
    String login;
    String name;
    LocalDate birthday;
    //@JsonIgnore
    Set<Integer> friends;

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

    //public Set<Integer> getFriendsId() {
    //    return friends;
    //}
}
