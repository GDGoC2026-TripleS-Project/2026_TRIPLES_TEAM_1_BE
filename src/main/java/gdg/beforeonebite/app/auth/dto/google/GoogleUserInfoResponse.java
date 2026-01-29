package gdg.beforeonebite.app.auth.dto.google;

public record GoogleUserInfoResponse (
        String sub,
        String email,
        String name
) {}
