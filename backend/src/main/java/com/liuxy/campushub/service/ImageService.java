package com.liuxy.campushub.service;

import com.liuxy.campushub.entity.Image;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * 图片服务接口
 *
 * @author liuxy
 * @since 2024-04-07
 */
public interface ImageService {
    
    /**
     * 上传图片
     *
     * @param file 图片文件
     * @param uploaderId 上传者ID
     * @param usageType 图片用途
     * @return 图片实体
     */
    Image uploadImage(MultipartFile file, Long uploaderId, String usageType);
    
    /**
     * 批量上传图片
     *
     * @param files 图片文件列表
     * @param uploaderId 上传者ID
     * @param usageType 图片用途
     * @return 图片实体列表
     */
    List<Image> uploadImages(List<MultipartFile> files, Long uploaderId, String usageType);
    
    /**
     * 根据ID获取图片
     *
     * @param imageId 图片ID
     * @return 图片实体
     */
    Image getImageById(Long imageId);
    
    /**
     * 删除图片
     *
     * @param imageId 图片ID
     * @return 是否删除成功
     */
    boolean deleteImage(Long imageId);
    
    /**
     * 获取图片的完整URL
     *
     * @param image 图片实体
     * @return 图片URL
     */
    String getImageUrl(Image image);
    
    /**
     * 获取图片的存储路径
     *
     * @param image 图片实体
     * @return 图片存储路径
     */
    String getImagePath(Image image);
} 