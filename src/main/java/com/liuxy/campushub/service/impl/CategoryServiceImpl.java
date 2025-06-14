package com.liuxy.campushub.service.impl;

import com.liuxy.campushub.entity.Category;
import com.liuxy.campushub.mapper.CategoryMapper;
import com.liuxy.campushub.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类服务实现类
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    @Transactional
    public Integer createCategory(Category category) {
        categoryMapper.insert(category);
        return category.getCategoryId();
    }

    @Override
    @Transactional
    public boolean updateCategory(Category category) {
        return categoryMapper.updateById(category) > 0;
    }

    @Override
    public Category getCategoryById(Integer categoryId) {
        return categoryMapper.selectById(categoryId);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.selectAll();
    }

    public List<Category> getRootCategories() {
        return categoryMapper.selectRootCategories();
    }

    public List<Category> getCategoriesByParentId(Integer parentId) {
        return categoryMapper.selectByParentId(parentId);
    }

    @Override
    public List<Category> getTopLevelCategories() {
        return categoryMapper.selectTopLevel();
    }

    @Override
    public List<Category> getChildCategories(Integer parentId) {
        return categoryMapper.selectByParentId(parentId);
    }

    @Override
    public List<Category> getCategoriesByType(String categoryType) {
        return categoryMapper.selectByType(categoryType);
    }

    @Override
    public List<Category> getCategoryTree() {
        List<Category> allCategories = getAllCategories();
        List<Category> rootCategories = new ArrayList<>();
        
        // 构建分类树
        for (Category category : allCategories) {
            if (category.getParentId() == null || category.getParentId() == 0) {
                rootCategories.add(category);
                buildCategoryTree(category, allCategories);
            }
        }
        
        return rootCategories;
    }

    @Override
    @Transactional
    public boolean deleteCategory(Integer categoryId) {
        if (hasChildren(categoryId)) {
            return false;
        }
        return categoryMapper.deleteById(categoryId) > 0;
    }

    @Override
    public boolean hasChildren(Integer categoryId) {
        return categoryMapper.countChildren(categoryId) > 0;
    }

    @Override
    public List<Category> getCategoryPath(Integer categoryId) {
        List<Category> path = new ArrayList<>();
        Category category = getCategoryById(categoryId);
        while (category != null) {
            path.add(0, category);
            category = category.getParentId() != null ? getCategoryById(category.getParentId()) : null;
        }
        return path;
    }

    @Override
    public List<Integer> getAllChildCategoryIds(Integer categoryId) {
        List<Integer> childIds = new ArrayList<>();
        List<Category> children = getChildCategories(categoryId);
        for (Category child : children) {
            childIds.add(child.getCategoryId());
            childIds.addAll(getAllChildCategoryIds(child.getCategoryId()));
        }
        return childIds;
    }

    public int countChildren(Integer categoryId) {
        return categoryMapper.countChildren(categoryId);
    }

    private void buildCategoryTree(Category parent, List<Category> allCategories) {
        List<Category> children = new ArrayList<>();
        for (Category category : allCategories) {
            if (parent.getCategoryId().equals(category.getParentId())) {
                children.add(category);
                buildCategoryTree(category, allCategories);
            }
        }
        parent.setChildren(children);
    }
} 