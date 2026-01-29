package gdg.beforeonebite.app.food.dto;

import gdg.beforeonebite.app.food.domain.Brand;
import gdg.beforeonebite.app.food.domain.Food;
import lombok.Builder;

@Builder
public record FoodSearchResponse(
        Long foodId,
        String foodName,
        Brand brand,
        String category,
        double calories,
        String guideMessage,
        int walkingMinutes,
        int activityMinutes

) {
    public static FoodSearchResponse from(Food food, CalorieGuideDto calorieGuideDto) {
        return FoodSearchResponse.builder()
                .foodId(food.getId())
                .foodName(food.getFoodName())
                .brand(food.getBrand())
                .category(food.getCategory())
                .calories(food.getCalories())
                .guideMessage(calorieGuideDto.message())
                .walkingMinutes(calorieGuideDto.walkingMinutes())
                .activityMinutes(calorieGuideDto.activityMinutes())
                .build();
    }
}
