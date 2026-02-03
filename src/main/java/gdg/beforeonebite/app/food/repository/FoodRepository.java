package gdg.beforeonebite.app.food.repository;

import gdg.beforeonebite.app.food.domain.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {
    boolean existsByFoodNameAndCategory(String foodName, String category);
}
