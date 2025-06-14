package com.liuxy.campushub.dto;

import java.util.List;

/**
 * 商品列表响应DTO
 *
 * @author liuxy
 * @date 2024-03-25
 */
public class ProductListResponse {

    /**
     * 商品列表
     */
    private List<ProductResponse> products;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页记录数
     */
    private Integer pageSize;

    public List<ProductResponse> getProducts() {
        return products;
    }

    public void setProducts(List<ProductResponse> products) {
        this.products = products;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}