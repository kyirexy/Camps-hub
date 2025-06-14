package com.liuxy.campushub.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminLog {
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 操作管理员ID
     */
    private Integer adminId;
    
    /**
     * 操作类型
     */
    private String action;
    
    /**
     * 操作对象
     */
    private String target;
    
    /**
     * 操作IP
     */
    private String ip;
    
    /**
     * 操作详情
     */
    private String detail;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 