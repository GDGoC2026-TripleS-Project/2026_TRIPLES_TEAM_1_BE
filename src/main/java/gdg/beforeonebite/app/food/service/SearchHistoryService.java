package gdg.beforeonebite.app.food.service;

import gdg.beforeonebite.app.auth.domain.AuthUser;
import gdg.beforeonebite.app.auth.repository.UserRepository;
import gdg.beforeonebite.app.food.domain.Food;
import gdg.beforeonebite.app.food.domain.SearchHistory;
import gdg.beforeonebite.app.food.repository.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;

    public void saveSearchHistory(AuthUser authUser, Food food) {
        if (authUser == null) return;

        Long userId = authUser.id();
        Long foodId = food.getId();

        searchHistoryRepository.findByUserIdAndFoodId(userId, foodId)
                .ifPresentOrElse(
                        history -> history.updateSearchedAt(LocalDateTime.now()),
                        () -> {
                            SearchHistory history = SearchHistory.builder()
                                    .user(userRepository.getReferenceById(userId))
                                    .food(food)
                                    .searchedAt(LocalDateTime.now())
                                    .build();
                            searchHistoryRepository.save(history);
                        }
                );
    }

    @Transactional(readOnly = true)
    public List<String> getRecentSearchFoodNames(AuthUser authUser) {

        if (authUser == null) {
            return List.of();
        }

        return searchHistoryRepository.findTop15FoodNamesByUserId(
                authUser.id(),
                PageRequest.of(0, 15)
        );
    }

    public int deleteOldSearchHistory() {

        LocalDateTime fiveDaysAgo = LocalDateTime.now().minusDays(5);

        return searchHistoryRepository.deleteOldSearchHistory(fiveDaysAgo);
    }
}
