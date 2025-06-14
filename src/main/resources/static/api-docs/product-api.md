# 商品模块API文档

本文档详细描述了Campus-hub平台商品模块的API接口，供前端开发人员参考使用。

## 接口基本信息

- 基础路径: `/api/v1/products`
- 所有请求需要携带认证信息: `Authorization: Bearer {token}`
- 所有接口均需要用户登录认证

## 1. 发布商品

### 请求信息

- 请求方法: `POST`
- 请求URL: `/api/v1/products`
- 请求头:
  ```
  Authorization: Bearer {token}
  Content-Type: application/json
  ```
- 请求参数:
  - Query参数:
    - `userId`: 用户ID，类型为Long
  - 请求体:
  ```json
  {
    "categoryId": 1,           // 分类ID，必填，整数类型
    "title": "商品标题",      // 商品标题，必填，2-100个字符
    "description": "商品详细描述", // 详细描述，10-2000个字符
    "priceType": "固定价",    // 报价类型，必填，可选值：面议、区间报价、固定价
    "minPrice": 100.00,      // 最低价（区间报价时生效），数字类型
    "maxPrice": 200.00,      // 最高价（区间报价时生效），数字类型
    "expectPrice": 150.00,   // 期望价（固定价时存储），数字类型
    "coverImages": [         // 封面图数组，0-9张
      "image_url_1",
      "image_url_2"
    ],
    "contactWechat": "wxid", // 卖家微信ID（可选）
    "isContactVisible": true // 是否公开联系方式，布尔类型
  }
  ```

### 响应信息

- 成功响应 (状态码: 200)
  ```json
  123  // 返回商品ID，数字类型
  ```

- 错误响应
  - 400: 请求参数错误
  - 401: 未授权
  - 500: 服务器内部错误

## 2. 获取商品详情

### 请求信息

- 请求方法: `GET`
- 请求URL: `/api/v1/products/{productId}`
- 请求头:
  ```
  Authorization: Bearer {token}
  ```
- 路径参数:
  - `productId`: 商品ID，类型为Long

### 响应信息

- 成功响应 (状态码: 200)
  ```json
  {
    "productId": 123,         // 商品ID
    "sellerId": 456,         // 发布者ID
    "sellerUsername": "user", // 发布者用户名
    "categoryId": 1,          // 分类ID
    "categoryName": "数码",   // 分类名称
    "title": "商品标题",      // 商品标题
    "description": "商品详细描述", // 详细描述
    "priceType": "固定价",    // 报价类型
    "minPrice": 100.00,      // 最低价
    "maxPrice": 200.00,      // 最高价
    "expectPrice": 150.00,   // 期望价
    "coverImages": [         // 封面图数组
      "image_url_1",
      "image_url_2"
    ],
    "contactWechat": "wxid", // 卖家微信ID
    "isContactVisible": true, // 是否公开联系方式
    "viewCount": 10,         // 浏览数
    "status": "出售中",      // 商品状态：出售中、已下架、已达成
    "createTime": "2024-03-25T12:00:00", // 创建时间
    "updateTime": "2024-03-25T13:00:00"  // 更新时间
  }
  ```

- 错误响应
  - 401: 未授权
  - 404: 商品不存在
  - 500: 服务器内部错误

## 3. 更新商品信息

### 请求信息

- 请求方法: `PUT`
- 请求URL: `/api/v1/products/{productId}`
- 请求头:
  ```
  Authorization: Bearer {token}
  Content-Type: application/json
  ```
- 路径参数:
  - `productId`: 商品ID，类型为Long
