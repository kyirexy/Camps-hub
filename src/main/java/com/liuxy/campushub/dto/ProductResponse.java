package com.liuxy.campushub.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品响应DTO
 *
 * @author liuxy
 * @date 2024-03-25
 */
@Data
public class ProductResponse {
    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 发布者ID
     */
    private Long sellerId;

    /**
     * 发布者用户名
     */
    private String sellerUsername;

    /**
     * 分类ID
     */
    private Integer categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 详细描述
     */
    private String description;

    /**
     * 报价类型：面议、区间报价、固定价
     */
    private String priceType;

    /**
     * 最低价（区间报价时生效）
     */
    private BigDecimal minPrice;

    /**
     * 最高价（区间报价时生效）
     */
    private BigDecimal maxPrice;

    /**
     * 期望价（固定价时存储）
     */
    private BigDecimal expectPrice;

    /**
     * 封面图数组
     */
    private List<String> coverImages;

    /**
     * 卖家微信ID（加密存储）
     */
    private String contactWechat;

    /**
     * 是否公开联系方式
     */
    private Boolean isContactVisible;

    /**
     * 浏览数
     */
    private Integer viewCount;

    /**
     * 商品状态：出售中、已下架、已达成
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 