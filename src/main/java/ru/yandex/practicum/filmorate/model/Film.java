package ru.yandex.practicum.filmorate.model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.time.LocalDate;


@Data
@EqualsAndHashCode
public class Film {
    int id;
    @NonNull String name;
    @NonNull String description;
    @NonNull LocalDate releaseDate;
    @NonNull int duration;
}
