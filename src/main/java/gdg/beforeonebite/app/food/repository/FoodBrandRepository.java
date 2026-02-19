package gdg.beforeonebite.app.food.repository;

import gdg.beforeonebite.app.food.domain.Brand;
import gdg.beforeonebite.app.food.domain.FoodBrand;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FoodBrandRepository extends JpaRepository<FoodBrand, Long> {
    Optional<FoodBrand> findByFood_FoodName(String foodName);

    List<FoodBrand> findAllByFood_IdInAndBrand_IdIn(Set<Long> foodIds, Set<Long> brandIds);

    @Query("""
                select fb from FoodBrand fb
                join fetch fb.food f
                left join fetch fb.brand b
                where replace(f.foodName, ' ', '') = :foodName
                order by fb.calories asc
            """)
    List<FoodBrand> findByFoodNameOrderByCaloriesAsc(String foodName, Pageable pageable);

    @Query("""
                select fb from FoodBrand fb
                join fetch fb.food f
                left join fetch fb.brand b
                where fb.brand = :brand
                  and f.category = :category
                  and fb.calories < :currentCalories
                  and fb.id <> :excludeFoodBrandId
                order by fb.calories asc
            """)
    List<FoodBrand> findLighterCandidatesWithBrand(
            Brand brand,
            String category,
            double currentCalories,
            Long excludeFoodBrandId,
            Pageable pageable
    );

    @Query("""
                select fb from FoodBrand fb
                join fetch fb.food f
                left join fetch fb.brand b
                where f.category = :category
                  and fb.calories < :currentCalories
                  and fb.id <> :excludeFoodBrandId
                order by fb.calories asc
            """)
    List<FoodBrand> findLighterCandidatesNoBrand(
            String category,
            double currentCalories,
            Long excludeFoodBrandId,
            Pageable pageable
    );

    @Query("""
                select fb from FoodBrand fb
                join fetch fb.food f
                left join fetch fb.brand b
                where fb.id = :id
            """)
    Optional<FoodBrand> findOneWithFoodAndBrandById(Long id);
}
