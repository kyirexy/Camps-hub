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

    public PostDetailVO(Post post, List<Topic> topics
                      /*List<Attachment> attachments, LostFound lostFound*/) {
        this.post = post;
        this.topics = topics;
        //this.attachments = attachments;
        //this.lostFound = lostFound;
    }
}