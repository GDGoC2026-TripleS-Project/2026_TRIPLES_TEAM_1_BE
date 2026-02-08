package gdg.beforeonebite.app.food.dto;

import lombok.Builder;

@Builder
public record FoodBestCompareResponse(
        FoodRecommendDto current,
        FoodRecommendDto bestRecommendation
) {
}

