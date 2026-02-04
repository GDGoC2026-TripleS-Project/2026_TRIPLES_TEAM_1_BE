package gdg.beforeonebite.app.admin.service;

import gdg.beforeonebite.app.admin.dto.brand.BrandAddDto;
import gdg.beforeonebite.app.admin.dto.brand.BrandAddResult;
import gdg.beforeonebite.app.admin.dto.food.FoodAddDto;
import gdg.beforeonebite.app.admin.dto.food.FoodAddResult;
import gdg.beforeonebite.app.admin.dto.foodbrand.FoodBrandAddDto;
import gdg.beforeonebite.app.admin.dto.foodbrand.FoodBrandAddResult;
import gdg.beforeonebite.app.admin.dto.foodbrand.FoodBrandKey;
import gdg.beforeonebite.app.admin.dto.foodbrand.IdBundle;
import gdg.beforeonebite.app.food.domain.Brand;
import gdg.beforeonebite.app.food.domain.Food;
import gdg.beforeonebite.app.food.domain.FoodBrand;
import gdg.beforeonebite.app.food.repository.BrandRepository;
import gdg.beforeonebite.app.food.repository.FoodBrandRepository;
import gdg.beforeonebite.app.food.repository.FoodRepository;
import gdg.beforeonebite.global.exception.ErrorMessage;
import gdg.beforeonebite.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final FoodRepository foodRepository;
    private final BrandRepository brandRepository;
    private final FoodBrandRepository foodBrandRepository;

    public FoodAddResult addFood(List<FoodAddDto> list) {
        if (list == null || list.isEmpty()) {
            return new FoodAddResult(0, 0);
        }

        Set<FoodAddDto> requestedKeys = extractFoodKeys(list);
        Set<FoodAddDto> existingKeys = loadExistingFoodKeys(requestedKeys);

        return saveFoods(list, existingKeys);
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

    public FoodBrandAddResult addFoodBrand(List<FoodBrandAddDto> list) {
        if (list == null || list.isEmpty()) {
            return new FoodBrandAddResult(0, 0);
        }

        IdBundle idBundle = extractIds(list);

        Map<Long, Food> foodMap = loadFoods(idBundle.foodIds());
        Map<Long, Brand> brandMap = loadBrands(idBundle.brandIds());

        validateFoodsExist(idBundle.foodIds(), foodMap);
        validateBrandsExist(idBundle.brandIds(), brandMap);

        Set<FoodBrandKey> existingKeys =
                loadExistingFoodBrandKeys(idBundle.foodIds(), idBundle.brandIds());

        return processFoodBrands(list, foodMap, brandMap, existingKeys);
    }

    private Set<FoodAddDto> extractFoodKeys(List<FoodAddDto> list) {
        return new HashSet<>(list);
    }

    private Set<FoodAddDto> loadExistingFoodKeys(Set<FoodAddDto> requestedKeys) {
        Set<String> foodNames = new HashSet<>();
        Set<String> categories = new HashSet<>();

        for (FoodAddDto key : requestedKeys) {
            foodNames.add(key.foodName());
            categories.add(key.category());
        }

        List<Food> existingFoods =
                foodRepository.findAllByFoodNameInAndCategoryIn(foodNames, categories);

        Set<FoodAddDto> existingKeys = new HashSet<>();
        for (Food food : existingFoods) {
            existingKeys.add(
                    new FoodAddDto(food.getFoodName(), food.getCategory())
            );
        }

        return existingKeys;
    }

    private FoodAddResult saveFoods(
            List<FoodAddDto> list,
            Set<FoodAddDto> existingKeys
    ) {
        Set<FoodAddDto> seenKeys = new HashSet<>();
        List<Food> toSave = new ArrayList<>();

        int saved = 0;
        int skipped = 0;

        for (FoodAddDto dto : list) {

            // 입력 중복 OR DB 중복
            if (!seenKeys.add(dto) || existingKeys.contains(dto)) {
                skipped++;
                continue;
            }

            toSave.add(
                    Food.builder()
                            .foodName(dto.foodName())
                            .category(dto.category())
                            .build()
            );
            saved++;
        }

        foodRepository.saveAll(toSave);
        return new FoodAddResult(saved, skipped);
    }

    private IdBundle extractIds(List<FoodBrandAddDto> list) {
        Set<Long> foodIds = new HashSet<>();
        Set<Long> brandIds = new HashSet<>();

        for (FoodBrandAddDto dto : list) {
            foodIds.add(dto.foodId());
            brandIds.add(dto.brandId());
        }

        return new IdBundle(foodIds, brandIds);
    }

    private Map<Long, Food> loadFoods(Set<Long> foodIds) {
        List<Food> foods = foodRepository.findAllById(foodIds);

        Map<Long, Food> foodMap = new HashMap<>();
        for (Food food : foods) {
            foodMap.put(food.getId(), food);
        }
        return foodMap;
    }

    private Map<Long, Brand> loadBrands(Set<Long> brandIds) {
        List<Brand> brands = brandRepository.findAllById(brandIds);

        Map<Long, Brand> brandMap = new HashMap<>();
        for (Brand brand : brands) {
            brandMap.put(brand.getId(), brand);
        }
        return brandMap;
    }

    private void validateFoodsExist(Set<Long> requestedIds, Map<Long, Food> loadedFoods) {
        if (requestedIds.size() != loadedFoods.size()) {
            throw new NotFoundException(ErrorMessage.FOOD_NOT_EXIST);
        }
    }

    private void validateBrandsExist(Set<Long> requestedIds, Map<Long, Brand> loadedBrands) {
        if (requestedIds.size() != loadedBrands.size()) {
            throw new NotFoundException(ErrorMessage.BRAND_NOT_EXIST);
        }
    }

    private Set<FoodBrandKey> loadExistingFoodBrandKeys(
            Set<Long> foodIds,
            Set<Long> brandIds
    ) {
        List<FoodBrand> existingFoodBrands =
                foodBrandRepository.findAllByFood_IdInAndBrand_IdIn(foodIds, brandIds);

        Set<FoodBrandKey> keys = new HashSet<>();
        for (FoodBrand foodBrand : existingFoodBrands) {
            keys.add(new FoodBrandKey(
                    foodBrand.getFood().getId(),
                    foodBrand.getBrand().getId()
            ));
        }
        return keys;
    }

    private FoodBrandAddResult processFoodBrands(
            List<FoodBrandAddDto> list,
            Map<Long, Food> foodMap,
            Map<Long, Brand> brandMap,
            Set<FoodBrandKey> existingKeys
    ) {
        Set<FoodBrandKey> seenKeys = new HashSet<>();
        List<FoodBrand> toSave = new ArrayList<>();

        int saved = 0;
        int skipped = 0;

        for (FoodBrandAddDto dto : list) {
            FoodBrandKey key = new FoodBrandKey(dto.foodId(), dto.brandId());

            if (!seenKeys.add(key) || existingKeys.contains(key)) {
                skipped++;
                continue;
            }

            FoodBrand foodBrand = createFoodBrand(dto, foodMap, brandMap);
            toSave.add(foodBrand);
            saved++;
        }

        foodBrandRepository.saveAll(toSave);
        return new FoodBrandAddResult(saved, skipped);
    }

    private FoodBrand createFoodBrand(
            FoodBrandAddDto dto,
            Map<Long, Food> foodMap,
            Map<Long, Brand> brandMap
    ) {
        return FoodBrand.builder()
                .food(foodMap.get(dto.foodId()))
                .brand(brandMap.get(dto.brandId()))
                .calories(dto.calories())
                .build();
    }


}
