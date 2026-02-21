package gdg.beforeonebite.app.food.controller;

import gdg.beforeonebite.app.food.dto.FoodBestCompareResponse;
import gdg.beforeonebite.app.food.dto.FoodRecommendationListResponse;
import gdg.beforeonebite.app.food.dto.FoodSearchResponse;
import gdg.beforeonebite.app.food.service.FoodSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/food")
@Tag(
        name = "음식 검색 API",
        description = "음식 검색 시에 필요한 API"
)
public class FoodController {

    private final FoodSearchService foodSearchService;

    @GetMapping("/search")
    @Operation(
            summary = "음식 검색",
            description =
                    """
                    음식명을 입력하면 해당 음식의 칼로리 정보, 활동시간, 걷기시간 및 문구를 반환합니다.
                    
                    띄어쓰기는 자동으로 제거되어 검색됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "400", description = "검색어가 유효하지 않음"),
            @ApiResponse(responseCode = "404", description = "해당 음식이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러, 관리자에게 문의")
    })
    public ResponseEntity<FoodSearchResponse> searchFood(
            @Parameter(description = "검색할 음식명", example = "치즈버거")
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(foodSearchService.search(keyword));
    }

    @GetMapping("/recommend/best")
    @Operation(
            summary = "덜 부담되는 선택 보기",
            description =
                    """
                    검색한 음식과, 덜 부담되는 선택 보기 시에 보여줄 음식의 정보를 반환합니다.
                    
                    /food/search 시에 반환된 foodBrandId를 Parameter로 받습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "404", description = "foodBrandId에 해당하는 음식이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러, 관리자에게 문의")
    })
    public ResponseEntity<FoodBestCompareResponse> bestCompare(@RequestParam Long foodBrandId) {
        return ResponseEntity.ok(foodSearchService.getBestCompare(foodBrandId));
    }

    @GetMapping("/recommend/list")
    @Operation(
            summary = "비슷한 선택을 더 볼 수 있어요",
            description =
                    """
                    덜 부담되는 선택 보기 시에 반환된 값과, 추가적인 비슷한 선택 값을 다합쳐 최대 5개 반환합니다.
                    
                    /food/search 시에 반환된 foodBrandId를 Parameter로 받습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "404", description = "foodBrandId에 해당하는 음식이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러, 관리자에게 문의")
    })
    public ResponseEntity<FoodRecommendationListResponse> recommendationList(@RequestParam Long foodBrandId) {
        return ResponseEntity.ok(foodSearchService.getRecommendationList(foodBrandId));
    }
}
