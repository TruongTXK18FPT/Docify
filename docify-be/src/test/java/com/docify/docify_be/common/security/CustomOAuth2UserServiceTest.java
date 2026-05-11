package com.docify.docify_be.common.security;

import com.docify.docify_be.user.entity.AuthProvider;
import com.docify.docify_be.user.entity.User;
import com.docify.docify_be.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomOAuth2UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomOAuth2UserService customOAuth2UserService;

    private OAuth2UserRequest mockRequest;
    private OAuth2User mockOAuth2User;

    @BeforeEach
    void setUp() {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("github")
                .clientId("test-client-id")
                .clientSecret("test-client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .authorizationUri("https://github.com/login/oauth/authorize")
                .tokenUri("https://github.com/login/oauth/access_token")
                .userInfoUri("https://api.github.com/user")
                .userNameAttributeName("id")
                .clientName("GitHub")
                .build();

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "test-token",
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );

        mockRequest = new OAuth2UserRequest(clientRegistration, accessToken);
    }

    @Test
    void processOAuth2User_NewUser_ShouldSaveAndReturnUser() {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", "123456789"); // Should be string, otherwise String.valueOf might throw CastClass on some configurations or String.valueOf is what it needs but passing an int broke something
        attributes.put("sub", "123456789");
        attributes.put("email", "newuser@github.com");
        attributes.put("login", "newuser");
        attributes.put("avatar_url", "https://github.com/avatar.png");
        
        mockOAuth2User = new DefaultOAuth2User(Collections.emptyList(), attributes, "id");
        
        when(userRepository.findByEmail("newuser@github.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(java.util.UUID.randomUUID());
            return user;
        });

        // Act
        OAuth2User result = customOAuth2UserService.processOAuth2User(mockRequest, mockOAuth2User);

        // Assert
        assertNotNull(result);
        assertEquals("newuser@github.com", result.getAttribute("email"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void processOAuth2User_ExistingUser_ShouldUpdateAndReturnUser() {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", "123456789");
        attributes.put("sub", "123456789");
        attributes.put("email", "existing@github.com");
        attributes.put("login", "updated-name");
        attributes.put("avatar_url", "https://github.com/new-avatar.png");

        mockOAuth2User = new DefaultOAuth2User(Collections.emptyList(), attributes, "id");

        User existingUser = User.builder()
                .id(java.util.UUID.randomUUID())
                .email("existing@github.com")
                .name("old-name")
                .provider(AuthProvider.GITHUB)
                .providerId("123456789")
                .build();

        when(userRepository.findByEmail("existing@github.com")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        OAuth2User result = customOAuth2UserService.processOAuth2User(mockRequest, mockOAuth2User);

        // Assert
        assertNotNull(result);
        assertEquals("existing@github.com", result.getAttribute("email"));
        verify(userRepository, times(1)).save(existingUser);
    }
}
