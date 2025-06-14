package com.liuxy.campushub.mapper;

import com.liuxy.campushub.entity.Attachment;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 附件数据访问层
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Mapper
public interface AttachmentMapper {
    
    /**
     * 创建新附件记录
     *
     * @param attachment 附件实体
     * @return 影响行数
     */
    @Insert("INSERT INTO attachment (post_id, file_type, file_url, thumbnail_url, file_size, upload_time) " +
            "VALUES (#{postId}, #{fileType}, #{fileUrl}, #{thumbnailUrl}, #{fileSize}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    int insert(Attachment attachment);
    
    /**
     * 根据ID查询附件
     *
     * @param fileId 附件ID
     * @return 附件实体
     */
    @Select("SELECT * FROM attachment WHERE file_id = #{fileId}")
    Attachment selectById(Long fileId);
    
    /**
     * 根据帖子ID查询附件列表
     *
     * @param postId 帖子ID
     * @return 附件列表
     */
    @Select("SELECT * FROM attachment WHERE post_id = #{postId}")
    List<Attachment> selectByPostId(Long postId);
    
    /**
     * 根据文件类型查询附件
     *
     * @param fileType 文件类型
     * @param offset 偏移量
     * @param pageSize 限制数量
     * @return 附件列表
     */
    @Select("SELECT * FROM attachment WHERE file_type = #{fileType} " +
            "ORDER BY upload_time DESC LIMIT #{offset}, #{pageSize}")
    List<Attachment> selectByType(@Param("fileType") String fileType,
                                @Param("offset") int offset,
                                @Param("pageSize") int pageSize);
    
    /**
     * 删除附件
     *
     * @param fileId 附件ID
     * @return 影响行数
     */
    @Delete("DELETE FROM attachment WHERE file_id = #{fileId}")
    int deleteById(Long fileId);
    
    /**
     * 删除帖子相关的所有附件
     *
     * @param postId 帖子ID
     * @return 影响行数
     */
    @Delete("DELETE FROM attachment WHERE post_id = #{postId}")
    int deleteByPostId(Long postId);
} 