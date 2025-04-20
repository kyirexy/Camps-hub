package com.liuxy.campushub.service.impl;

import com.liuxy.campushub.dto.ProductRequest;
import com.liuxy.campushub.dto.ProductResponse;
import com.liuxy.campushub.dto.ProductListResponse;
import com.liuxy.campushub.entity.Product;
import com.liuxy.campushub.mapper.ProductMapper;
import com.liuxy.campushub.service.ProductService;
import com.liuxy.campushub.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品服务实现类
 *
 * @author liuxy
 * @date 2024-03-25
 */
@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional
    public Long publishProduct(Long sellerId, ProductRequest request) {
        logger.info("发布商品，发布者ID：{}", sellerId);
        try {
            // 创建商品实体
            Product product = new Product();
            BeanUtils.copyProperties(request, product);
            product.setSellerId(sellerId);
            product.setStatus("出售中");
            product.setViewCount(0);
            // 使用SecurityUtil获取当前用户名
            product.setSellerUsername(SecurityUtil.getCurrentUsername());

            // 保存商品信息
            int result = productMapper.insert(product);
            if (result > 0) {
                logger.info("商品发布成功，商品ID：{}", product.getProductId());
                return product.getProductId();
            } else {
                throw new RuntimeException("商品发布失败");
            }
        } catch (Exception e) {
            logger.error("商品发布失败，发布者ID：" + sellerId, e);
            throw e;
        }
    }

    @Override
    public ProductResponse getProductDetail(Long productId) {
        logger.info("获取商品详情，商品ID：{}", productId);
        try {
            // 先增加浏览数
            productMapper.incrementViewCount(productId);
            
            // 获取商品详情
            Product product = productMapper.selectById(productId);
            if (product == null) {
                throw new RuntimeException("商品不存在");
            }

            // 转换为响应DTO
            ProductResponse response = new ProductResponse();
            BeanUtils.copyProperties(product, response);
            return response;
        } catch (Exception e) {
            logger.error("获取商品详情失败，商品ID：" + productId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void updateProduct(Long productId, Long sellerId, ProductRequest request) {
        logger.info("更新商品信息，商品ID：{}，发布者ID：{}", productId, sellerId);
        try {
            // 查询商品信息
            Product product = productMapper.selectById(productId);
            if (product == null) {
                throw new RuntimeException("商品不存在");
            }

            // 验证发布者权限
            if (!product.getSellerId().equals(sellerId)) {
                throw new RuntimeException("无权修改该商品");
            }

            // 保存原有的卖家用户名
            String originalSellerUsername = product.getSellerUsername();

            // 更新商品信息
            BeanUtils.copyProperties(request, product);
            product.setSellerUsername(originalSellerUsername);

            int result = productMapper.update(product);
            if (result <= 0) {
                throw new RuntimeException("商品更新失败");
            }

            logger.info("商品更新成功，商品ID：{}", productId);
        } catch (Exception e) {
            logger.error("商品更新失败，商品ID：" + productId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void takeDownProduct(Long productId, Long sellerId) {
        logger.info("下架商品，商品ID：{}，发布者ID：{}", productId, sellerId);
        try {
            // 查询商品信息
            Product product = productMapper.selectById(productId);
            if (product == null) {
                throw new RuntimeException("商品不存在");
            }

            // 验证发布者权限
            if (!product.getSellerId().equals(sellerId)) {
                throw new RuntimeException("无权下架该商品");
            }

            // 更新商品状态
            int result = productMapper.updateStatus(productId, "已下架");
            if (result <= 0) {
                throw new RuntimeException("商品下架失败");
            }

            logger.info("商品下架成功，商品ID：{}", productId);
        } catch (Exception e) {
            logger.error("商品下架失败，商品ID：" + productId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void markProductAsCompleted(Long productId, Long sellerId) {
        logger.info("标记商品为已达成，商品ID：{}，发布者ID：{}", productId, sellerId);
        try {
            // 查询商品信息
            Product product = productMapper.selectById(productId);
            if (product == null) {
                throw new RuntimeException("商品不存在");
            }

            // 验证发布者权限
            if (!product.getSellerId().equals(sellerId)) {
                throw new RuntimeException("无权标记该商品");
            }

            // 更新商品状态
            int result = productMapper.updateStatus(productId, "已达成");
            if (result <= 0) {
                throw new RuntimeException("商品状态更新失败");
            }

            logger.info("商品已标记为已达成，商品ID：{}", productId);
        } catch (Exception e) {
            logger.error("商品状态更新失败，商品ID：" + productId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void incrementViewCount(Long productId) {
        logger.debug("增加商品浏览数，商品ID：{}", productId);
        try {
            int result = productMapper.incrementViewCount(productId);
            if (result <= 0) {
                logger.warn("商品浏览数增加失败，商品ID：{}", productId);
            }
        } catch (Exception e) {
            logger.error("商品浏览数增加失败，商品ID：" + productId, e);
        }
    }

    @Override
    public ProductListResponse getProductList(
            Integer categoryId,
            String keyword,
            String searchType,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String status,
            String sortField,
            String sortOrder,
            Integer pageNum,
            Integer pageSize) {
        logger.info("查询商品列表");
        try {
            // 参数校验和预处理
            if (keyword != null) {
                keyword = keyword.trim();
                if (keyword.isEmpty()) {
                    keyword = null;
                }
            }
            if (searchType == null || !Arrays.asList("all", "title", "description").contains(searchType)) {
                searchType = "all";
            }

            // 计算偏移量
            int offset = (pageNum - 1) * pageSize;

            // 查询商品列表
            List<Product> products = productMapper.selectProductList(
                categoryId, keyword, searchType, minPrice, maxPrice, status,
                sortField, sortOrder, offset, pageSize);

            // 查询总记录数
            Long total = productMapper.countProductList(
                categoryId, keyword, searchType, minPrice, maxPrice, status);

            // 转换为响应DTO
            List<ProductResponse> productResponses = products.stream()
                .map(product -> {
                    ProductResponse response = new ProductResponse();
                    BeanUtils.copyProperties(product, response);
                    return response;
                })
                .collect(Collectors.toList());

            // 构建响应对象
            ProductListResponse response = new ProductListResponse();
            response.setProducts(productResponses);
            response.setTotal(total);
            response.setPageNum(pageNum);
            response.setPageSize(pageSize);

            return response;
        } catch (Exception e) {
            logger.error("查询商品列表失败", e);
            throw e;
        }
    }
} 