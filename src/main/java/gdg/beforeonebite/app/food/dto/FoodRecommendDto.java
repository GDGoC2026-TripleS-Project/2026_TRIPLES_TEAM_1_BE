package gdg.beforeonebite.app.food.dto;


import gdg.beforeonebite.app.food.domain.FoodBrand;
import lombok.Builder;

@Builder
public record FoodRecommendDto(
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
    public static FoodRecommendDto from(FoodBrand fb, CalorieGuideDto guide) {
        return FoodRecommendDto.builder()
                .foodBrandId(fb.getId())
                .foodId(fb.getFood().getId())
                .foodName(fb.getFood().getFoodName())
                .brand(fb.getBrand() == null ? null : fb.getBrand().getBrandName())
                .category(fb.getFood().getCategory())
                .calories(fb.getCalories())
                .guideMessage(guide.message())
                .walkingMinutes(guide.walkingMinutes())
                .activityMinutes(guide.activityMinutes())
                .build();
    }
}
