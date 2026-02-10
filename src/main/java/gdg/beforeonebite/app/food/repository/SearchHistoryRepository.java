package gdg.beforeonebite.app.food.repository;

import gdg.beforeonebite.app.food.domain.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    Optional<SearchHistory> findByUserIdAndFoodId(Long userId, Long foodId);
}
