package com.liuxy.campushub.mapper;

import com.liuxy.campushub.entity.Product;
import com.liuxy.campushub.handler.JsonListTypeHandler;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.math.BigDecimal;

/**
 * 商品Mapper接口
 *
 * @author liuxy
 * @date 2024-03-25
 */
@Mapper
public interface ProductMapper {

    /**
     * 插入商品信息
     *
     * @param product 商品信息
     * @return 影响行数
     */
    @Insert("INSERT INTO product (seller_id, seller_username, category_id, title, description, price_type, " +
            "min_price, max_price, expect_price, cover_images, contact_wechat, " +
            "is_contact_visible, status) " +
            "VALUES (#{sellerId}, #{sellerUsername}, #{categoryId}, #{title}, #{description}, #{priceType}, " +
            "#{minPrice}, #{maxPrice}, #{expectPrice}, #{coverImages,typeHandler=com.liuxy.campushub.handler.JsonListTypeHandler}, #{contactWechat}, " +
            "#{isContactVisible}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "productId")
    int insert(Product product);

    /**
     * 根据商品ID查询商品信息
     *
     * @param productId 商品ID
     * @return 商品信息
     */
    @Select("SELECT p.*, u.username as seller_username " +
            "FROM product p " +
            "LEFT JOIN student_user u ON p.seller_id = u.user_id " +
            "WHERE p.product_id = #{productId}")
    @Results({
        @Result(property = "productId", column = "product_id", id = true),
        @Result(property = "sellerId", column = "seller_id"),
        @Result(property = "sellerUsername", column = "seller_username"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "title", column = "title"),
        @Result(property = "description", column = "description"),
        @Result(property = "priceType", column = "price_type"),
        @Result(property = "minPrice", column = "min_price"),
        @Result(property = "maxPrice", column = "max_price"),
        @Result(property = "expectPrice", column = "expect_price"),
        @Result(property = "coverImages", column = "cover_images", typeHandler = JsonListTypeHandler.class),
        @Result(property = "contactWechat", column = "contact_wechat"),
        @Result(property = "isContactVisible", column = "is_contact_visible"),
        @Result(property = "viewCount", column = "view_count"),
        @Result(property = "status", column = "status"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    Product selectById(@Param("productId") Long productId);

    /**
     * 更新商品信息
     *
     * @param product 商品信息
     * @return 影响行数
     */
    @Update("UPDATE product SET " +
            "category_id = #{categoryId}, " +
            "title = #{title}, " +
            "description = #{description}, " +
            "price_type = #{priceType}, " +
            "min_price = #{minPrice}, " +
            "max_price = #{maxPrice}, " +
            "expect_price = #{expectPrice}, " +
            "cover_images = #{coverImages,typeHandler=com.liuxy.campushub.handler.JsonListTypeHandler}, " +
            "contact_wechat = #{contactWechat}, " +
            "is_contact_visible = #{isContactVisible}, " +
            "status = #{status} " +
            "WHERE product_id = #{productId}")
    int update(Product product);

    /**
     * 更新商品状态
     *
     * @param productId 商品ID
     * @param status 商品状态
     * @return 影响行数
     */
    @Update("UPDATE product SET status = #{status} WHERE product_id = #{productId}")
    int updateStatus(@Param("productId") Long productId, @Param("status") String status);

    /**
     * 增加商品浏览数
     *
     * @param productId 商品ID
     * @return 影响行数
     */
    @Update("UPDATE product SET view_count = view_count + 1 WHERE product_id = #{productId}")
    int incrementViewCount(@Param("productId") Long productId);
    
    /**
     * 查询商品列表
     *
     * @param categoryId 分类ID
     * @param keyword 关键词
     * @param searchType 搜索类型：all-全部，title-标题，description-描述
     * @param minPrice 最低价
     * @param maxPrice 最高价
     * @param status 商品状态
     * @param sortField 排序字段
     * @param sortOrder 排序方式
     * @param offset 偏移量
     * @param pageSize 每页记录数
     * @return 商品列表
     */
    List<Product> selectProductList(
            @Param("categoryId") Integer categoryId,
            @Param("keyword") String keyword,
            @Param("searchType") String searchType,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("status") String status,
            @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder,
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize);
    
    /**
     * 查询商品总数
     *
     * @param categoryId 分类ID
     * @param keyword 关键词
     * @param searchType 搜索类型：all-全部，title-标题，description-描述
     * @param minPrice 最低价
     * @param maxPrice 最高价
     * @param status 商品状态
     * @return 商品总数
     */
    Long countProductList(
            @Param("categoryId") Integer categoryId,
            @Param("keyword") String keyword,
            @Param("searchType") String searchType,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("status") String status);

    /**
     * 查询用户发布的商品列表
     *
     * @param userId 用户ID
     * @param status 商品状态
     * @param sortField 排序字段
     * @param sortOrder 排序方式
     * @param offset 偏移量
     * @param pageSize 每页记录数
     * @return 商品列表
     */
    @Select("<script>" +
            "SELECT p.*, u.username as seller_username " +
            "FROM product p " +
            "LEFT JOIN student_user u ON p.seller_id = u.user_id " +
            "WHERE p.seller_id = #{userId} " +
            "<if test='status != null'>" +
            "AND p.status = #{status} " +
            "</if>" +
            "ORDER BY p.${sortField} ${sortOrder} " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    @Results({
        @Result(property = "productId", column = "product_id", id = true),
        @Result(property = "sellerId", column = "seller_id"),
        @Result(property = "sellerUsername", column = "seller_username"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "title", column = "title"),
        @Result(property = "description", column = "description"),
        @Result(property = "priceType", column = "price_type"),
        @Result(property = "minPrice", column = "min_price"),
        @Result(property = "maxPrice", column = "max_price"),
        @Result(property = "expectPrice", column = "expect_price"),
        @Result(property = "coverImages", column = "cover_images", typeHandler = JsonListTypeHandler.class),
        @Result(property = "contactWechat", column = "contact_wechat"),
        @Result(property = "isContactVisible", column = "is_contact_visible"),
        @Result(property = "viewCount", column = "view_count"),
        @Result(property = "status", column = "status"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<Product> selectMyProducts(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder,
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize);

    /**
     * 查询用户发布的商品总数
     *
     * @param userId 用户ID
     * @param status 商品状态
     * @return 商品总数
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM product " +
            "WHERE seller_id = #{userId} " +
            "<if test='status != null'>" +
            "AND status = #{status} " +
            "</if>" +
            "</script>")
    Long countMyProducts(@Param("userId") Long userId, @Param("status") String status);

}