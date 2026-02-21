package gdg.beforeonebite;

import gdg.beforeonebite.app.food.domain.Food;
import gdg.beforeonebite.app.food.domain.FoodBrand;
import gdg.beforeonebite.app.food.dto.FoodSearchResponse;
import gdg.beforeonebite.app.food.repository.FoodBrandRepository;
import gdg.beforeonebite.app.food.service.CalorieGuidePolicy;
import gdg.beforeonebite.app.food.service.FoodSearchService;
import gdg.beforeonebite.app.food.service.SearchHistoryService;
import gdg.beforeonebite.global.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class FoodSearchServiceTest {

    @Mock
    private FoodBrandRepository foodBrandRepository;

    @Mock
    private SearchHistoryService searchHistoryService;

    private CalorieGuidePolicy calorieGuidePolicy = new CalorieGuidePolicy();

    private FoodSearchService foodSearchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        foodSearchService = new FoodSearchService(
                foodBrandRepository,
                calorieGuidePolicy,
                searchHistoryService
        );
    }

    @Test
    void keyword가_Null이면_예외() {
        assertThatThrownBy(() -> foodSearchService.search(null))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 정상_검색() {
        Food food = Food.builder()
                .id(1L)
                .foodName("콜라")
                .category("음료")
                .build();

        FoodBrand foodBrand = FoodBrand.builder()
                .id(1L)
                .food(food)
                .calories(300)
                .build();

        when(foodBrandRepository.findByFoodNameOrderByCaloriesAsc(
                anyString(),
                any(PageRequest.class)
        )).thenReturn(List.of(foodBrand));

        FoodSearchResponse result = foodSearchService.search("콜라");

        assertThat(result.foodName()).isEqualTo("콜라");
        assertThat(result.walkingMinutes()).isEqualTo(20);
    }
}
