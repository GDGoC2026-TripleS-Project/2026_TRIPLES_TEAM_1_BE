package gdg.beforeonebite.app.food.controller;

import gdg.beforeonebite.app.auth.domain.AuthUser;
import gdg.beforeonebite.app.food.service.SearchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search/history")
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;

    @GetMapping("/recent")
    public List<String> getRecentSearchHistory(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return searchHistoryService.getRecentSearchFoodNames(authUser);
    }
}
