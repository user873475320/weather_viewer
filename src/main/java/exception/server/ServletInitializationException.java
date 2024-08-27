package exception.server;

public class ServletInitializationException extends RuntimeException {
    public ServletInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}