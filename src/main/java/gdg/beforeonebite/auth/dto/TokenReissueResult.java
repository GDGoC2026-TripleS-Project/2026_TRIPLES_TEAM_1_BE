package gdg.beforeonebite.auth.dto;

public record TokenReissueResult(
        String accessToken,
        String newRefreshToken
) {}
