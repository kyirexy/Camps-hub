package com.liuxy.campushub.vo;

import com.liuxy.campushub.entity.*;
import lombok.Data;
import java.util.List;

@Data
public class PostDetailVO {
    private Post post;
    private List<Topic> topics;
    //private List<Attachment> attachments;
    //private LostFound lostFound;

    // 添加直接的用户名字段和头像URL字段
    private String username;
    private String avatar;

    public PostDetailVO(Post post, List<Topic> topics
                      /*List<Attachment> attachments, LostFound lostFound*/) {
        this.post = post;
        this.topics = topics;
        //this.attachments = attachments;
        //this.lostFound = lostFound;
    }
}