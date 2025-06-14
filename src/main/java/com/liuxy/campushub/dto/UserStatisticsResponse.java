package com.liuxy.campushub.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户统计信息响应DTO
 *
 * @author liuxy
 * @date 2024-03-25
 */
@Data
@Builder
public class UserStatisticsResponse {
    /**
     * 总用户数
     */
    private Integer totalUsers;
    
    /**
     * 今日新增用户数
     */
    private Integer todayNewUsers;
    
    /**
     * 本月活跃用户数（有登录记录）
     */
    private Integer monthlyActiveUsers;
    
    /**
     * 正常状态用户数
     */
    private Integer normalUsers;
    
    /**
     * 禁用状态用户数
     */
    private Integer disabledUsers;
    
    /**
     * 未激活用户数
     */
    private Integer inactiveUsers;
    
    /**
     * 统计时间
     */
    private LocalDateTime statisticsTime;
} 