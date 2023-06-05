package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidatorException extends RuntimeException {

    public ValidatorException(String message) {
        super(message);
    }
}
