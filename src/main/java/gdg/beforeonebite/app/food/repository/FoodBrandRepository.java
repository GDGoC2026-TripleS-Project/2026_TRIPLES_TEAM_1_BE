package gdg.beforeonebite.app.food.repository;

import gdg.beforeonebite.app.food.domain.Brand;
import gdg.beforeonebite.app.food.domain.Food;
import gdg.beforeonebite.app.food.domain.FoodBrand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FoodBrandRepository extends JpaRepository<FoodBrand, Long> {
    Optional<FoodBrand> findByFood_FoodName(String foodName);

    boolean existsByFoodAndBrand(Food food, Brand brand);
}
