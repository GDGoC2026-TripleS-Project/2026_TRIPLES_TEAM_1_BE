package gdg.beforeonebite.app.food.repository;

import gdg.beforeonebite.app.food.domain.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface FoodRepository extends JpaRepository<Food, Long> {
    boolean existsByFoodNameAndCategory(String foodName, String category);

    List<Food> findAllByFoodNameInAndCategoryIn(Set<String> foodNames, Set<String> categories);
}
