package com.liuxy.campushub.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 赏金订单实体类
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Data
public class BountyOrder {
    /**
     * 订单ID
     */
    private Integer orderId;

    /**
     * 帖子ID
     */
    private Integer postId;

    /**
     * 发布者ID
     */
    private Long publisherId;

    /**
     * 接单者ID
     */
    private Long accepterId;

    /**
     * 赏金金额
     */
    private BigDecimal amount;

    /**
     * 订单状态（0-待支付，1-已支付，2-已完成，3-已取消）
     */
    private Integer status;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 