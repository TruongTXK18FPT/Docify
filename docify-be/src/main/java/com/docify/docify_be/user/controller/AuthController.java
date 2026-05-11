package com.docify.docify_be.user.controller;

import com.docify.docify_be.common.api.ApiResponse;
import com.docify.docify_be.common.exception.DocifyException;
import com.docify.docify_be.common.security.JwtTokenProvider;
import com.docify.docify_be.common.security.UserPrincipal;
import com.docify.docify_be.user.dto.AuthResponse;
import com.docify.docify_be.user.dto.LoginRequest;
import com.docify.docify_be.user.dto.RegisterRequest;
import com.docify.docify_be.user.entity.AuthProvider;
import com.docify.docify_be.user.entity.User;
import com.docify.docify_be.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DocifyException("USER_NOT_FOUND", "User not found"));

        return ApiResponse.success(AuthResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .user(AuthResponse.UserDto.builder()
                        .id(user.getId().toString())
                        .name(user.getName())
                        .email(user.getEmail())
                        .avatarUrl(user.getAvatarUrl())
                        .build())
                .build());
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new DocifyException("EMAIL_IN_USE", "Email address already in use.");
        }

        User user = User.builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .provider(AuthProvider.LOCAL)
                .build();

        User result = userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signUpRequest.getEmail(),
                        signUpRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return ApiResponse.success(AuthResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .user(AuthResponse.UserDto.builder()
                        .id(result.getId().toString())
                        .name(result.getName())
                        .email(result.getEmail())
                        .avatarUrl(result.getAvatarUrl())
                        .build())
                .build());
    }
}