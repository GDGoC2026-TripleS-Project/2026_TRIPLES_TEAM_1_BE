package gdg.beforeonebite.app.user.service;

import gdg.beforeonebite.app.auth.domain.AuthUser;
import gdg.beforeonebite.app.food.repository.SearchHistoryRepository;
import gdg.beforeonebite.app.user.dto.SearchListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final SearchHistoryRepository searchHistoryRepository;

    public SearchListDto getMyHistory(AuthUser authUser) {
        Long userId = authUser.id();

        return getSearchHistoryDtoFromUserId(userId);
    }

    public SearchListDto getSearchHistory(Long userId) {
        return getSearchHistoryDtoFromUserId(userId);
    }

    private SearchListDto getSearchHistoryDtoFromUserId(Long userId) {
        return SearchListDto.builder()
                .searchList(
                        searchHistoryRepository.findByUserIdOrderBySearchedAtDesc(userId).stream()
                                .map(h -> h.getFood().getFoodName())
                                .toList())
                .build();
    }
}
