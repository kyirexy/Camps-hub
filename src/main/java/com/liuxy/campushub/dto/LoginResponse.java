package com.liuxy.campushub.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private boolean success;
    private String message;
    private Long userId;
    private String username;
    private Integer userRole;
    private String token;

    public static LoginResponse success(Long userId, String username, Integer userRole, String token) {
        return LoginResponse.builder()
                .success(true)
                .message("登录成功")
                .userId(userId)
                .username(username)
                .userRole(userRole)
                .token(token)
                .build();
    }

    public static LoginResponse fail(String message) {
        return LoginResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}