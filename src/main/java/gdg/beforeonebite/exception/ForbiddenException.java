package gdg.beforeonebite.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }
}
