package gdg.beforeonebite.auth.dto;

public record GoogleUserInfoResponse (
        String sub,
        String email,
        String name
) {}
