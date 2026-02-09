package gdg.beforeonebite.app.food.repository;

import gdg.beforeonebite.app.food.domain.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
}
