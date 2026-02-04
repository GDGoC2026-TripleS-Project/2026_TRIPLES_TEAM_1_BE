package gdg.beforeonebite.app.admin.dto.foodbrand;

public record FoodBrandAddDto(
        long foodId,
        long brandId,
        double calories
) {
}
