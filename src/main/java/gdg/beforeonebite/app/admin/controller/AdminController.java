package gdg.beforeonebite.app.admin.controller;

import gdg.beforeonebite.app.admin.dto.FoodAddDto;
import gdg.beforeonebite.app.admin.dto.FoodAddResult;
import gdg.beforeonebite.app.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public ResponseEntity<FoodAddResult> addFood(@RequestBody List<FoodAddDto> list) {
        return ResponseEntity.ok(adminService.addFood(list));
    }
}
