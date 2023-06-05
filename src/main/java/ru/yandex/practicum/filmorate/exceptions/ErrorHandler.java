package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFoundException(final ObjectNotFoundException e) {
        log.warn("Response 404, cause: {}", e.getMessage());
        return new ErrorResponse(String.format("Object not found. Cause: %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOtherExceptions(final OtherException e) {
        log.warn("Response 500, cause: {}", e.getMessage());
        return new ErrorResponse(String.format("Something went wrong: %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleNullPointerException(final NullPointerException e) {
        log.warn("NPE error: {}", e.getMessage());
        return new ErrorResponse(String.format("NullPointer error: %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidatorExceptions(final ValidatorException e) {
        log.error("Response 400, cause: {}", e.getMessage());
        return new ErrorResponse(String.format("Bad request. Cause: %s", e.getMessage()));
    }

}
