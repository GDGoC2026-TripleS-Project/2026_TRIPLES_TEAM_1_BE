package gdg.beforeonebite.app.food.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record FoodRecommendationListResponse(
        Long currentFoodBrandId,
        List<FoodRecommendDto> recommendations
) {
}

