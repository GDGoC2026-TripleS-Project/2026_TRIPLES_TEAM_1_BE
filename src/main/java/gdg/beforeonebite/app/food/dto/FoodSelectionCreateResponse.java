package gdg.beforeonebite.app.food.dto;

import lombok.Builder;

@Builder
public record FoodSelectionCreateResponse(
        Long selectionId,
        String message,
        int walkingMinutes
) {
}
