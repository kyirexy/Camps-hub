package com.liuxy.campushub.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类实体类
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Data
public class Category {
    /**
     * 分类ID
     */
    private Integer categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 父分类ID（用于多级分类）
     */
    private Integer parentId;

    /**
     * 排序权重
     */
    private Integer sortOrder;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 分类类型
     */
    private String categoryType;
    
    /**
     * 子分类列表
     */
    private List<Category> children;
}