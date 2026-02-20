package gdg.beforeonebite.app.user.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record SearchListDto(
        List<String> searchList
) {
}
