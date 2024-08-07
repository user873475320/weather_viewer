package exception;

public class DatabaseInteractionException extends RuntimeException {

    public DatabaseInteractionException(String message) {
        super(message);
    }

    public DatabaseInteractionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseInteractionException(Throwable cause) {
        super(cause);
    }
}
