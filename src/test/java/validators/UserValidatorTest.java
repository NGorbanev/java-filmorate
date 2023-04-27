package validators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidatorException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserValidatorTest {

    UserController uc = new UserController();

    public User createUser(String name, String login, String email, String bDay){
        User user = new User(email, login, bDay);
        user.setName(name);
        return user;
    }

    @Test
    public void emailTest() {
        // empty email
        User user = createUser("Somename", "Somelogin", "", "2013-12-11");
        ValidatorException thrown = Assertions.assertThrowsExactly(ValidatorException.class, ()-> uc.posttUser(user));
        Assertions.assertEquals("400 BAD_REQUEST \"Email shouldn't be empty\"", thrown.getMessage());

        // no @ symbol
        user.setEmail("someEmail");
        thrown = Assertions.assertThrowsExactly(ValidatorException.class, ()-> uc.posttUser(user));
        Assertions.assertEquals("400 BAD_REQUEST \"Email should have a \"@\" digit\"", thrown.getMessage());

        // everything is correct
        user.setEmail("login@server.domen");
        user.setId(1);
        Assertions.assertEquals(user, uc.posttUser(user));
    }

    @Test
    public void loginTest() {
        // login is empty
        User user = createUser("Somename", "", "login@server.domen", "2013-12-11");
        ValidatorException thrown = Assertions.assertThrowsExactly(ValidatorException.class, ()-> uc.posttUser(user));
        Assertions.assertEquals("400 BAD_REQUEST \"Login shouldn't have any spaces or appear empty\"",
                thrown.getMessage());

        // login has spaces
        user.setLogin("Some login");
        thrown = Assertions.assertThrowsExactly(ValidatorException.class, ()-> uc.posttUser(user));
        Assertions.assertEquals("400 BAD_REQUEST \"Login shouldn't have any spaces or appear empty\"",
                thrown.getMessage());
    }

    @Test
    public void nameTest() {
        //if name is empty - login should be used as a name
        User user = createUser("", "Petya2013", "login@server.domen", "2013-12-11");
        user.setId(1);
        uc.posttUser(user);
        Assertions.assertEquals(user.getLogin(), uc.getuser(1).getName());
    }

    @Test
    public void bithdayTest() {
        LocalDate ld = LocalDate.now().plusDays(1);
        String dayInFuture = ld.toString().formatted("yyyy-MM-dd");
        User user = createUser("Ivan", "Petya2013", "login@server.domen", dayInFuture);
        ValidatorException thrown = Assertions.assertThrowsExactly(ValidatorException.class, ()-> uc.posttUser(user));
        Assertions.assertEquals("400 BAD_REQUEST \"User birthday must be in the past\"",
                thrown.getMessage());
    }

}
