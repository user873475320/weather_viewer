package exception;

import org.hibernate.HibernateException;

public class DatabaseInteractionException extends HibernateException {

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
