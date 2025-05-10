# 帖子详情与评论接口文档 (UniApp版)

## 1. 帖子详情接口

### 1.1 获取帖子详情
- 请求路径：`/api/posts/{postId}`
- 请求方法：`GET`
- 请求参数：

| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| postId | Long | 是 | 帖子ID | 1 |

### 响应信息
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "postId": 1,
        "title": "帖子标题",
        "content": "帖子内容",
        "postType": "normal",
        "createdAt": "2024-04-07 10:00:00",
        "updatedAt": "2024-04-07 10:00:00",
        "viewCount": 100,
        "likeCount": 50,
        "commentCount": 20,
        "categoryName": "分类名称",
        "categoryId": 1,
        "author": {
            "userId": 1,
            "username": "用户名",
            "avatar": "头像URL",
            "level": 1,
            "signature": "个性签名"
        },
        "images": [
            {
                "imageId": 1,
                "url": "图片URL",
                "width": 800,
                "height": 600
            }
        ],
        "isLiked": false,
        "isCollected": false
    }
}
```

## 2. 帖子评论接口

### 2.1 获取评论列表
- 请求路径：`/api/posts/{postId}/comments`
- 请求方法：`GET`
- 请求参数：

| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| postId | Long | 是 | 帖子ID | 1 |
| page | Integer | 否 | 页码，默认1 | 1 |
| size | Integer | 否 | 每页数量，默认20 | 20 |
| sort | String | 否 | 排序方式：latest(最新)/hot(最热)，默认latest | latest |

### 响应信息
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "total": 100,
        "pages": 5,
        "current": 1,
        "items": [
            {
                "commentId": 1,
                "content": "评论内容",
                "createdAt": "2024-04-07 10:00:00",
                "likeCount": 10,
                "isLiked": false,
                "author": {
                    "userId": 1,
                    "username": "用户名",
                    "avatar": "头像URL",
                    "level": 1
                },
                "replies": [
                    {
                        "replyId": 1,
                        "content": "回复内容",
                        "createdAt": "2024-04-07 10:05:00",
                        "likeCount": 5,
                        "isLiked": false,
                        "author": {
                            "userId": 2,
                            "username": "用户名",
                            "avatar": "头像URL",
                            "level": 1
                        },
                        "replyTo": {
                            "userId": 1,
                            "username": "用户名"
                        }
                    }
                ],
                "replyCount": 1
            }
        ]
    }
}
```

### 2.2 发表评论
- 请求路径：`/api/posts/{postId}/comments`
- 请求方法：`POST`
- 请求参数：

| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| postId | Long | 是 | 帖子ID | 1 |
| content | String | 是 | 评论内容 | "这是一条评论" |
| parentId | Long | 否 | 父评论ID，回复评论时必填 | 1 |
| replyToId | Long | 否 | 回复的用户ID，回复评论时必填 | 2 |

### 响应信息
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "commentId": 1,
        "content": "评论内容",
        "createdAt": "2024-04-07 10:00:00",
        "author": {
            "userId": 1,
            "username": "用户名",
            "avatar": "头像URL",
            "level": 1
        }
    }
}
```

### 2.3 点赞评论
- 请求路径：`/api/comments/{commentId}/like`
- 请求方法：`POST`
- 请求参数：

| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| commentId | Long | 是 | 评论ID | 1 |

### 响应信息
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "likeCount": 11,
        "isLiked": true
    }
}
```

## 3. 开发规范

### 3.1 接口调用规范
1. 统一使用 Promise 方式处理异步请求
2. 所有请求需要处理错误情况
3. 请求超时时间设置为 10 秒
4. 需要登录的接口要携带 token
5. 图片上传需要压缩处理

### 3.2 数据缓存规范
1. 帖子详情缓存时间不超过 5 分钟
2. 评论列表缓存时间不超过 2 分钟
3. 用户信息缓存时间不超过 30 分钟
4. 使用 uni.setStorageSync 存储缓存数据
5. 缓存数据需要包含时间戳

### 3.3 错误处理规范
1. 网络错误统一提示"网络连接失败，请检查网络设置"
2. 服务器错误统一提示"服务器错误，请稍后重试"
3. 登录失效统一跳转到登录页面
4. 所有错误需要记录日志
5. 关键操作需要二次确认

### 3.4 性能优化规范
1. 图片使用懒加载
2. 长列表使用虚拟列表
3. 评论列表分页加载
4. 图片需要压缩和裁剪
5. 避免频繁的 DOM 操作

### 3.5 用户体验规范
1. 所有操作都需要有加载状态
2. 评论发表需要字数限制
3. 评论内容需要敏感词过滤
4. 点赞需要防抖处理
5. 长按评论显示操作菜单

### 3.6 安全规范
1. 所有请求需要携带 token
2. 敏感信息需要加密处理
3. 防止 XSS 攻击
4. 防止 SQL 注入
5. 防止 CSRF 攻击

### 3.7 代码规范
1. 使用 ESLint 进行代码检查
2. 使用 Prettier 进行代码格式化
3. 遵循 Vue 风格指南
4. 使用 TypeScript 类型定义
5. 编写单元测试

### 3.8 文档规范
1. 接口文档需要及时更新
2. 代码需要添加注释
3. 组件需要说明使用方法
4. 需要记录更新日志
5. 需要编写开发文档 