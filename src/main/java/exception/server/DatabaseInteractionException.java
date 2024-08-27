package exception.server;

public class DatabaseInteractionException extends RuntimeException {

    public DatabaseInteractionException(Throwable cause) {
        super(cause);
    }
}