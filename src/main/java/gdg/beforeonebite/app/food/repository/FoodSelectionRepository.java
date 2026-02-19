package gdg.beforeonebite.app.food.repository;

import gdg.beforeonebite.app.food.domain.FoodSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface FoodSelectionRepository extends JpaRepository<FoodSelection, Long> {

    @Query("""
                select fs from FoodSelection fs
                join fetch fs.foodBrand fb
                join fetch fb.food f
                left join fetch fb.brand b
                where fs.user.id = :userId
                  and fs.selectedDate >= :fromDate
                order by fs.selectedAt desc
            """)
    List<FoodSelection> findAllWithFoodBrandByUserAndDateFrom(Long userId, LocalDate fromDate);

    @Modifying
    @Query("""
                delete from FoodSelection fs
                where fs.selectedDate < :cutoffDate
            """)
    void deleteBySelectedDateBefore(@Param("cutoffDate") LocalDate cutoffDate);
}
