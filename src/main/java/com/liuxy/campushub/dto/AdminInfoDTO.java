package com.liuxy.campushub.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AdminInfoDTO {
    private Integer id;
    private String username;
    private Boolean isSuper;
    private LocalDateTime lastLogin;
    private String loginIp;
} 