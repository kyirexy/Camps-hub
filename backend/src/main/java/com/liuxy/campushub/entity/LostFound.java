package com.liuxy.campushub.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 失物招领实体类
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Data
public class LostFound {
    /**
     * 关联帖子ID
     */
    private Long postId;

    /**
     * 物品名称
     */
    private String itemName;

    /**
     * 物品特征描述
     */
    private String itemFeature;

    /**
     * 丢失/发现时间
     */
    private LocalDateTime lostTime;

    /**
     * 实际找回时间
     */
    private LocalDateTime foundTime;

    /**
     * 详细位置描述
     */
    private String locationDetail;

    /**
     * 酬谢方式: cash现金/gift物品/points积分
     */
    private String rewardType;
} 