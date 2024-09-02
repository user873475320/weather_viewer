package exception.client;

import lombok.Getter;

@Getter
public class InvalidUserRequestException extends RuntimeException{
    private final int statusCode;
    private final boolean isOnlyShowBanner;

    public InvalidUserRequestException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
        this.isOnlyShowBanner = false;
    }

    public InvalidUserRequestException(String message, int statusCode, boolean isOnlyShowBanner) {
        super(message);
        this.statusCode = statusCode;
        this.isOnlyShowBanner = isOnlyShowBanner;
    }

    public InvalidUserRequestException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.isOnlyShowBanner = false;
    }
}