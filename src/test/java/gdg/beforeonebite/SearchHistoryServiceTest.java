package gdg.beforeonebite;

import gdg.beforeonebite.app.auth.domain.AuthUser;
import gdg.beforeonebite.app.food.domain.Food;
import gdg.beforeonebite.app.food.domain.SearchHistory;
import gdg.beforeonebite.app.food.repository.SearchHistoryRepository;
import gdg.beforeonebite.app.auth.repository.UserRepository;
import gdg.beforeonebite.app.food.service.SearchHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;

class SearchHistoryServiceTest {

    @Mock
    private SearchHistoryRepository searchHistoryRepository;

    @Mock
    private UserRepository userRepository;

    private SearchHistoryService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new SearchHistoryService(searchHistoryRepository, userRepository);
    }

    @Test
    void 기존_기록이_있으면_update() {
        AuthUser authUser = new AuthUser(1L, null);
        Food food = Food.builder().id(1L).build();

        SearchHistory history = mock(SearchHistory.class);

        when(searchHistoryRepository.findByUserIdAndFoodId(1L, 1L))
                .thenReturn(Optional.of(history));

        service.saveSearchHistory(authUser, food);

        verify(history, times(1)).updateSearchedAt(any());
        verify(searchHistoryRepository, never()).save(any());
    }
}
