package Controller;

import Model.Exceptions.IncorrectPasswordException;
import Model.Exceptions.NoUsernameAvailableException;
import Model.Exceptions.UserNotFoundException;
import Model.User;
import View.ChatMenu;
import View.Menu;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UserConfigController {


    private static UserConfigController instance;

    private UserConfigController() {
    }

    public static UserConfigController getInstance() {

        if (instance == null)
            instance = new UserConfigController();

        return instance;
    }

    private boolean isUsernameValid(String username) {
        Pattern pattern = Pattern.compile("[\\w-]");
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    public void create(String username, String password) throws NoUsernameAvailableException {
        if (User.getUserByUsername(username) == null || !isUsernameValid(username))
            throw new NoUsernameAvailableException();
        new User(username, password);

    }

    public void login(String username, String password) throws UserNotFoundException, IncorrectPasswordException {
        if (User.getUserByUsername(username) == null)
            throw new UserNotFoundException();
        if (!User.getUserByUsername(username).getPassword().equals(password))
            throw new IncorrectPasswordException();
        Menu.setCurrentUser(User.getUserByUsername(username));
        Menu.setLoggedIn(true);
        Menu.setCurrentMenu(new ChatMenu());
    }


}
