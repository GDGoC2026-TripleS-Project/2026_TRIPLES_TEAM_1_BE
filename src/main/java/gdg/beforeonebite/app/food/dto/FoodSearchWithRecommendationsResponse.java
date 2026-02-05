package gdg.beforeonebite.app.food.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record FoodSearchWithRecommendationsResponse(
        FoodRecommendDto current,
        FoodRecommendDto bestRecommendation,
        List<FoodRecommendDto> otherRecommendations
) {
}
