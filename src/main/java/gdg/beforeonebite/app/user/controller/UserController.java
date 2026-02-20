package gdg.beforeonebite.app.user.controller;

import gdg.beforeonebite.app.auth.domain.AuthUser;
import gdg.beforeonebite.app.user.dto.SearchListDto;
import gdg.beforeonebite.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/history/my")
    public ResponseEntity<SearchListDto> getMyHistory(@AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(userService.getMyHistory(authUser));
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<SearchListDto> getHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getSearchHistory(userId));
    }
}
