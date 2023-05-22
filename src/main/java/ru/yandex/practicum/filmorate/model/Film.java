package ru.yandex.practicum.filmorate.model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.exceptions.OtherException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
@EqualsAndHashCode
public class Film {
    int id;
    @NonNull String name;
    @NonNull String description;
    @NonNull LocalDate releaseDate;
    @NonNull int duration;
    private final Set<Integer> likeSet = new HashSet<>();

    public Film addLike(int userId) {
        if (!likeSet.add(userId))
            throw new OtherException(String.format("User %s has already liked %s", userId, this.name));
        else likeSet.add(userId);
        return this;
    }

    public Film removeLike(int userId) {
        if (likeSet.remove(userId)) return this;
        else throw new OtherException(String.format("User %s has never liked %s", userId, this.name));
    }

    public int getLikesAmount() {
        return likeSet.size();
    }
}
