package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@EqualsAndHashCode
public class User {
    int id;
    @NonNull String email;
    @NonNull String login;
    String name;
    @NonNull String birthday;
}