- 请求体: 同发布商品
  ```json
  {
    "categoryId": 1,           // 分类ID，必填，整数类型
    "title": "更新后的商品标题", // 商品标题，必填，2-100个字符
    "description": "更新后的商品详细描述", // 详细描述，10-2000个字符
    "priceType": "固定价",    // 报价类型，必填，可选值：面议、区间报价、固定价
    "minPrice": 100.00,      // 最低价（区间报价时生效），数字类型
    "maxPrice": 200.00,      // 最高价（区间报价时生效），数字类型
    "expectPrice": 150.00,   // 期望价（固定价时存储），数字类型
    "coverImages": [         // 封面图数组，0-9张
      "image_url_1",
      "image_url_2"
    ],
    "contactWechat": "wxid", // 卖家微信ID（可选）
    "isContactVisible": true // 是否公开联系方式，布尔类型
  }
  ```

### 响应信息

- 成功响应 (状态码: 204)
  - 无响应体

- 错误响应
  - 400: 请求参数错误
  - 401: 未授权
  - 403: 无权限操作（非发布者）
  - 404: 商品不存在
  - 500: 服务器内部错误

## 4. 下架商品

### 请求信息

- 请求方法: `PUT`
- 请求URL: `/api/v1/products/{productId}/take-down`
- 请求头:
  ```
  Authorization: Bearer {token}
  ```
- 路径参数:
  - `productId`: 商品ID，类型为Long

### 响应信息

- 成功响应 (状态码: 204)
  - 无响应体

- 错误响应
  - 401: 未授权
  - 403: 无权限操作（非发布者）
  - 404: 商品不存在
  - 500: 服务器内部错误

## 5. 标记商品为已达成

### 请求信息

- 请求方法: `PUT`
- 请求URL: `/api/v1/products/{productId}/complete`
- 请求头:
  ```
  Authorization: Bearer {token}
  ```
- 路径参数:
  - `productId`: 商品ID，类型为Long

### 响应信息

- 成功响应 (状态码: 204)
  - 无响应体

- 错误响应
  - 401: 未授权
  - 403: 无权限操作（非发布者）
  - 404: 商品不存在
  - 500: 服务器内部错误

## 数据模型

### ProductRequest

```typescript
interface ProductRequest {
  categoryId: number;       // 分类ID，必填
  title: string;           // 商品标题，必填，2-100个字符
  description: string;     // 详细描述，10-2000个字符
  priceType: string;       // 报价类型，必填，可选值：面议、区间报价、固定价
  minPrice?: number;       // 最低价（区间报价时生效）
  maxPrice?: number;       // 最高价（区间报价时生效）
  expectPrice?: number;    // 期望价（固定价时存储）
  coverImages?: string[];  // 封面图数组，0-9张
  contactWechat?: string;  // 卖家微信ID（可选）
  isContactVisible: boolean; // 是否公开联系方式
}
```

### ProductResponse

```typescript
interface ProductResponse {
  productId: number;        // 商品ID
  sellerId: number;         // 发布者ID
  sellerUsername: string;   // 发布者用户名
  categoryId: number;       // 分类ID
  categoryName: string;     // 分类名称
  title: string;            // 商品标题
  description: string;      // 详细描述
  priceType: string;        // 报价类型
  minPrice?: number;        // 最低价
  maxPrice?: number;        // 最高价
  expectPrice?: number;     // 期望价
  coverImages: string[];    // 封面图数组
  contactWechat?: string;   // 卖家微信ID
  isContactVisible: boolean; // 是否公开联系方式
  viewCount: number;        // 浏览数
  status: string;           // 商品状态：出售中、已下架、已达成
  createTime: string;       // 创建时间
  updateTime: string;       // 更新时间
}
```

## 注意事项

1. 所有接口均需要用户登录认证，请在请求头中携带有效的JWT令牌
2. 更新商品、下架商品、标记商品为已达成等操作只能由商品发布者执行
3. 价格相关字段根据priceType的不同有不同的必填要求：
   - 面议：minPrice、maxPrice、expectPrice均可不填
   - 区间报价：minPrice和maxPrice必填，expectPrice不填
   - 固定价：expectPrice必填，minPrice和maxPrice不填