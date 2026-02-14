package gdg.beforeonebite.app.food.controller;

import gdg.beforeonebite.app.auth.domain.AuthUser;
import gdg.beforeonebite.app.food.service.SearchHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search/history")
@Tag(
        name = "검색기록 반환 API",
        description = "로그인 사용자의 검색 기록 반환 API"
)
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;

    @GetMapping("/recent")
    @Operation(
            summary = "최근 기록 반환",
            description =
                    """
                    토큰으로 식별된 사용자의 최근 검색 기록을 반환합니다.
                    
                    값은 최대 15개를 반환합니다.
                    
                    5일이 지난 검색기록은 자동 삭제됩니다.
                    
                    토큰이 없다면, 빈 리스트가 반환됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색기록 반환 성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러, 관리자에게 문의")
    })
    public List<String> getRecentSearchHistory(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return searchHistoryService.getRecentSearchFoodNames(authUser);
    }
}
