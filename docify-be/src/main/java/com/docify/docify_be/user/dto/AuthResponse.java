package com.docify.docify_be.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String tokenType;
    private UserDto user;

    @Data
    @Builder
    @AllArgsConstructor
    public static class UserDto {
        private String id;
        private String name;
        private String email;
        private String avatarUrl;
    }
}