package com.aryan.omybott.handlers;

import com.aryan.omybott.entities.User;
import com.aryan.omybott.repositories.UserRepository;
import com.aryan.omybott.services.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Value("${deploy.env:development}")
    private String deployEnv;

    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) token.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        User userEntity = userRepository.findByEmail(email).orElse(null);

        if (userEntity == null) {
            User newUser = User.builder()
                    .name(oAuth2User.getAttribute("name"))
                    .email(email)
                    .build();

            userEntity = userRepository.save(newUser);
        }

        String accessToken = jwtService.generateAccessToken(userEntity);
        String refreshToken = jwtService.generateRefreshToken(userEntity);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure("production".equals(deployEnv));
        response.addCookie(cookie);

        String baseUrl = frontendUrl.split(",")[0].trim();
        String frontEndUrl = baseUrl + "/auth/oauth-callback?token=" + accessToken + "&refreshToken=" + refreshToken;

        getRedirectStrategy().sendRedirect(request, response, frontEndUrl);
    }
}
