package gdg.beforeonebite.app.admin.controller;

import gdg.beforeonebite.app.admin.dto.brand.BrandAddDto;
import gdg.beforeonebite.app.admin.dto.brand.BrandAddResult;
import gdg.beforeonebite.app.admin.dto.food.FoodAddDto;
import gdg.beforeonebite.app.admin.dto.food.FoodAddResult;
import gdg.beforeonebite.app.admin.dto.foodbrand.FoodBrandAddDto;
import gdg.beforeonebite.app.admin.dto.foodbrand.FoodBrandAddResult;
import gdg.beforeonebite.app.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Tag(
        name = "관리자용 API",
        description = "음식 데이터를 DB에 적재하는 API"
)
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/food")
    @Operation(
            summary = "음식 등록",
            description =
                    """
                    Food 리스트를 등록합니다.
                    
                    foodName + category 조합이 이미 존재하면 스킵합니다.
                    """
    )
    public ResponseEntity<FoodAddResult> addFood(@RequestBody List<FoodAddDto> list) {
        return ResponseEntity.ok(adminService.addFood(list));
    }

    @PostMapping("/brand")
    @Operation(
            summary = "음식 등록",
            description =
                    """
                    Brand 리스트를 등록합니다.
                    
                    이미 존재하는 브랜드는 스킵합니다.
                    """
    )
    public ResponseEntity<BrandAddResult> addBrand(@RequestBody List<BrandAddDto> list) {
        return ResponseEntity.ok(adminService.addBrand(list));
    }

    @PostMapping("/foodbrand")
    @Operation(
            summary = "음식-브랜드 매핑 등록",
            description =
                    """
                    FoodBrand 리스트를 등록합니다.
                    
                    foodId + brandId 조합이 이미 존재하면 스킵합니다.
                    """
    )
    public ResponseEntity<FoodBrandAddResult> addFoodBrand(@RequestBody List<FoodBrandAddDto> list) {
        return ResponseEntity.ok(adminService.addFoodBrand(list));
    }
}
