package com.liuxy.campushub.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class RegisterResponse {
    private boolean success;
    private String message;
    private Long userId;
    private String username;
} 