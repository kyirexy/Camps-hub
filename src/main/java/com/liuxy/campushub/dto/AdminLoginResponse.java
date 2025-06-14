package com.liuxy.campushub.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminLoginResponse {
    private String token;
    private AdminInfoDTO admin;
} 