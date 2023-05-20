package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.OtherException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.compare;
@Slf4j
@Service
public class FilmService {

    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private final FilmValidator validator = new FilmValidator();

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    // proxying methods (validator check added here)
    public Film postFilm(Film film) {
        if (validator.validate(film)) {
            return filmStorage.postFilm(film);
        } else return film; // unreachable case
    }

    public Film putFilmNoArgs(Film film) {
        if (validator.validate(film) && filmIdValidator(film.getId())) {
            return filmStorage.putFilmNoArgs(film);
        } else return film; // unreachable case
    }

    public Film putFilm(int id, Film film) {
        if (validator.validate(film) && filmIdValidator(id)) {
            return filmStorage.putFilm(id, film);
        } else return film; // unreachable case
    }

    public Collection<Film> getFilmsAsArrayList() {
        return filmStorage.getFilmsAsArrayList();
    }

    public Film getFilmById(int id) {
        if (filmIdValidator(id)) {
            return filmStorage.getFilmById(id);
        } else throw new ObjectNotFoundException(String.format("Film id=%s not found", id));
    }

    //service methods
    private boolean filmIdValidator(int filmId) {
        if (filmStorage.getFilmById(filmId) == null) {
            log.warn("Film not found error");
            throw new ObjectNotFoundException(String.format("Film id=%s not found", filmId));
        } else return true;
    }

    private boolean userIdValidator(int userId) {
        if (userStorage.getuser(userId) == null) {
            log.warn("User not found error");
            throw new ObjectNotFoundException(String.format("User id=%s not found", userId));
        } else return true;
    }

    // business logic methods
    public Film addLike(int filmId, int userId) {
        if (filmIdValidator(filmId) && userIdValidator(userId)) {
            log.info(String.format("Like successfully added to film id=%s from user id=%s", filmId, userId));
            return filmStorage.putFilm(filmId, filmStorage.getFilmById(filmId).addLike(userId));
        } else throw new OtherException(String.format("Like adding error, FilmId=%s, UserId=%s", filmId, userId));
    }

    public Film removeLike(int filmId, int userId) {
        log.info(String.format("Request for like removal to film id=%s of user id=%s received", filmId, userId));
        if (filmIdValidator(filmId) && userIdValidator(userId)) {
            log.info(String.format("Like from user id=%s to film id=%s was successfully removed", userId, filmId));
            return filmStorage.putFilm(filmId, filmStorage.getFilmById(filmId).removeLike(userId));
        } else throw new OtherException(String.format("Like removal failed, FilmId=%s, UserId=%s", filmId, userId));
    }

    public List<Film> getTopLikedFilms(int numOfFilms) {
        return filmStorage.getFilmsAsArrayList().stream()
                .sorted((o0, o1) -> compare(o1.getLikesAmount(), o0.getLikesAmount()))
                .limit(numOfFilms)
                .collect(Collectors.toList());
    }
}
