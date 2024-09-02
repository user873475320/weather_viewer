package exception.client;

public class LoginException extends RuntimeException {
    public LoginException(String message) {
        super(message);
    }
}