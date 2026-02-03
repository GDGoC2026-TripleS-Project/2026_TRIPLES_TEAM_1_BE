package gdg.beforeonebite.global.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }
}

