package com.liuxy.campushub.controller;

import com.liuxy.campushub.dto.ProductRequest;
import com.liuxy.campushub.dto.ProductResponse;
import com.liuxy.campushub.dto.ProductListResponse;
import com.liuxy.campushub.service.ProductService;
import com.liuxy.campushub.service.ImageService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品控制器*
 * 接口文档：*
 * 1. 发布商品
 * POST /api/v1/products
 * 请求头：
 * Authorization: Bearer {token}
 * 请求体：
 * {
 *   "categoryId": number,      // 分类ID
 *   "title": "string",        // 商品标题，2-100个字符
 *   "description": "string",  // 详细描述，10-2000个字符
 *   "priceType": "string",    // 报价类型：面议、区间报价、固定价
 *   "minPrice": number,       // 最低价（区间报价时生效）
 *   "maxPrice": number,       // 最高价（区间报价时生效）
 *   "expectPrice": number,    // 期望价（固定价时存储）
 *   "coverImages": ["string"], // 封面图数组，1-9张
 *   "contactWechat": "string", // 卖家微信ID（可选）
 *   "isContactVisible": boolean // 是否公开联系方式
 * }
 * 响应：
 * {
 *   "productId": number       // 商品ID
 * }
 * 2. 获取商品详情
 * GET /api/v1/products/{productId}
 * 请求头：
 * Authorization: Bearer {token}
 * 响应：
 * {
 *   "productId": number,      // 商品ID
 *   "sellerId": number,       // 发布者ID
 *   "sellerUsername": "string", // 发布者用户名
 *   "categoryId": number,     // 分类ID
 *   "categoryName": "string", // 分类名称
 *   "title": "string",        // 商品标题
 *   "description": "string",  // 详细描述
 *   "priceType": "string",    // 报价类型
 *   "minPrice": number,       // 最低价
 *   "maxPrice": number,       // 最高价
 *   "expectPrice": number,    // 期望价
 *   "coverImages": ["string"], // 封面图数组
 *   "contactWechat": "string", // 卖家微信ID
 *   "isContactVisible": boolean, // 是否公开联系方式
 *   "viewCount": number,      // 浏览数
 *   "status": "string",       // 商品状态
 *   "createTime": "string",   // 创建时间
 *   "updateTime": "string"    // 更新时间
 * }
 ** 3. 更新商品信息
 * PUT /api/v1/products/{productId}
 * 请求头：
 * Authorization: Bearer {token}
 * 请求体：同发布商品
 * 响应：
 * 204 No Content*
 * 4. 下架商品
 * PUT /api/v1/products/{productId}/take-down
 * 请求头：
 * Authorization: Bearer {token}
 * 响应：
 * 204 No Content*
 * 5. 标记商品为已达成
 * PUT /api/v1/products/{productId}/complete
 * 请求头：
 * Authorization: Bearer {token}
 * 响应：
 * 204 No Content
 *
 * @author liuxy
 *@date 2024-03-25
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private ImageService imageService;

    /**
     * 发布商品
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> publishProduct(
            @RequestParam("userId") Long userId,
            @Valid @RequestBody ProductRequest request) {
        logger.info("收到发布商品请求，发布者ID：{}", userId);
        try {
            Long productId = productService.publishProduct(userId, request);
            return ResponseEntity.ok(productId);
        } catch (Exception e) {
            logger.error("发布商品失败，发布者ID：" + userId, e);
            throw e;
        }
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductResponse> getProductDetail(@PathVariable Long productId) {
        logger.info("获取商品详情，商品ID：{}", productId);
        try {
            ProductResponse response = productService.getProductDetail(productId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取商品详情失败，商品ID：" + productId, e);
            throw e;
        }
    }

    /**
     * 获取当前用户发布的商品列表
     */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductListResponse> getMyProducts(
            @RequestAttribute("userId") Long userId,
            @RequestParam(required = false, defaultValue = "出售中") String status,
            @RequestParam(required = false, defaultValue = "createTime") String sortField,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        logger.info("获取用户发布的商品列表，用户ID：{}，状态：{}，排序：{} {}，页码：{}，每页数量：{}", 
            userId, status, sortField, sortOrder, pageNum, pageSize);
        try {
            ProductListResponse response = productService.getMyProducts(
                userId, status, sortField, sortOrder, pageNum, pageSize);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取用户发布的商品列表失败，用户ID：" + userId, e);
            throw e;
        }
    }

    /**
     * 更新商品信息
     */
    @PutMapping("/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateProduct(
            @PathVariable Long productId,
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody ProductRequest request) {
        logger.info("更新商品信息，商品ID：{}，发布者ID：{}", productId, userId);
        try {
            productService.updateProduct(productId, userId, request);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("更新商品信息失败，商品ID：" + productId, e);
            throw e;
        }
    }

    /**
     * 下架商品
     */
    @PutMapping("/{productId}/take-down")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> takeDownProduct(
            @PathVariable Long productId,
            @RequestAttribute("userId") Long userId) {
        logger.info("下架商品，商品ID：{}，发布者ID：{}", productId, userId);
        try {
            productService.takeDownProduct(productId, userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("下架商品失败，商品ID：" + productId, e);
            throw e;
        }
    }

    /**
     * 标记商品为已达成
     */
    @PutMapping("/{productId}/complete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markProductAsCompleted(
            @PathVariable Long productId,
            @RequestAttribute("userId") Long userId) {
        logger.info("标记商品为已达成，商品ID：{}，发布者ID：{}", productId, userId);
        try {
            productService.markProductAsCompleted(productId, userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("标记商品状态失败，商品ID：" + productId, e);
            throw e;
        }
    }

    /**
     * 获取商品列表
     */
    @GetMapping
    public ResponseEntity<ProductListResponse> getProductList(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String searchType, // 搜索类型：all-全部，title-标题，description-描述
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false, defaultValue = "出售中") String status,
            @RequestParam(required = false, defaultValue = "createTime") String sortField,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        logger.info("获取商品列表，分类：{}，关键词：{}，搜索类型：{}，价格区间：{}-{}，状态：{}，排序：{} {}，页码：{}，每页数量：{}", 
            categoryId, keyword, searchType, minPrice, maxPrice, status, sortField, sortOrder, pageNum, pageSize);
        try {
            ProductListResponse response = productService.getProductList(
                categoryId, keyword, searchType, minPrice, maxPrice, status,
                sortField, sortOrder, pageNum, pageSize);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取商品列表失败", e);
            throw e;
        }
    }

    /**
     * 上传商品图片
     */
    @PostMapping("/images")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Long>> uploadProductImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestAttribute("userId") Long userId) {
        logger.info("上传商品图片，上传者ID：{}", userId);
        try {
            List<Long> imageIds = productService.uploadProductImages(files, userId);
            return ResponseEntity.ok(imageIds);
        } catch (Exception e) {
            logger.error("上传商品图片失败，上传者ID：" + userId, e);
            throw e;
        }
    }

    /**
     * 删除商品图片
     */
    @DeleteMapping("/images/{imageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteProductImage(
            @PathVariable Long imageId,
            @RequestAttribute("userId") Long userId) {
        logger.info("删除商品图片，图片ID：{}，用户ID：{}", imageId, userId);
        try {
            productService.deleteProductImage(imageId, userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("删除商品图片失败，图片ID：" + imageId, e);
            throw e;
        }
    }
} 