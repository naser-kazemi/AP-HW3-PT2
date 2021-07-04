package Model.Exceptions;

public class NotFocusedOnHostException extends Exception {

    public NotFocusedOnHostException() {
        super("you must focus on a host before using this command");
    }
}
