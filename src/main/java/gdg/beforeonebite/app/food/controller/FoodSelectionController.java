package gdg.beforeonebite.app.food.controller;

import gdg.beforeonebite.app.auth.domain.AuthUser;
import gdg.beforeonebite.app.food.dto.FoodSelectionCreateRequest;
import gdg.beforeonebite.app.food.dto.FoodSelectionResponse;
import gdg.beforeonebite.app.food.service.FoodSelectionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/food/selection")
@Tag(name = "음식 선택 기록 API", description = "추천 화면에서 선택한 음식 기록 저장/조회/삭제")
public class FoodSelectionController {

    private final FoodSelectionService foodSelectionService;

    @PostMapping
    public ResponseEntity<Void> select(@AuthenticationPrincipal AuthUser authUser, @RequestBody FoodSelectionCreateRequest request) {
        foodSelectionService.selectFood(authUser, request.foodBrandId());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<FoodSelectionResponse>> list(@AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(foodSelectionService.getSelections(authUser));
    }

    @DeleteMapping("/{selectionId}")
    public ResponseEntity<Void> deleteToday(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long selectionId) {
        foodSelectionService.deleteTodaySelection(authUser, selectionId);
        return ResponseEntity.noContent().build();
    }
}
