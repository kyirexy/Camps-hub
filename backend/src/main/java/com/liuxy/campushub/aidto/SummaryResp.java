package com.liuxy.campushub.aidto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 文档总结响应类
 * 继承自ResponseMsg，用于封装文档总结查询的响应信息
 */
@Getter
@Setter
public class SummaryResp extends ResponseMsg {
    /**
     * 总结数据
     */
    private Datas data;

    /**
     * 总结数据内部类
     * 用于存储文档总结的状态和内容
     */
    @Data
    public static class Datas {
        /**
         * 总结状态
         * 可能的值包括：
         * - processing: 处理中
         * - done: 完成
         * - failed: 失败
         * - illegal: 内容违规
         */
        private String summaryStatus;
        
        /**
         * 总结内容
         * 当summaryStatus为done时才会有值
         */
        private String summary;
    }
}
