package com.liuxy.campushub.service;

import com.liuxy.campushub.dto.ProductRequest;
import com.liuxy.campushub.dto.ProductResponse;
import com.liuxy.campushub.dto.ProductListResponse;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品服务接口
 *
 * @author liuxy
 * @since 2024-04-07
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
     * @param searchType 搜索类型：all-全部，title-标题，description-描述
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
            String searchType,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String status,
            String sortField,
            String sortOrder,
            Integer pageNum,
            Integer pageSize);

    /**
     * 上传商品图片
     *
     * @param files 图片文件列表
     * @param userId 用户ID
     * @return 图片ID列表
     */
    List<Long> uploadProductImages(List<MultipartFile> files, Long userId);

    /**
     * 删除商品图片
     *
     * @param imageId 图片ID
     * @param userId 用户ID
     */
    void deleteProductImage(Long imageId, Long userId);

    /**
     * 获取当前用户发布的商品列表
     *
     * @param userId 用户ID
     * @param status 商品状态
     * @param sortField 排序字段
     * @param sortOrder 排序方式
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @return 商品列表响应
     */
    ProductListResponse getMyProducts(
            Long userId,
            String status,
            String sortField,
            String sortOrder,
            Integer pageNum,
            Integer pageSize);
} 