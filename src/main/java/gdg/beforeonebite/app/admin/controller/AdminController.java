package gdg.beforeonebite.app.admin.controller;

import gdg.beforeonebite.app.admin.dto.brand.BrandAddDto;
import gdg.beforeonebite.app.admin.dto.brand.BrandAddResult;
import gdg.beforeonebite.app.admin.dto.food.FoodAddDto;
import gdg.beforeonebite.app.admin.dto.food.FoodAddResult;
import gdg.beforeonebite.app.admin.service.AdminService;
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
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/food")
    public ResponseEntity<FoodAddResult> addFood(@RequestBody List<FoodAddDto> list) {
        return ResponseEntity.ok(adminService.addFood(list));
    }

    @PostMapping("/brand")
    public ResponseEntity<BrandAddResult> addBrand(@RequestBody List<BrandAddDto> list) {
        return ResponseEntity.ok(adminService.addBrand(list));
    }
}
