package com.liuxy.campushub.aidto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 文档状态查询响应类
 * 继承自ResponseMsg，用于封装文档处理状态查询的响应信息
 */
@Getter
@Setter
public class StatusResp extends ResponseMsg {
    /**
     * 文档状态数据列表
     */
    private List<Datas> data;

    /**
     * 文档状态数据内部类
     * 用于存储单个文档的状态信息
     */
    @Data
    public static class Datas {
        /**
         * 文档ID
         */
        private String fileId;
        
        /**
         * 文档处理状态
         * 可能的值包括：
         * - uploading: 上传中
         * - uploaded: 上传完成
         * - vectoring: 向量化中
         * - vectored: 向量化完成
         * - failed: 处理失败
         */
        private String fileStatus;
    }
}