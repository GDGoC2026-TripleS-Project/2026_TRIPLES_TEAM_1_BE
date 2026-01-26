package gdg.beforeonebite.auth.dto.google;

public record GoogleUserInfoResponse (
        String sub,
        String email,
        String name
) {}
