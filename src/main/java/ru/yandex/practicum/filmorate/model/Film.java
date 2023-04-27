package ru.yandex.practicum.filmorate.model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;


@Data
@EqualsAndHashCode
public class Film {
    int id;
    @NonNull String name;
    @NonNull String description;
    @NonNull String releaseDate;
    @NonNull int duration;
}
