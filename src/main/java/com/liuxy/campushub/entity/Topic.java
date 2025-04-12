package com.liuxy.campushub.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 话题实体类
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Data
public class Topic {
    /**
     * 话题ID
     */
    private Integer topicId;

    /**
     * 话题名称（不含#）
     */
    private String topicName;

    /**
     * 创建者ID
     */
    private Long creatorId;

    /**
     * 使用次数
     */
    private Integer usageCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 