# 商品搜索接口文档

## 1. 商品搜索列表接口

### 基本信息

- **接口URL**: `/api/v1/products`
- **请求方式**: GET
- **接口描述**: 根据条件搜索商品列表，支持多种筛选条件和排序方式

### 请求参数

#### Query Parameters

| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| keyword | string | 否 | - | 搜索关键词 |
| searchType | string | 否 | "all" | 搜索类型：all-全部，title-标题，description-描述 |
| categoryId | integer | 否 | - | 分类ID |
| minPrice | number | 否 | - | 最低价格 |
| maxPrice | number | 否 | - | 最高价格 |
| status | string | 否 | "出售中" | 商品状态：出售中、已下架、已达成 |
| sortField | string | 否 | "createTime" | 排序字段：createTime-创建时间，price-价格，viewCount-浏览量 |
| sortOrder | string | 否 | "desc" | 排序方式：asc-升序，desc-降序 |
| pageNum | integer | 否 | 1 | 页码，从1开始 |
| pageSize | integer | 否 | 10 | 每页记录数 |

### 响应结果

```json
{
    "products": [
        {
            "productId": 123,         // 商品ID
            "sellerId": 456,         // 发布者ID
            "sellerUsername": "user", // 发布者用户名
            "categoryId": 1,          // 分类ID
            "categoryName": "数码",   // 分类名称
            "title": "商品标题",      // 商品标题
            "description": "商品详细描述", // 详细描述
            "priceType": "固定价",    // 报价类型：面议、区间报价、固定价
            "minPrice": 100.00,      // 最低价（区间报价时有效）
            "maxPrice": 200.00,      // 最高价（区间报价时有效）
            "expectPrice": 150.00,   // 期望价（固定价时有效）
            "coverImages": [         // 封面图数组
                "image_url_1",
                "image_url_2"
            ],
            "contactWechat": "wxid", // 卖家微信ID（仅在允许查看时显示）
            "isContactVisible": true, // 是否公开联系方式
            "viewCount": 10,         // 浏览数
            "status": "出售中",      // 商品状态
            "createTime": "2024-03-25T12:00:00", // 创建时间
            "updateTime": "2024-03-25T13:00:00"  // 更新时间
        }
    ],
    "total": 100,      // 总记录数
    "pageNum": 1,      // 当前页码
    "pageSize": 10     // 每页记录数
}
```

### 示例

#### 请求示例

1. 基本搜索：
```http
GET /api/v1/products?keyword=笔记本&pageNum=1&pageSize=10
```

2. 带分类和价格区间的搜索：
```http
GET /api/v1/products?keyword=笔记本&categoryId=1&minPrice=1000&maxPrice=5000&pageNum=1&pageSize=10
```

3. 仅搜索标题：
```http
GET /api/v1/products?keyword=笔记本&searchType=title&pageNum=1&pageSize=10
```

4. 按价格排序：
```http
GET /api/v1/products?sortField=price&sortOrder=asc&pageNum=1&pageSize=10
```

### 注意事项

1. 价格相关的搜索会同时考虑固定价和区间报价的情况
2. 搜索关键词会自动去除首尾空格
3. 默认按创建时间倒序排序
4. 联系方式仅在商品设置为公开可见时才会返回
5. 返回结果中的价格字段根据报价类型的不同会有不同的显示规则：
   - 固定价：只有 expectPrice 有值
   - 区间报价：只有 minPrice 和 maxPrice 有值
   - 面议：所有价格字段都为 null 