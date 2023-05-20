package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.ValidatorException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class UserValidator {

    //private ArrayList<User> userList;
    private UserStorage userStorage;

    @Autowired
    public UserValidator(UserStorage userStorage) {
        this.userStorage = userStorage;
        ///userList = new ArrayList<User>(List.of(this.userStorage.getUserList()));
    }

    public boolean validate(User user, boolean needToCheckLogin) {
        if (user == null) return false;

        // email check
        if (user.getEmail().isEmpty()) throw new ValidatorException("Email shouldn't be empty");
        if (!user.getEmail().contains("@")) throw new ValidatorException("Email should have a \"@\" sign");

        // login check
        if (user.getLogin().contains(" ") || user.getLogin().isEmpty()) {
            throw new ValidatorException("Login shouldn't have any spaces or appear empty");
        }

        // userName check
        if (user.getName() == null || user.getName().isEmpty()) user.setName(user.getLogin());

        //birthday check
        if (LocalDate.now().isBefore(user.getBirthday())) throw new ValidatorException("User birthday must be in the past");

        // unique login check
        if (needToCheckLogin) {
            for (User us : userStorage.getUserList()) {
                if (user.getLogin().equals(us.getLogin())) throw new ValidatorException(
                        "Login " + user.getLogin() + " already exists");
            }
        }
        return true;
    }
}
