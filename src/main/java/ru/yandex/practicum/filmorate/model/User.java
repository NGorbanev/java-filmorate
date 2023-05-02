package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;

@Data
@EqualsAndHashCode
public class User {
    int id;
    @NonNull String email;
    @NonNull String login;
    String name;
    @NonNull LocalDate birthday;
}
