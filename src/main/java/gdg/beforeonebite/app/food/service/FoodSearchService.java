package gdg.beforeonebite.app.food.service;

import gdg.beforeonebite.app.food.domain.Food;
import gdg.beforeonebite.app.food.dto.CalorieGuideDto;
import gdg.beforeonebite.app.food.dto.FoodSearchResponse;
import gdg.beforeonebite.app.food.repository.FoodRepository;
import gdg.beforeonebite.global.exception.BadRequestException;
import gdg.beforeonebite.global.exception.ErrorMessage;
import gdg.beforeonebite.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FoodSearchService {

    private final FoodRepository foodRepository;
    private final CalorieGuidePolicy calorieGuidePolicy;

    public FoodSearchResponse search(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            throw new BadRequestException(ErrorMessage.INVALID_SEARCH_KEYWORD);
        }

        String normalized = normalize(keyword);

        Food food = foodRepository.findByFoodName(normalized)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.FOOD_NOT_EXIST));

        CalorieGuideDto guideDto = calorieGuidePolicy.from(food.getCalories());

        return FoodSearchResponse.from(food, guideDto);
    }

    private String normalize(String keyword) {
        return keyword.replaceAll("\\s+", "");
    }
}
