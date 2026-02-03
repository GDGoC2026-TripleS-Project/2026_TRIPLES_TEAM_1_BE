package gdg.beforeonebite.app.food.dto;

import lombok.Builder;

@Builder
public record CalorieGuideDto(
        String message,
        int walkingMinutes,
        int activityMinutes
) {
}
