package gdg.beforeonebite.app.food.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record FoodSearchListResponse(
        String keyword,
        List<FoodSearchResponse> items
) {
}
