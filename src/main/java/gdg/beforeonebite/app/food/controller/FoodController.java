package gdg.beforeonebite.app.food.controller;

import gdg.beforeonebite.app.food.dto.FoodBestCompareResponse;
import gdg.beforeonebite.app.food.dto.FoodRecommendationListResponse;
import gdg.beforeonebite.app.food.dto.FoodSearchResponse;
import gdg.beforeonebite.app.food.service.FoodSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/food")
public class FoodController {

    private final FoodSearchService foodSearchService;

    @GetMapping("/search")
    public ResponseEntity<FoodSearchResponse> searchFood(@RequestParam String keyword) {
        return ResponseEntity.ok(foodSearchService.search(keyword));
    }

    @GetMapping("/recommend/best")
    public ResponseEntity<FoodBestCompareResponse> bestCompare(@RequestParam Long foodBrandId) {
        return ResponseEntity.ok(foodSearchService.getBestCompare(foodBrandId));
    }

    @GetMapping("/recommend/list")
    public ResponseEntity<FoodRecommendationListResponse> recommendationList(@RequestParam Long foodBrandId) {
        return ResponseEntity.ok(foodSearchService.getRecommendationList(foodBrandId));
    }
}
