package gdg.beforeonebite.app.food.dto;

import gdg.beforeonebite.app.food.domain.FoodBrand;
import lombok.Builder;

@Builder
public record FoodSearchResponse(
        Long foodBrandId,
        Long foodId,
        String foodName,
        String brand,
        String category,
        double calories,
        String guideMessage,
        int walkingMinutes,
        int activityMinutes
) {
    public static FoodSearchResponse from(FoodBrand foodBrand, CalorieGuideDto calorieGuideDto) {
        return FoodSearchResponse.builder()
                .foodBrandId(foodBrand.getId())
                .foodId(foodBrand.getFood().getId())
                .foodName(foodBrand.getFood().getFoodName())
                .brand(foodBrand.getBrand() == null ? null : foodBrand.getBrand().getBrandName())
                .category(foodBrand.getFood().getCategory())
                .calories(foodBrand.getCalories())
                .guideMessage(calorieGuideDto.message())
                .walkingMinutes(calorieGuideDto.walkingMinutes())
                .activityMinutes(calorieGuideDto.activityMinutes())
                .build();
    }
}
