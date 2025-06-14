package com.liuxy.campushub.service;

import com.liuxy.campushub.entity.Category;
import java.util.List;

/**
 * 分类服务接口
 *
 * @author liuxy
 * @since 2024-04-07
 */
public interface CategoryService {
    
    /**
     * 创建新分类
     *
     * @param category 分类实体
     * @return 创建的分类ID
     */
    Integer createCategory(Category category);
    
    /**
     * 更新分类信息
     *
     * @param category 分类实体
     * @return 是否更新成功
     */
    boolean updateCategory(Category category);
    
    /**
     * 根据ID查询分类
     *
     * @param categoryId 分类ID
     * @return 分类实体
     */
    Category getCategoryById(Integer categoryId);
    
    /**
     * 查询所有分类
     *
     * @return 分类列表
     */
    List<Category> getAllCategories();
    
    /**
     * 查询顶级分类
     *
     * @return 分类列表
     */
    List<Category> getTopLevelCategories();
    
    /**
     * 查询子分类
     *
     * @param parentId 父分类ID
     * @return 分类列表
     */
    List<Category> getChildCategories(Integer parentId);
    
    /**
     * 根据类型查询分类
     *
     * @param categoryType 分类类型
     * @return 分类列表
     */
    List<Category> getCategoriesByType(String categoryType);
    
    /**
     * 查询分类树
     *
     * @return 分类树列表
     */
    List<Category> getCategoryTree();
    
    /**
     * 删除分类
     *
     * @param categoryId 分类ID
     * @return 是否删除成功
     */
    boolean deleteCategory(Integer categoryId);
    
    /**
     * 检查分类是否有子分类
     *
     * @param categoryId 分类ID
     * @return 是否有子分类
     */
    boolean hasChildren(Integer categoryId);
    
    /**
     * 获取分类的完整路径
     *
     * @param categoryId 分类ID
     * @return 分类路径列表（从顶级到当前）
     */
    List<Category> getCategoryPath(Integer categoryId);
    
    /**
     * 获取分类的所有子分类ID（包括自身）
     *
     * @param categoryId 分类ID
     * @return 分类ID列表
     */
    List<Integer> getAllChildCategoryIds(Integer categoryId);
    
    /**
     * 获取分类的子分类数量
     *
     * @param categoryId 分类ID
     * @return 子分类数量
     */
    int countChildren(Integer categoryId);
    
    /**
     * 获取根分类列表
     *
     * @return 根分类列表
     */
    List<Category> getRootCategories();
    
    /**
     * 根据父ID获取分类列表
     *
     * @param parentId 父分类ID
     * @return 分类列表
     */
    List<Category> getCategoriesByParentId(Integer parentId);
}