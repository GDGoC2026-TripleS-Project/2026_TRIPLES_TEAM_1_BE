package gdg.beforeonebite.app.food.controller;

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
}
