package com.liuxy.campushub.service;

import com.liuxy.campushub.dto.ProductRequest;
import com.liuxy.campushub.dto.ProductResponse;
import com.liuxy.campushub.dto.ProductListResponse;
import java.math.BigDecimal;

/**
 * 商品服务接口
 *
 * @author liuxy
 * @date 2024-03-25
 */
public interface ProductService {

    /**
     * 发布商品
     *
     * @param sellerId 发布者ID
     * @param request 商品信息
     * @return 商品ID
     */
    Long publishProduct(Long sellerId, ProductRequest request);

    /**
     * 获取商品详情
     *
     * @param productId 商品ID
     * @return 商品信息
     */
    ProductResponse getProductDetail(Long productId);

    /**
     * 更新商品信息
     *
     * @param productId 商品ID
     * @param sellerId 发布者ID
     * @param request 商品信息
     */
    void updateProduct(Long productId, Long sellerId, ProductRequest request);

    /**
     * 下架商品
     *
     * @param productId 商品ID
     * @param sellerId 发布者ID
     */
    void takeDownProduct(Long productId, Long sellerId);

    /**
     * 标记商品为已达成
     *
     * @param productId 商品ID
     * @param sellerId 发布者ID
     */
    void markProductAsCompleted(Long productId, Long sellerId);

    /**
     * 增加商品浏览数
     *
     * @param productId 商品ID
     */
    void incrementViewCount(Long productId);

    /**
     * 获取商品列表
     *
     * @param categoryId 分类ID
     * @param keyword 关键词
     * @param minPrice 最低价
     * @param maxPrice 最高价
     * @param status 商品状态
     * @param sortField 排序字段
     * @param sortOrder 排序方式
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @return 商品列表响应
     */
    ProductListResponse getProductList(
            Integer categoryId,
            String keyword,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String status,
            String sortField,
            String sortOrder,
            Integer pageNum,
            Integer pageSize);
} 