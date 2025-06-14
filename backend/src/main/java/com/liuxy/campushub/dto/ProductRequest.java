package com.liuxy.campushub.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品发布请求DTO
 *
 * @author liuxy
 * @date 2024-03-25
 */
@Data
public class ProductRequest {
    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    @Min(value = 1, message = "分类ID必须大于0")
    private Integer categoryId;

    /**
     * 商品标题
     */
    @NotBlank(message = "商品标题不能为空")
    @Size(min = 2, max = 100, message = "商品标题长度必须在2-100个字符之间")
    private String title;

    /**
     * 详细描述
     */
    //@NotBlank(message = "商品描述不能为空")
    @Size(min = 10, max = 2000, message = "商品描述长度必须在10-2000个字符之间")
    private String description;

    /**
     * 报价类型：面议、区间报价、固定价
     */
    @NotBlank(message = "报价类型不能为空")
    @Pattern(regexp = "^(面议|区间报价|固定价)$", message = "报价类型不正确")
    private String priceType;

    /**
     * 最低价（区间报价时生效）
     */
    @DecimalMin(value = "0.01", message = "最低价必须大于0")
    private BigDecimal minPrice;

    /**
     * 最高价（区间报价时生效）
     */
    @DecimalMin(value = "0.01", message = "最高价必须大于0")
    private BigDecimal maxPrice;

    /**
     * 期望价（固定价时存储）
     */
    @DecimalMin(value = "0.01", message = "期望价必须大于0")
    private BigDecimal expectPrice;

    /**
     * 封面图数组
     */
    //@NotEmpty(message = "封面图不能为空")
    @Size(max = 9, message = "封面图数量必须在0-9张之间")
    private List<String> coverImages;

    /**
     * 卖家微信ID
     */
    @Size(max = 50, message = "微信ID长度不能超过50个字符")
    private String contactWechat;

    /**
     * 是否公开联系方式
     */
    private Boolean isContactVisible;
} 