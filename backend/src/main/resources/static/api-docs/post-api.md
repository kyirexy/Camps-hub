# 帖子相关API文档

## 基础信息

- **基础URL**: `http://localhost:8081/api/posts`
- **认证方式**: JWT Token (需要在请求头中添加 `Authorization: Bearer {token}`)
- **响应格式**: JSON

## 通用响应格式

```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": {
    // 具体数据
  }
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## API列表

### 1. 获取帖子列表

获取帖子列表，支持滚动分页。

- **URL**: `/list`
- **方法**: GET
- **参数**:
  - `lastTime`: 最后一条记录的时间戳（首次传空）
  - `pageSize`: 每页数量，默认10

- **响应示例**:

```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": {
    "items": [
      {
        "postId": 1,
        "userId": 1001,
        "title": "寻找校园卡",
        "content": "今天在图书馆丢失了校园卡，有谁捡到了吗？",
        "categoryName": "失物招领",
        "postType": "NORMAL",
        "bountyAmount": null,
        "emergencyLevel": 0,
        "viewCount": 10,
        "likeCount": 2,
        "commentCount": 3,
        "createdAt": "2024-04-20T10:30:00",
        "username": "张三",
        "avatar": "http://localhost:8081/files/avatar1.jpg"
      }
    ],
    "hasMore": true,
    "nextTimestamp": "2024-04-20T10:30:00"
  }
}
```

### 2. 获取帖子详情

获取帖子详情，包含基础信息、话题及扩展字段。

- **URL**: `/{postId}`
- **方法**: GET
- **参数**:
  - `postId`: 帖子ID (路径参数)

- **响应示例**:

```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": {
    "post": {
      "postId": 1,
      "userId": 1001,
      "categoryId": 1,
      "title": "寻找校园卡",
      "content": "今天在图书馆丢失了校园卡，有谁捡到了吗？",
      "postType": "NORMAL",
      "bountyAmount": null,
      "bountyStatus": null,
      "emergencyLevel": 0,
      "viewCount": 10,
      "likeCount": 2,
      "commentCount": 3,
      "shareCount": 0,
      "status": "PUBLISHED",
      "createdAt": "2024-04-20T10:30:00",
      "updatedAt": "2024-04-20T10:30:00",
      "topics": []
    },
    "topics": [
      {
        "topicId": 1,
        "name": "校园卡",
        "description": "关于校园卡的话题",
        "createdAt": "2024-04-20T10:30:00"
      }
    ]
  }
}
```

### 3. 创建帖子

创建新帖子。

- **URL**: `/`
- **方法**: POST
- **请求体**:

```json
{
  "title": "寻找校园卡",
  "content": "今天在图书馆丢失了校园卡，有谁捡到了吗？",
  "postType": "NORMAL",
  "categoryId": 1,
  "images": ["base64编码的图片1", "base64编码的图片2"],
  "topicNames": ["校园卡", "图书馆"]
}
```

- **响应示例**:

```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": 1
}
```

### 4. 更新帖子

更新帖子信息。

- **URL**: `/{postId}`
- **方法**: PUT
- **参数**:
  - `postId`: 帖子ID (路径参数)
- **请求体**:

```json
{
  "title": "寻找校园卡（已找到）",
  "content": "校园卡已经找到了，谢谢大家！",
  "postType": "NORMAL",
  "categoryId": 1,
  "images": ["base64编码的图片1"],
  "topicNames": ["校园卡", "图书馆"]
}
```

- **响应示例**:

```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

### 5. 更新帖子状态

更新帖子状态。

- **URL**: `/{postId}/status`
- **方法**: PUT
- **参数**:
  - `postId`: 帖子ID (路径参数)
  - `status`: 新状态 (查询参数)

- **响应示例**:

```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

### 6. 点赞帖子

点赞帖子。

- **URL**: `/{postId}/like`
- **方法**: POST
- **参数**:
  - `postId`: 帖子ID (路径参数)

- **响应示例**:

```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

### 7. 分享帖子

分享帖子。

- **URL**: `/{postId}/share`
- **方法**: POST
- **参数**:
  - `postId`: 帖子ID (路径参数)

- **响应示例**:

```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

### 8. 删除帖子

删除帖子。

- **URL**: `/{postId}`
- **方法**: DELETE
- **参数**:
  - `postId`: 帖子ID (路径参数)

- **响应示例**:

```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

