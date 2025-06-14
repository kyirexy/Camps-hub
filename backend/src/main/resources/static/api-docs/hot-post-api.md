# 热点帖子系统 API 文档

## 目录
- [接口说明](#接口说明)
- [通用说明](#通用说明)
- [接口列表](#接口列表)
  - [获取热点帖子列表](#获取热点帖子列表)
  - [获取突发热点帖子](#获取突发热点帖子)
  - [分页获取热点帖子](#分页获取热点帖子)
  - [获取用户热点帖子](#获取用户热点帖子)
  - [获取分类热点帖子](#获取分类热点帖子)

## 接口说明

本文档描述了热点帖子系统的所有接口。热点帖子系统通过综合考虑帖子的浏览量、点赞数、评论数、分享数等指标，结合时间衰减因子，动态计算帖子的热度值，并支持突发热点的识别。

## 通用说明


### 通用响应格式
```json
{
    "code": 200,           // 状态码
    "message": "success",  // 响应消息
    "data": {             // 响应数据
        // 具体数据字段
    }
}
```

### 通用状态码
| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 接口列表

### 获取热点帖子列表

获取当前最热门的帖子列表。

#### 请求信息
- 请求路径：`/posts/hot`
- 请求方法：GET
- 请求参数：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| limit | int | 否 | 获取条数，默认10条 |

#### 响应信息
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "items": [
            {
                "postId": 123,
                "userId": 456,
                "username": "张三",
                "avatar": "http://example.com/avatar.jpg",
                "title": "帖子标题",
                "content": "帖子内容",
                "categoryId": 1,
                "categoryName": "分类名称",
                "viewCount": 1000,
                "likeCount": 100,
                "commentCount": 50,
                "shareCount": 20,
                "hotness": 85.5,
                "isNew": true,
                "isBurst": false,
                "createdAt": "2024-04-27T10:00:00",
                "updatedAt": "2024-04-27T10:30:00"
            }
        ],
        "total": 100
    }
}
```

### 获取突发热点帖子

获取24小时内评论数超过100的突发热点帖子。

#### 请求信息
- 请求路径：`/posts/hot/burst`
- 请求方法：GET
- 请求参数：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| limit | int | 否 | 获取条数，默认5条 |

#### 响应信息
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "items": [
            {
                "postId": 123,
                "userId": 456,
                "username": "张三",
                "avatar": "http://example.com/avatar.jpg",
                "title": "帖子标题",
                "content": "帖子内容",
                "categoryId": 1,
                "categoryName": "分类名称",
                "viewCount": 2000,
                "likeCount": 200,
                "commentCount": 150,
                "shareCount": 50,
                "hotness": 95.5,
                "isNew": true,
                "isBurst": true,
                "createdAt": "2024-04-27T10:00:00",
                "updatedAt": "2024-04-27T10:30:00"
            }
        ],
        "total": 10
    }
}
```

### 分页获取热点帖子

分页获取热点帖子列表。

#### 请求信息
- 请求路径：`/posts/hot/page`
- 请求方法：GET
- 请求参数：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | int | 是 | 页码，从1开始 |
| size | int | 否 | 每页大小，默认10 |

#### 响应信息
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "items": [
            {
                "postId": 123,
                "userId": 456,
                "username": "张三",
                "avatar": "http://example.com/avatar.jpg",
                "title": "帖子标题",
                "content": "帖子内容",
                "categoryId": 1,
                "categoryName": "分类名称",
                "viewCount": 1000,
                "likeCount": 100,
                "commentCount": 50,
                "shareCount": 20,
                "hotness": 85.5,
                "isNew": true,
                "isBurst": false,
                "createdAt": "2024-04-27T10:00:00",
                "updatedAt": "2024-04-27T10:30:00"
            }
        ],
        "total": 100,
        "page": 1,
        "size": 10,
        "hasMore": true
    }
}
```

### 获取用户热点帖子

获取指定用户的热点帖子列表。

#### 请求信息
- 请求路径：`/posts/hot/user/{userId}`
- 请求方法：GET
- 路径参数：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | long | 是 | 用户ID |

- 查询参数：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | int | 是 | 页码，从1开始 |
| size | int | 否 | 每页大小，默认10 |

#### 响应信息
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "items": [
            {
                "postId": 123,
                "userId": 456,
                "username": "张三",
                "avatar": "http://example.com/avatar.jpg",
                "title": "帖子标题",
                "content": "帖子内容",
                "categoryId": 1,
                "categoryName": "分类名称",
                "viewCount": 1000,
                "likeCount": 100,
                "commentCount": 50,
                "shareCount": 20,
                "hotness": 85.5,
                "isNew": true,
                "isBurst": false,
                "createdAt": "2024-04-27T10:00:00",
                "updatedAt": "2024-04-27T10:30:00"
            }
        ],
        "total": 50,
        "page": 1,
        "size": 10,
        "hasMore": true
    }
}
```

### 获取分类热点帖子

获取指定分类的热点帖子列表。

#### 请求信息
- 请求路径：`/posts/hot/category/{categoryId}`
- 请求方法：GET
- 路径参数：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| categoryId | int | 是 | 分类ID |

- 查询参数：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | int | 是 | 页码，从1开始 |
| size | int | 否 | 每页大小，默认10 |

#### 响应信息
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "items": [
            {
                "postId": 123,
                "userId": 456,
                "username": "张三",
                "avatar": "http://example.com/avatar.jpg",
                "title": "帖子标题",
                "content": "帖子内容",
                "categoryId": 1,
                "categoryName": "分类名称",
                "viewCount": 1000,
                "likeCount": 100,
                "commentCount": 50,
                "shareCount": 20,
                "hotness": 85.5,
                "isNew": true,
                "isBurst": false,
                "createdAt": "2024-04-27T10:00:00",
                "updatedAt": "2024-04-27T10:30:00"
            }
        ],
        "total": 30,
        "page": 1,
        "size": 10,
        "hasMore": true
    }
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 40001 | 参数错误 |
| 40002 | 分类不存在 |
| 40003 | 用户不存在 |
| 40004 | 帖子不存在 |
| 50001 | 服务器内部错误 |

## 注意事项

1. 热度值计算规则：
   - 浏览量权重：20%
   - 点赞数权重：30%
   - 评论数权重：25%
   - 分享数权重：10%
   - 时间衰减因子：0.2

2. 突发热点判定规则：
   - 24小时内评论数超过100
   - 热度值额外提升50%

3. 新帖判定规则：
   - 发布时间在24小时内

4. 缓存策略：
   - 热点帖子列表缓存时间：5分钟
   - 突发热点列表缓存时间：1分钟

5. 性能优化：
   - 使用Redis缓存热点数据
   - 定时任务更新热度值
   - 分页查询限制最大条数 