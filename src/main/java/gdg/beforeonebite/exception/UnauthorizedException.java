package gdg.beforeonebite.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }
}
