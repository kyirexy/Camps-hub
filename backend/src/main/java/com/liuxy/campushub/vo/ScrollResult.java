package com.liuxy.campushub.vo;

import lombok.Data;
import java.util.List;
import java.util.Date;

/**
 * 滚动分页结果
 */
@Data
public class ScrollResult<T> {
    private List<T> items;       // 当前页数据
    private boolean hasMore;     // 是否有更多数据
    private Date nextTimestamp;  // 下次请求的时间参数
} 