### 9. 瀑布流加载帖子列表

瀑布流加载帖子列表，支持滚动分页。

- **URL**: `/waterfall`
- **方法**: GET
- **参数**:
  - `lastTime`: 最后一条记录的时间戳（首次加载传null）
  - `limit`: 获取条数，默认10条

- **响应示例**:

```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": {
    "items": [
      {
        "postId": 1,
        "userId": 1001,
        "title": "寻找校园卡",
        "content": "今天在图书馆丢失了校园卡，有谁捡到了吗？",
        "categoryName": "失物招领",
        "postType": "NORMAL",
        "bountyAmount": null,
        "emergencyLevel": 0,
        "viewCount": 10,
        "likeCount": 2,
        "commentCount": 3,
        "createdAt": "2024-04-20T10:30:00",
        "username": "张三",
        "avatar": "http://localhost:8081/files/avatar1.jpg"
      }
    ],
    "hasMore": true,
    "nextTimestamp": "2024-04-20T10:30:00"
  }
}
```

## 数据模型

### PostVO (帖子视图对象)

```json
{
  "postId": 1,
  "userId": 1001,
  "title": "寻找校园卡",
  "content": "今天在图书馆丢失了校园卡，有谁捡到了吗？",
  "categoryName": "失物招领",
  "postType": "NORMAL",
  "bountyAmount": null,
  "emergencyLevel": 0,
  "viewCount": 10,
  "likeCount": 2,
  "commentCount": 3,
  "createdAt": "2024-04-20T10:30:00",
  "username": "张三",
  "avatar": "http://localhost:8081/files/avatar1.jpg"
}
```

### PostDetailVO (帖子详情视图对象)

```json
{
  "post": {
    "postId": 1,
    "userId": 1001,
    "categoryId": 1,
    "title": "寻找校园卡",
    "content": "今天在图书馆丢失了校园卡，有谁捡到了吗？",
    "postType": "NORMAL",
    "bountyAmount": null,
    "bountyStatus": null,
    "emergencyLevel": 0,
    "viewCount": 10,
    "likeCount": 2,
    "commentCount": 3,
    "shareCount": 0,
    "status": "PUBLISHED",
    "createdAt": "2024-04-20T10:30:00",
    "updatedAt": "2024-04-20T10:30:00",
    "topics": []
  },
  "topics": [
    {
      "topicId": 1,
      "name": "校园卡",
      "description": "关于校园卡的话题",
      "createdAt": "2024-04-20T10:30:00"
    }
  ]
}
```

### ScrollResult (滚动分页结果)

```json
{
  "items": [
    // 数据项列表
  ],
  "hasMore": true,
  "nextTimestamp": "2024-04-20T10:30:00"
}
```

## 注意事项

1. 所有请求需要在请求头中添加 `Authorization: Bearer {token}` 进行身份验证
2. 图片上传使用Base64编码，需要在请求体中包含完整的Base64字符串
3. 时间戳格式为ISO 8601标准格式
4. 分页加载时，`lastTime`参数为上一页最后一条记录的时间戳
5. 瀑布流加载时，`limit`参数控制每次加载的数量

## 删除帖子

### 接口说明
删除指定ID的帖子。用户只能删除自己发布的帖子，删除操作会同时删除帖子关联的话题和附件。

### 请求信息
- **接口地址**: `/api/posts/{postId}`
- **请求方式**: DELETE
- **接口描述**: 删除指定ID的帖子

### 请求参数
| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| postId | Long | 是 | 要删除的帖子ID | 123 |

### 请求头
| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| Authorization | String | 是 | Bearer Token | Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9... |

### 响应参数
| 参数名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| code | Integer | 响应码 | 200 |
| message | String | 响应消息 | "success" |
| data | Boolean | 删除结果 | true |

### 响应示例
```json
{
    "code": 200,
    "message": "success",
    "data": true
}
```

### 错误码说明
| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| 400 | 帖子不存在 | 检查帖子ID是否正确 |
| 403 | 无权删除此帖子 | 只能删除自己发布的帖子 |
| 500 | 删除帖子失败 | 服务器内部错误，请稍后重试 |

### 注意事项
1. 删除帖子是软删除操作，帖子状态会被更新为"deleted"
2. 删除帖子会同时删除帖子关联的话题和附件
3. 只有帖子的发布者才能删除帖子
4. 删除操作不可逆，请谨慎操作 