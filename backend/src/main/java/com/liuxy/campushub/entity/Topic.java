package com.liuxy.campushub.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 话题实体类（轻量设计：仅包含本表核心字段）
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "topic")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer topicId;

    @Column(nullable = false, length = 50, unique = true)
    private String topicName; // 话题名称（唯一）

    @Column(nullable = false)
    private Long creatorId; // 外键，关联student_user

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer usageCount = 0; // 使用次数，默认0

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 创建时间自动填充

    @Transient
    private String avatarUrl; // 创建者头像URL

    @Transient
    private String username; // 创建者用户名

    // 构造函数优化：必填字段初始化
    public Topic(String topicName, Long creatorId) {
        this.topicName = topicName;
        this.creatorId = creatorId;
    }
}