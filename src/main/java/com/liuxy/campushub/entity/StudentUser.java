package com.liuxy.campushub.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StudentUser {
    private Long userId;
    private String username;
    private String password;
    private String realName;
    private String studentNumber;
    private String gender;
    private String phone;
    private String email;
    private Integer collegeId;
    private String collegeName;
    private String major;
    private Integer grade;
    private Integer userRole;
    private LocalDateTime registerTime;
    private LocalDateTime lastLogin;
    private Integer avatarImageId;
    private String bio;
    private Integer status;
    private String jwPassword;
    private Integer creditScore;

    public void setAvatarImageId(Integer avatarImageId) {
        this.avatarImageId = avatarImageId;
    }
}