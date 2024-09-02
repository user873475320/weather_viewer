package exception.server;

public class OpenWeatherApiInteractionException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Failed to retrieve weather data";

    public OpenWeatherApiInteractionException() {
        this(DEFAULT_MESSAGE);
    }

    public OpenWeatherApiInteractionException(String message) {
        super(message);
    }

    public OpenWeatherApiInteractionException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }
}