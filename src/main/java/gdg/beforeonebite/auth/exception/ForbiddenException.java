package gdg.beforeonebite.auth.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }
}
