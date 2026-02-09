package gdg.beforeonebite.app.food.service;

import gdg.beforeonebite.app.food.domain.FoodBrand;
import gdg.beforeonebite.app.food.dto.CalorieGuideDto;
import gdg.beforeonebite.app.food.dto.FoodBestCompareResponse;
import gdg.beforeonebite.app.food.dto.FoodRecommendDto;
import gdg.beforeonebite.app.food.dto.FoodRecommendationListResponse;
import gdg.beforeonebite.app.food.dto.FoodSearchResponse;
import gdg.beforeonebite.app.food.repository.FoodBrandRepository;
import gdg.beforeonebite.global.exception.BadRequestException;
import gdg.beforeonebite.global.exception.ErrorMessage;
import gdg.beforeonebite.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodSearchService {

    private static final int LIST_LIMIT = 5;

    private final FoodBrandRepository foodBrandRepository;
    private final CalorieGuidePolicy calorieGuidePolicy;

    public FoodSearchResponse search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new BadRequestException(ErrorMessage.INVALID_SEARCH_KEYWORD);
        }

        String normalized = normalize(keyword);

        FoodBrand foodBrand = foodBrandRepository
                .findOneWithFoodAndBrandByFoodName(normalized)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.FOOD_NOT_EXIST));

        CalorieGuideDto guideDto = calorieGuidePolicy.from(foodBrand.getCalories());
        return FoodSearchResponse.from(foodBrand, guideDto);
    }

    private String normalize(String keyword) {
        return keyword.replaceAll("\\s+", "");
    }

    // 추천 음식 코드

    public FoodBestCompareResponse getBestCompare(Long currentFoodBrandId) {
        FoodBrand current = foodBrandRepository.findOneWithFoodAndBrandById(currentFoodBrandId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.FOOD_NOT_EXIST));

        FoodRecommendDto currentDto = recommendFood(current);

        List<FoodBrand> candidates = findCandidates(current, 1);
        FoodRecommendDto best = candidates.isEmpty() ? null : recommendFood(candidates.getFirst());

        return FoodBestCompareResponse.builder()
                .current(currentDto)
                .bestRecommendation(best)
                .build();
    }

    public FoodRecommendationListResponse getRecommendationList(Long currentFoodBrandId) {
        FoodBrand current = foodBrandRepository.findOneWithFoodAndBrandById(currentFoodBrandId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.FOOD_NOT_EXIST));

        List<FoodBrand> candidates = findCandidates(current, LIST_LIMIT);

        List<FoodRecommendDto> list = new ArrayList<>();
        for (FoodBrand fb : candidates) {
            list.add(recommendFood(fb));
        }

        return FoodRecommendationListResponse.builder()
                .currentFoodBrandId(current.getId())
                .recommendations(list)
                .build();
    }

    private List<FoodBrand> findCandidates(FoodBrand current, int limit) {
        String category = current.getFood().getCategory();
        double calories = current.getCalories();
        Long excludeId = current.getId();

        PageRequest pageRequest = PageRequest.of(0, limit);

        if (current.getBrand() != null) {
            return foodBrandRepository.findLighterCandidatesWithBrand(current.getBrand(), category, calories, excludeId, pageRequest);
        }

        return foodBrandRepository.findLighterCandidatesNoBrand(category, calories, excludeId, pageRequest);
    }

    private FoodRecommendDto recommendFood(FoodBrand foodBrand) {
        CalorieGuideDto guide = calorieGuidePolicy.from(foodBrand.getCalories());
        return FoodRecommendDto.from(foodBrand, guide);
    }
}
