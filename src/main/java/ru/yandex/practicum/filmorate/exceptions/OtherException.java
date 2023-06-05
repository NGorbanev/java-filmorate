package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OtherException extends RuntimeException {
    public OtherException(String msg) {
        super(msg);
    }
}
