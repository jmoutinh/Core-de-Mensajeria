package Exceptions;

public class UserBlockedException extends Exception {

    public UserBlockedException() {
    }

    public UserBlockedException(Exception e) {
        super(e);
    }

    public UserBlockedException(String msg) {
        super(msg);
    }

    public UserBlockedException(String msg, Exception e) {
        super(msg, e);
    }
}
