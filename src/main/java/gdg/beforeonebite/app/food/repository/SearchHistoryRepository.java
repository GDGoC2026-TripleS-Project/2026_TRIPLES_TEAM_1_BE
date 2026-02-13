package gdg.beforeonebite.app.food.repository;

import gdg.beforeonebite.app.food.domain.SearchHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    Optional<SearchHistory> findByUserIdAndFoodId(Long userId, Long foodId);

    @Query("""
        select sh.food.foodName
        from SearchHistory sh
        where sh.user.id = :userId
        order by sh.searchedAt desc
    """)
    List<String> findTop15FoodNamesByUserId(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Modifying
    @Query("""
        delete from SearchHistory sh
        where sh.searchedAt < :fiveDaysAgo
    """)
    int deleteOldSearchHistory(@Param("fiveDaysAgo") LocalDateTime fiveDaysAgo);
}
