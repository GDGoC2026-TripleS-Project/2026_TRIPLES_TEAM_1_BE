package gdg.beforeonebite.auth.exception;

public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }

    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
