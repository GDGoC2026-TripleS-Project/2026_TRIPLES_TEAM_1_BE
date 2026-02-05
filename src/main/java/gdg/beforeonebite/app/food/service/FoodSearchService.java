package gdg.beforeonebite.app.food.service;

import gdg.beforeonebite.app.food.domain.FoodBrand;
import gdg.beforeonebite.app.food.dto.CalorieGuideDto;
import gdg.beforeonebite.app.food.dto.FoodRecommendDto;
import gdg.beforeonebite.app.food.dto.FoodSearchResponse;
import gdg.beforeonebite.app.food.dto.FoodSearchWithRecommendationsResponse;
import gdg.beforeonebite.app.food.repository.FoodBrandRepository;
import gdg.beforeonebite.global.exception.BadRequestException;
import gdg.beforeonebite.global.exception.ErrorMessage;
import gdg.beforeonebite.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodSearchService {

    private static final int RECOMMEND_FETCH_SIZE = 5;

    private final FoodBrandRepository foodBrandRepository;
    private final CalorieGuidePolicy calorieGuidePolicy;

    public FoodSearchResponse search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new BadRequestException(ErrorMessage.INVALID_SEARCH_KEYWORD);
        }

        String normalized = normalize(keyword);

        FoodBrand foodBrand = foodBrandRepository
                .findByFood_FoodName(normalized)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.FOOD_NOT_EXIST));

        CalorieGuideDto guideDto =
                calorieGuidePolicy.from(foodBrand.getCalories());

        return FoodSearchResponse.from(foodBrand, guideDto);
    }

    private String normalize(String keyword) {
        return keyword.replaceAll("\\s+", "");
    }

    // 추천 음식 코드

    public FoodSearchWithRecommendationsResponse searchWithRecommendations(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new BadRequestException(ErrorMessage.INVALID_SEARCH_KEYWORD);
        }

        String normalized = normalize(keyword);

        FoodBrand current = foodBrandRepository
                .findOneWithFoodAndBrandByFoodName(normalized)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.FOOD_NOT_EXIST));

        FoodRecommendDto currentFood = recommendFood(current);

        List<FoodBrand> candidateFood = findCandidates(current);

        if (candidateFood.isEmpty()) {
            return FoodSearchWithRecommendationsResponse.builder()
                    .current(currentFood)
                    .bestRecommendation(null)
                    .otherRecommendations(Collections.emptyList())
                    .build();
        }

        FoodBrand best = candidateFood.getFirst();
        List<FoodBrand> others = candidateFood.size() > 1
                ? candidateFood.subList(1, Math.min(candidateFood.size(), 5))
                : Collections.emptyList();

        FoodRecommendDto bestFood = recommendFood(best);

        List<FoodRecommendDto> otherCards = new ArrayList<>();
        for (FoodBrand fb : others) {
            otherCards.add(recommendFood(fb));
        }

        return FoodSearchWithRecommendationsResponse.builder()
                .current(currentFood)
                .bestRecommendation(bestFood)
                .otherRecommendations(otherCards)
                .build();
    }

    private List<FoodBrand> findCandidates(FoodBrand current) {
        String category = current.getFood().getCategory();
        double calories = current.getCalories();
        Long excludeId = current.getId();

        PageRequest pageRequest = PageRequest.of(0, RECOMMEND_FETCH_SIZE);

        if (current.getBrand() != null) {
            return foodBrandRepository.findLighterCandidatesWithBrand(
                    current.getBrand(), category, calories, excludeId, pageRequest
            );
        }

        return foodBrandRepository.findLighterCandidatesNoBrand(
                category, calories, excludeId, pageRequest
        );
    }

    private FoodRecommendDto recommendFood(FoodBrand foodBrand) {
        CalorieGuideDto guide = calorieGuidePolicy.from(foodBrand.getCalories());
        return FoodRecommendDto.from(foodBrand, guide);
    }
}
