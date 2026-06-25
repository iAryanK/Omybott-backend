package com.aryan.omybott.services;

import com.aryan.omybott.Entities.User;
import com.aryan.omybott.dto.request.LoginReqDTO;
import com.aryan.omybott.dto.request.SignupReqDTO;
import com.aryan.omybott.dto.response.LoginRespDTO;
import com.aryan.omybott.dto.response.SignupRespDTO;
import com.aryan.omybott.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public LoginRespDTO login(LoginReqDTO loginReqDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginReqDTO.email(), loginReqDTO.password())
        );

        User userEntity = (User) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(userEntity);
        String refreshToken = jwtService.generateRefreshToken(userEntity);

        return new LoginRespDTO(accessToken, refreshToken);
    }

    public SignupRespDTO signUp(SignupReqDTO signupReqDTO) {
        Optional<User> user = userRepository.findByEmail(signupReqDTO.email());
        if (user.isPresent()) {
            throw new BadCredentialsException("user with email "+signupReqDTO.email()+" already exists");
        }

        User toBeCreated = new User(signupReqDTO.name(), signupReqDTO.email(),
                                signupReqDTO.password(), false, true);
        toBeCreated.setPassword(passwordEncoder.encode(signupReqDTO.password()));

        User savedUser = userRepository.save(toBeCreated);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signupReqDTO.email(), signupReqDTO.password())
        );

        User userEntity = (User) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(userEntity);
        String refreshToken = jwtService.generateRefreshToken(userEntity);

        return new SignupRespDTO(savedUser.getId(), accessToken, refreshToken);
    }

    public LoginRespDTO refreshToken(String refreshToken) {
        UUID userId = jwtService.getUserIdFromToken(refreshToken);
        User userEntity = userService.getUserById(userId);

        String accessToken = jwtService.generateRefreshToken(userEntity);
        return new LoginRespDTO(accessToken, refreshToken);
    }

}
