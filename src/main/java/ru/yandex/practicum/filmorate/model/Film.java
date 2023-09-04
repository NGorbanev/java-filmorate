package ru.yandex.practicum.filmorate.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.exceptions.OtherException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class Film {
    int id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
    Mpa mpa;
    List<Genre> genres;
    @JsonIgnore
    Set<Integer> likeSet;

    public Film addLike(int userId) {
        if (this.likeSet == null) this.likeSet = new HashSet<>();
        if (!likeSet.add(userId))
            throw new OtherException(String.format("User %s has already liked %s", userId, this.name));
        return this;
    }

    public Film removeLike(int userId) {
        if (likeSet.remove(userId)) return this;
        else throw new OtherException(String.format("User %s has never liked %s", userId, this.name));
    }

    public Film setLikes(Set<Integer> likeSet) {
        if (likeSet == null) return this;
        this.likeSet = likeSet;
        return this;
    }

    public void addGenre(Genre g) {
        if (this.genres != null) {
            this.genres.add(g);
        } else {
            genres = new ArrayList<>();
            genres.add(g);
        }
    }
}
