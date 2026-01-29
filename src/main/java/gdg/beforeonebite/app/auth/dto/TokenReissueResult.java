package gdg.beforeonebite.app.auth.dto;

public record TokenReissueResult(
        String accessToken,
        String newRefreshToken
) {}
