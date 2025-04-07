package com.liuxy.campushub.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Admin {
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 登录账号
     */
    private String username;
    
    /**
     * BCrypt加密后的密码
     */
    private String passwordHash;
    
    /**
     * 是否超级管理员
     */
    private Boolean isSuper;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLogin;
    
    /**
     * 最后登录IP
     */
    private String loginIp;
    
    /**
     * 状态：0-禁用 1-正常
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 