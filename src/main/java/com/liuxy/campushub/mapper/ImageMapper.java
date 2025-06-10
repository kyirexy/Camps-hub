package com.liuxy.campushub.mapper;

import com.liuxy.campushub.entity.Image;
import org.apache.ibatis.annotations.*;

/**
 * 图片Mapper接口
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Mapper
public interface ImageMapper {
    
    /**
     * 插入图片记录
     *
     * @param image 图片实体
     * @return 影响行数
     */
    @Insert("INSERT INTO image (file_path, origin_name, file_size, status, uploader_id, " +
            "upload_time, usage_type, file_type, filename, original_filename, stored_path) " +
            "VALUES (#{filePath}, #{originName}, #{fileSize}, #{status}, #{uploaderId}, " +
            "#{uploadTime}, #{usageType}, #{fileType}, #{filename}, #{originalFilename}, #{storedPath})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Image image);

    /**
     * 根据ID查询图片
     *
     * @param id 图片ID
     * @return 图片实体
     */
    @Select("SELECT * FROM image WHERE id = #{id}")
    Image selectById(@Param("id") Long id);

    /**
     * 更新图片信息
     *
     * @param image 图片实体
     * @return 影响行数
     */
    @Update("UPDATE image SET status = #{status} WHERE id = #{id}")
    int update(Image image);
    
    /**
     * 删除图片
     *
     * @param id 图片ID
     * @return 影响行数
     */
    @Delete("DELETE FROM image WHERE id = #{id}")
    int delete(@Param("id") Long id);
} 