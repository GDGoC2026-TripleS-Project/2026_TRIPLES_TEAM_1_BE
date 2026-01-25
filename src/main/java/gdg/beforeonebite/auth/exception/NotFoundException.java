package gdg.beforeonebite.auth.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }
}

