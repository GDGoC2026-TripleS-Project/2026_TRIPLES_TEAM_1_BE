package gdg.beforeonebite.app.admin.service;

import gdg.beforeonebite.app.admin.dto.brand.BrandAddDto;
import gdg.beforeonebite.app.admin.dto.brand.BrandAddResult;
import gdg.beforeonebite.app.admin.dto.food.FoodAddDto;
import gdg.beforeonebite.app.admin.dto.food.FoodAddResult;
import gdg.beforeonebite.app.food.domain.Brand;
import gdg.beforeonebite.app.food.domain.Food;
import gdg.beforeonebite.app.food.repository.BrandRepository;
import gdg.beforeonebite.app.food.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final FoodRepository foodRepository;
    private final BrandRepository brandRepository;

    public FoodAddResult addFood(List<FoodAddDto> list) {
        int saved = 0;
        int skipped = 0;

        for (FoodAddDto dto : list) {
            if (foodRepository.existsByFoodNameAndCategory(dto.foodName(), dto.category())) {
                skipped++;
                continue;
            }

            foodRepository.save(
                    Food.builder()
                            .foodName(dto.foodName())
                            .category(dto.category())
                            .build()
            );
            saved++;
        }

        return new FoodAddResult(saved, skipped);
    }

    public BrandAddResult addBrand(List<BrandAddDto> list) {
        int saved = 0;
        int skipped = 0;

        for (BrandAddDto dto : list) {
            if (brandRepository.existsByBrandName(dto.brandName())) {
                skipped++;
                continue;
            }

            brandRepository.save(
                    Brand.builder()
                            .brandName(dto.brandName())
                            .build()
            );
            saved++;
        }

        return new BrandAddResult(saved, skipped);
    }
}
