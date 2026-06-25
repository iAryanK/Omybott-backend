package com.aryan.omybott.controllers;

import com.aryan.omybott.dto.request.LoginReqDTO;
import com.aryan.omybott.dto.request.SignupReqDTO;
import com.aryan.omybott.dto.response.LoginRespDTO;
import com.aryan.omybott.dto.response.SignupRespDTO;
import com.aryan.omybott.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${deploy.env}")
    private String deployEnv;

    @PostMapping("/signup")
    public ResponseEntity<SignupRespDTO> signup(@RequestBody SignupReqDTO signupReqDTO, HttpServletResponse response) {
        SignupRespDTO signupRespDTO = authService.signUp(signupReqDTO);

        Cookie cookie = new Cookie("refreshToken", signupRespDTO.accessToken());
        cookie.setHttpOnly(true);
        cookie.setSecure("production".equals(deployEnv));
        response.addCookie(cookie);

        return ResponseEntity.ok(signupRespDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginRespDTO> login(@RequestBody LoginReqDTO loginReqDTO, HttpServletResponse response) {
        LoginRespDTO loginRespDTO = authService.login(loginReqDTO);

        Cookie cookie = new Cookie("refreshToken", loginRespDTO.accessToken());
        cookie.setHttpOnly(true);
        cookie.setSecure("production".equals(deployEnv));
        response.addCookie(cookie);

        return ResponseEntity.ok(loginRespDTO);
    }

    @PostMapping(path = "/refresh")
    public ResponseEntity<LoginRespDTO> refresh(HttpServletRequest request) {
        String refreshToken = Arrays.stream(request.getCookies()).
                filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new AuthenticationServiceException("Refresh token not found inside the Cookies"));
        LoginRespDTO loginResponseDto = authService.refreshToken(refreshToken);

        return ResponseEntity.ok(loginResponseDto);
    }

}
