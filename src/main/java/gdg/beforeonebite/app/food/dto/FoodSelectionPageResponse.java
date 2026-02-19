package gdg.beforeonebite.app.food.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record FoodSelectionPageResponse(
        FoodSelectionResponse recent,
        TodaySummary todaySummary,
        List<FoodSelectionResponse> todaySelections,
        List<DayGroup> groups
) {
    @Builder
    public record TodaySummary(
            double totalCalories,
            String message,
            int walkingMinutes
    ) {
    }

    @Builder
    public record DayGroup(
            String dayLabel,
            List<FoodSelectionResponse> items
    ) {
    }
}
