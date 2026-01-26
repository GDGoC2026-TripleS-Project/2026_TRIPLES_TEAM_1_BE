package gdg.beforeonebite.auth.controller;

import gdg.beforeonebite.auth.service.google.GoogleOAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class GoogleOAuthController {

    private final GoogleOAuthService googleOAuthService;

    @GetMapping("/auth/google")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        String googleUrl = googleOAuthService.buildGoogleAuthorizeUrl(response);
        response.sendRedirect(googleUrl);
    }

    @GetMapping("/oauth2/callback/google")
    public void callback(@RequestParam String code,
                         @RequestParam(required = false) String state,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        String redirectUrl = googleOAuthService.handleCallback(code, state, request, response);
        response.sendRedirect(redirectUrl);
    }
}
