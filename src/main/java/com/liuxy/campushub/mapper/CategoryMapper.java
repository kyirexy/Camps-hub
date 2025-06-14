package com.liuxy.campushub.mapper;

import com.liuxy.campushub.entity.Category;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 分类数据访问接口
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Mapper
public interface CategoryMapper {
    
    /**
     * 创建新分类
     *
     * @param category 分类实体
     * @return 影响行数
     */
    @Insert("INSERT INTO category (parent_id, category_name, category_type, sort_order) " +
            "VALUES (#{parentId}, #{categoryName}, #{categoryType}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "categoryId")
    int insert(Category category);
    
    /**
     * 更新分类信息
     *
     * @param category 分类实体
     * @return 影响行数
     */
    @Update("UPDATE category SET parent_id = #{parentId}, category_name = #{categoryName}, " +
            "category_type = #{categoryType}, sort_order = #{sortOrder} " +
            "WHERE category_id = #{categoryId}")
    int update(Category category);
    
    /**
     * 根据ID更新分类信息
     *
     * @param category 分类实体
     * @return 影响行数
     */
    @Update("UPDATE category SET parent_id = #{parentId}, category_name = #{categoryName}, " +
            "category_type = #{categoryType}, sort_order = #{sortOrder}, " +
            "description = #{description}, status = #{status} " +
            "WHERE category_id = #{categoryId}")
    int updateById(Category category);
    
    /**
     * 根据ID查询分类
     *
     * @param categoryId 分类ID
     * @return 分类实体
     */
    @Select("SELECT * FROM category WHERE category_id = #{categoryId}")
    Category selectById(Integer categoryId);
    
    /**
     * 查询所有分类
     *
     * @return 分类列表
     */
    @Select("SELECT * FROM category ORDER BY sort_order, category_id")
    List<Category> selectAll();
    
    /**
     * 查询顶级分类
     *
     * @return 分类列表
     */
    @Select("SELECT * FROM category WHERE parent_id = 0 ORDER BY sort_order, category_id")
    List<Category> selectTopLevel();
    
    /**
     * 查询根分类
     *
     * @return 分类列表
     */
    @Select("SELECT * FROM category WHERE parent_id = 0 OR parent_id IS NULL ORDER BY sort_order, category_id")
    List<Category> selectRootCategories();
    
    /**
     * 查询子分类
     *
     * @param parentId 父分类ID
     * @return 分类列表
     */
    @Select("SELECT * FROM category WHERE parent_id = #{parentId} ORDER BY sort_order, category_id")
    List<Category> selectChildren(Integer parentId);
    
    /**
     * 根据父ID查询分类
     *
     * @param parentId 父分类ID
     * @return 分类列表
     */
    @Select("SELECT * FROM category WHERE parent_id = #{parentId} ORDER BY sort_order, category_id")
    List<Category> selectByParentId(Integer parentId);
    
    /**
     * 根据类型查询分类
     *
     * @param categoryType 分类类型
     * @return 分类列表
     */
    @Select("SELECT * FROM category WHERE category_type = #{categoryType} ORDER BY sort_order, category_id")
    List<Category> selectByType(String categoryType);
    
    /**
     * 查询分类树
     *
     * @return 分类树列表
     */
    @Select("WITH RECURSIVE category_tree AS (" +
            "  SELECT *, 1 as level FROM category WHERE parent_id = 0 " +
            "  UNION ALL " +
            "  SELECT c.*, ct.level + 1 as level " +
            "  FROM category c " +
            "  JOIN category_tree ct ON c.parent_id = ct.category_id " +
            ") " +
            "SELECT * FROM category_tree ORDER BY level, sort_order, category_id")
    List<Category> selectTree();
    
    /**
     * 删除分类
     *
     * @param categoryId 分类ID
     * @return 影响行数
     */
    @Delete("DELETE FROM category WHERE category_id = #{categoryId}")
    int deleteById(Integer categoryId);
    
    /**
     * 检查分类是否有子分类
     *
     * @param categoryId 分类ID
     * @return 子分类数量
     */
    @Select("SELECT COUNT(*) FROM category WHERE parent_id = #{categoryId}")
    int countChildren(Integer categoryId);
}