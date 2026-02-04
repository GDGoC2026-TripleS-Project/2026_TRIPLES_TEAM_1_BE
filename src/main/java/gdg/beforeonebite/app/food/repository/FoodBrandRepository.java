package gdg.beforeonebite.app.food.repository;

import gdg.beforeonebite.app.food.domain.FoodBrand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FoodBrandRepository extends JpaRepository<FoodBrand, Long> {
    Optional<FoodBrand> findByFood_FoodName(String foodName);

    List<FoodBrand> findAllByFood_IdInAndBrand_IdIn(Set<Long> foodIds, Set<Long> brandIds);
}
