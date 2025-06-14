# 评论接口文档

## 概述

本文档描述了校园社区平台中评论相关的API接口。评论功能允许用户对帖子进行评论、回复其他评论、点赞评论等操作。

## 基础信息

- 基础路径: `/api/comments`
- 响应格式: JSON
- 认证方式: JWT Token (需要在请求头中携带 `Authorization: Bearer {token}`)

## 通用响应格式

所有接口都使用统一的响应格式：

```json
{
  "code": 200,       // 状态码，200表示成功
  "message": "success", // 状态信息
  "data": {}         // 响应数据，根据接口不同而不同
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 接口列表

### 1. 创建评论

创建一个新的评论。

- **URL**: `/api/comments`
- **方法**: `POST`
- **描述**: 创建一个新的评论，可以是帖子的一级评论，也可以是回复其他评论的二级评论
- **请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| postId | Long | 是 | 帖子ID |
| userId | Long | 是 | 用户ID |
| parentId | Long | 否 | 父评论ID，如果是回复其他评论，则填写父评论ID；如果是一级评论，则不填或填0 |
| content | String | 是 | 评论内容，最大长度500字符 |

- **请求示例**:

```json
{
  "postId": 123,
  "userId": 456,
  "parentId": 0,
  "content": "这是一条评论内容"
}
```

- **响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": 789  // 返回新创建的评论ID
}
```

### 2. 获取评论详情

获取单个评论的详细信息。

- **URL**: `/api/comments/{commentId}`
- **方法**: `GET`
- **描述**: 根据评论ID获取评论的详细信息
- **路径参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| commentId | Long | 评论ID |

- **响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "commentId": 789,
    "postId": 123,
    "userId": 456,
    "parentId": 0,
    "content": "这是一条评论内容",
    "likeCount": 0,
    "status": "normal",
    "createdAt": "2024-04-21T10:30:00Z"
  }
}
```

### 3. 获取帖子评论列表

获取指定帖子的所有评论，包括一级评论和二级评论。

- **URL**: `/api/comments/post/{postId}`
- **方法**: `GET`
- **描述**: 获取指定帖子的所有评论，一级评论按时间倒序排列，二级评论按时间正序排列
- **路径参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| postId | Long | 帖子ID |

- **响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "commentId": 789,
      "postId": 123,
      "userId": 456,
      "parentId": 0,
      "content": "这是一条一级评论",
      "likeCount": 5,
      "createdAt": "2024-04-21T10:30:00Z",
      "username": "张三",
      "avatar": "https://example.com/avatars/456.jpg",
      "children": [
        {
          "commentId": 790,
          "postId": 123,
          "userId": 457,
          "parentId": 789,
          "content": "这是一条回复评论",
          "likeCount": 2,
          "createdAt": "2024-04-21T10:35:00Z",
          "username": "李四",
          "avatar": "https://example.com/avatars/457.jpg",
          "children": []
        }
      ]
    },
    {
      "commentId": 791,
      "postId": 123,
      "userId": 458,
      "parentId": 0,
      "content": "这是另一条一级评论",
      "likeCount": 1,
      "createdAt": "2024-04-21T09:30:00Z",
      "username": "王五",
      "avatar": "https://example.com/avatars/458.jpg",
      "children": []
    }
  ]
}
```

### 4. 获取用户评论列表

获取指定用户发表的所有评论。

- **URL**: `/api/comments/user/{userId}`
- **方法**: `GET`
- **描述**: 获取指定用户发表的所有评论，按时间倒序排列
- **路径参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| userId | Long | 用户ID |

- **查询参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| page | Integer | 否 | 页码，默认为1 |
| pageSize | Integer | 否 | 每页大小，默认为10 |

- **响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "commentId": 789,
      "postId": 123,
      "userId": 456,
      "parentId": 0,
      "content": "这是一条评论",
      "likeCount": 5,
      "createdAt": "2024-04-21T10:30:00Z",
      "username": "张三",
      "avatar": "https://example.com/avatars/456.jpg",
      "children": []
    },
    {
      "commentId": 790,
      "postId": 124,
      "userId": 456,
      "parentId": 0,
      "content": "这是另一条评论",
      "likeCount": 2,
      "createdAt": "2024-04-21T09:30:00Z",
      "username": "张三",
      "avatar": "https://example.com/avatars/456.jpg",
      "children": []
    }
  ]
}
```

### 5. 更新评论

更新指定评论的内容。

- **URL**: `/api/comments/{commentId}`
- **方法**: `PUT`
- **描述**: 更新指定评论的内容
- **路径参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| commentId | Long | 评论ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| content | String | 是 | 新的评论内容，最大长度500字符 |

- **请求示例**:

```json
{
  "content": "这是更新后的评论内容"
}
```

- **响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true  // 更新成功返回true
}
```

### 6. 删除评论

删除指定的评论（软删除）。

- **URL**: `/api/comments/{commentId}`
- **方法**: `DELETE`
- **描述**: 删除指定的评论，执行软删除操作
- **路径参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| commentId | Long | 评论ID |

- **响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true  // 删除成功返回true
}
```

### 7. 点赞评论

对指定评论进行点赞。

- **URL**: `/api/comments/{commentId}/like`
- **方法**: `POST`
- **描述**: 对指定评论进行点赞，增加评论的点赞数
- **路径参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| commentId | Long | 评论ID |

- **响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true  // 点赞成功返回true
}
```

## 注意事项

1. 评论内容长度限制为500字符
2. 评论支持两级结构：一级评论（parentId为0或null）和二级评论（parentId为一级评论的ID）
3. 删除评论为软删除，不会真正从数据库中删除记录
4. 点赞评论会增加评论的likeCount字段值
5. 创建评论后会自动更新帖子的commentCount字段值
6. 删除评论后会自动减少帖子的commentCount字段值

## 前端实现建议

1. 评论列表展示时，建议使用树形结构展示一级评论和二级评论
2. 评论输入框可以使用富文本编辑器，支持表情、图片等
3. 评论列表支持分页加载，可以使用无限滚动或分页按钮
4. 评论操作（点赞、删除等）可以使用图标按钮，并添加操作确认提示
5. 评论时间显示可以使用相对时间格式，如"5分钟前"、"2小时前"等 