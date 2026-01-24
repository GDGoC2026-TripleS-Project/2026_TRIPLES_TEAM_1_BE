package gdg.beforeonebite.auth.dto;

public record GoogleUserInfo(
        String sub,
        String email,
        String name
) {}
