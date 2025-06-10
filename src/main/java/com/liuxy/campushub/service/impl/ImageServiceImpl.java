package com.liuxy.campushub.service.impl;

import com.liuxy.campushub.entity.Image;
import com.liuxy.campushub.mapper.ImageMapper;
import com.liuxy.campushub.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);

    @Value("${app.image.upload.path:/data/images}")
    private String uploadBasePath;

    @Value("${app.image.url.prefix:/images}")
    private String imageUrlPrefix;

    @Autowired
    private ImageMapper imageMapper;

    @Override
    @Transactional
    public Image uploadImage(MultipartFile file, Long uploaderId, String usageType) {
        try {
            // 生成存储路径
            String relativePath = generateRelativePath(file.getOriginalFilename());
            String fullPath = uploadBasePath + relativePath;

            // 确保目录存在
            Path directory = Paths.get(fullPath).getParent();
            Files.createDirectories(directory);

            // 保存文件
            file.transferTo(new File(fullPath));

            // 创建图片记录
            Image image = new Image();
            image.setFilePath(relativePath);
            image.setOriginName(file.getOriginalFilename());
            image.setFileSize((int) file.getSize());
            image.setStatus(1);
            image.setUploaderId(uploaderId);
            image.setUploadTime(LocalDateTime.now());
            image.setUsageType(usageType);
            image.setFileType(getFileExtension(file.getOriginalFilename()));
            image.setFilename(getFileName(file.getOriginalFilename()));
            image.setOriginalFilename(file.getOriginalFilename());
            image.setStoredPath(relativePath);

            // 保存到数据库
            imageMapper.insert(image);

            return image;
        } catch (IOException e) {
            logger.error("图片上传失败", e);
            throw new RuntimeException("图片上传失败", e);
        }
    }

    @Override
    @Transactional
    public List<Image> uploadImages(List<MultipartFile> files, Long uploaderId, String usageType) {
        List<Image> images = new ArrayList<>();
        for (MultipartFile file : files) {
            images.add(uploadImage(file, uploaderId, usageType));
        }
        return images;
    }

    @Override
    public Image getImageById(Long imageId) {
        return imageMapper.selectById(imageId);
    }

    @Override
    @Transactional
    public boolean deleteImage(Long imageId) {
        Image image = getImageById(imageId);
        if (image == null) {
            return false;
        }

        // 删除物理文件
        try {
            String fullPath = uploadBasePath + image.getFilePath();
            Files.deleteIfExists(Paths.get(fullPath));
        } catch (IOException e) {
            logger.error("删除图片文件失败", e);
        }

        // 更新数据库状态
        image.setStatus(0);
        return imageMapper.update(image) > 0;
    }

    @Override
    public String getImageUrl(Image image) {
        return imageUrlPrefix + image.getFilePath();
    }

    @Override
    public String getImagePath(Image image) {
        return uploadBasePath + image.getFilePath();
    }

    private String generateRelativePath(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return "/products/" + datePath + "/" + uuid + "." + extension;
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private String getFileName(String filename) {
        return filename.substring(0, filename.lastIndexOf("."));
    }
} 