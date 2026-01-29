package gdg.beforeonebite.app.food.repository;

import gdg.beforeonebite.app.food.domain.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {
    Optional<Food> findByFoodName(String foodName);
}
