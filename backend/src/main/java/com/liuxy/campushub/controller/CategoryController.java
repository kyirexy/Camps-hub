package com.liuxy.campushub.controller;

import com.liuxy.campushub.entity.Category;
import com.liuxy.campushub.service.CategoryService;
import com.liuxy.campushub.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 分类控制器
 *
 * @author liuxy
 * @since 2024-04-07
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    /**
     * 创建新分类
     *
     * @param category 分类信息
     * @return 创建结果
     */
    @PostMapping
    public Result<Integer> createCategory(@RequestBody Category category) {
        try {
            Integer categoryId = categoryService.createCategory(category);
            return Result.success(categoryId);
        } catch (Exception e) {
            return Result.error("创建分类失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新分类信息
     *
     * @param categoryId 分类ID
     * @param category 分类信息
     * @return 更新结果
     */
    @PutMapping("/{categoryId}")
    public Result<Boolean> updateCategory(@PathVariable Integer categoryId, @RequestBody Category category) {
        try {
            category.setCategoryId(categoryId);
            boolean updated = categoryService.updateCategory(category);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.error("更新分类失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取分类详情
     *
     * @param categoryId 分类ID
     * @return 分类详情
     */
    @GetMapping("/{categoryId}")
    public Result<Category> getCategory(@PathVariable Integer categoryId) {
        try {
            Category category = categoryService.getCategoryById(categoryId);
            if (category == null) {
                return Result.error("分类不存在");
            }
            return Result.success(category);
        } catch (Exception e) {
            return Result.error("获取分类详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有分类
     *
     * @return 分类列表
     */
    @GetMapping
    public Result<List<Category>> getAllCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error("获取分类列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取顶级分类
     *
     * @return 顶级分类列表
     */
    @GetMapping("/top-level")
    public Result<List<Category>> getTopLevelCategories() {
        try {
            List<Category> categories = categoryService.getTopLevelCategories();
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error("获取顶级分类失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取子分类
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @GetMapping("/children/{parentId}")
    public Result<List<Category>> getChildCategories(@PathVariable Integer parentId) {
        try {
            List<Category> categories = categoryService.getChildCategories(parentId);
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error("获取子分类失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据类型获取分类
     *
     * @param categoryType 分类类型
     * @return 分类列表
     */
    @GetMapping("/type/{categoryType}")
    public Result<List<Category>> getCategoriesByType(@PathVariable String categoryType) {
        try {
            List<Category> categories = categoryService.getCategoriesByType(categoryType);
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error("获取分类列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取分类树
     *
     * @return 分类树
     */
    @GetMapping("/tree")
    public Result<List<Category>> getCategoryTree() {
        try {
            List<Category> categories = categoryService.getCategoryTree();
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error("获取分类树失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除分类
     *
     * @param categoryId 分类ID
     * @return 删除结果
     */
    @DeleteMapping("/{categoryId}")
    public Result<Boolean> deleteCategory(@PathVariable Integer categoryId) {
        try {
            // 检查是否有子分类
            if (categoryService.hasChildren(categoryId)) {
                return Result.error("该分类下存在子分类，无法删除");
            }
            boolean deleted = categoryService.deleteCategory(categoryId);
            return Result.success(deleted);
        } catch (Exception e) {
            return Result.error("删除分类失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取分类完整路径
     *
     * @param categoryId 分类ID
     * @return 分类路径
     */
    @GetMapping("/{categoryId}/path")
    public Result<List<Category>> getCategoryPath(@PathVariable Integer categoryId) {
        try {
            List<Category> path = categoryService.getCategoryPath(categoryId);
            return Result.success(path);
        } catch (Exception e) {
            return Result.error("获取分类路径失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有子分类ID
     *
     * @param categoryId 分类ID
     * @return 子分类ID列表
     */
    @GetMapping("/{categoryId}/all-children")
    public Result<List<Integer>> getAllChildCategoryIds(@PathVariable Integer categoryId) {
        try {
            List<Integer> categoryIds = categoryService.getAllChildCategoryIds(categoryId);
            return Result.success(categoryIds);
        } catch (Exception e) {
            return Result.error("获取子分类ID失败: " + e.getMessage());
        }
    }
} 