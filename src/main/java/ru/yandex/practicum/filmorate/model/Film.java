package ru.yandex.practicum.filmorate.model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;


@Data
@EqualsAndHashCode
public class Film {
    int id;
    @NonNull String name;
    @NonNull String description;
    @NonNull LocalDate releaseDate;
    @NonNull int duration;
    Set<Integer> likeList = new TreeSet<>();

    public Film addLike(int userId) {
        if (this.likeList == null || !likeList.contains(userId)) likeList.add(userId);
        else {
           throw new RuntimeException(String.format("User %s has already liked %s", userId, this.name));
        }
        return this;
    }
    public Film removeLike(int userId) {
        if (likeList.contains(userId)) {
            likeList.remove(userId);
            return this;
        }
        else throw new RuntimeException(String.format("User %s has never liked %s", userId, this.name));
    }

    public int getLikesAmount(){
        return likeList.size();
    }
}
