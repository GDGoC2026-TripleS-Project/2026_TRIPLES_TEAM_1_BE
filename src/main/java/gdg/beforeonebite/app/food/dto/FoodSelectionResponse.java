package gdg.beforeonebite.app.food.dto;

import lombok.Builder;

@Builder
public record FoodSelectionResponse(
        Long selectionId,
        String foodName,
        int walkingMinutes,
        int activityMinutes,
        String dayLabel,
        String timeLabel
) {
}
