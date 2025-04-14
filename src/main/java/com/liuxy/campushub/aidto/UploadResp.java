package com.liuxy.campushub.aidto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 文件上传响应类
 * 继承自ResponseMsg，用于封装文件上传的响应信息
 */
@Getter
@Setter
public class UploadResp extends ResponseMsg {
    /**
     * 上传数据
     */
    private Datas data;

    /**
     * 上传数据内部类
     * 用于存储上传文件的相关信息
     */
    @Data
    public static class Datas {
        /**
         * 原始文件路径
         */
        private String originPath;
        
        /**
         * 服务器存储路径
         */
        private String filePath;
        
        /**
         * 文件唯一标识ID
         * 用于后续的文档处理、查询等操作
         */
        private String fileId;
    }
}
