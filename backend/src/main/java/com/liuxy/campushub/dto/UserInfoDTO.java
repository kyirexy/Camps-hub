package com.liuxy.campushub.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoDTO {
    private Long userId;
    private String username;
    private String realName;
    private String studentNumber;
    private Integer collegeId;
    private String major;
    private Integer grade;
    private String phone;
    private String email;
    private String bio;
    private Integer userRole;
    private Integer status;
    private String avatarUrl;
}