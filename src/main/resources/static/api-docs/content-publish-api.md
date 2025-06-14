# 内容发布功能API文档

本文档详细描述了Campus-hub平台内容发布功能的API接口，包括富文本编辑、多附件上传、分类选择、话题标注和地理位置标记等功能，供前端开发人员参考使用。

## 接口基本信息

- 基础路径: `/posts`
- 所有请求需要携带认证信息: `Authorization: Bearer {token}`
- 所有接口均需要用户登录认证

## 1. 帖子管理接口

### 1.1 创建新帖子

创建一个新的帖子，支持富文本内容、图片上传和话题标注。

#### 请求信息

- 请求方法: `POST`
- 请求URL: `/posts`
- 请求头:
  ```
  Authorization: Bearer {token}
  Content-Type: application/json
  ```
- 请求体:
  ```json
  {
    "title": "帖子标题",        // 必填
    "content": "<p>这是富文本内容</p>", // 必填
    "userId": 1001,           // 必填
    "postType": "normal",     // 必填，可选值：normal普通/bounty悬赏/lost失物/trade交易
    "images": [               // 可选，base64编码的图片列表
      "data:image/jpeg;base64,/9j/4AAQSkZJRg...",
      "data:image/jpeg;base64,/9j/4AAQSkZJRg..."
    ],
    "topicNames": [          // 可选，话题名称列表
      "校园生活",
      "学习交流"
    ]
  }
  ```

#### 响应信息

- 成功响应 (状态码: 200)
  ```json
  {
    "code": 200,
    "message": "success",
    "data": 123  // 返回帖子ID
  }
  ```

- 错误响应
  - 400: 缺少必要参数
  - 401: 未授权
  - 500: 服务器内部错误

### 1.2 更新帖子信息

更新已有帖子的信息。

#### 请求信息

- 请求方法: `PUT`
- 请求URL: `/posts/{postId}`
- 请求头:
  ```
  Authorization: Bearer {token}
  Content-Type: application/json
  ```
- 路径参数:
  - `postId`: 帖子ID
- 请求体:
  ```json
  {
    "title": "更新后的标题",      // 必填
    "content": "<p>更新后的内容</p>", // 必填
    "images": [               // 可选，如果提供则会替换原有图片
      "data:image/jpeg;base64,/9j/4AAQSkZJRg..."
    ],
    "topicNames": [          // 可选，如果提供则会替换原有话题
      "校园生活",
      "活动分享"
    ]
  }
  ```

#### 响应信息

- 成功响应 (状态码: 200)
  ```json
  {
    "code": 200,
    "message": "success",
    "data": true
  }
  ```

- 错误响应
  - 400: 缺少必要参数
  - 401: 未授权
  - 404: 帖子不存在
  - 500: 服务器内部错误

### 1.3 获取帖子详情

#### 请求信息

- 请求方法: `GET`
- 请求URL: `/api/posts/{postId}`
- 请求头:
  ```
  Authorization: Bearer {token}
  ```
- 路径参数:
  | 参数名称 | 类型   | 必填 | 说明       |
  |----------|--------|------|------------|
  | postId   | Long   | 是   | 帖子唯一标识 |

#### 响应示例
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "post": {
      "postId": 123,
      "title": "图书馆失物招领",
      "content": "在二楼阅览室捡到黑色钱包",
      "postType": "LOST",
      "viewCount": 150,
      "likeCount": 30,
      "bountyStatus": "OPEN",
      "bountyAmount": 50.00,
      "emergencyLevel": 2,
      "createTime": "2024-04-10 15:30:00"
    },
    "topics": [
      {"topicId": 1, "topicName": "失物招领"}
    ],
    "attachments": [
      {"url": "/attachments/wallet.jpg"}
    ],
    "lostFound": {
      "foundTime": null
    }
  }
}
```

#### 字段说明
1. postType枚举值：
   - `LOST`: 失物招领
   - "BOUNTY": 悬赏任务

2. bountyStatus状态流转：
   - `OPEN` → `IN_PROGRESS`（进行中）
   - `IN_PROGRESS` → `CLOSED`（已关闭）
   - `CLOSED` → `REOPENED`（重新开启）

3. emergencyLevel紧急程度：
   - 1: 普通（48小时内处理）
   - 2: 紧急（24小时内处理）
   - 3: 特急（6小时内处理）

#### 错误状态码
| 状态码 | 说明                 |
|--------|----------------------|
| 401    | 未授权访问           |
| 404    | 帖子不存在           |
| 500    | 服务器内部错误       |
      "topics": [
        {
          "topicId": 789,
          "topicName": "校园生活"
        }
      ],
      "attachments": [
        {
          "fileId": 456,
          "fileType": "image",
          "fileUrl": "http://localhost:8081/files/image1.jpg",
          "thumbnailUrl": "http://localhost:8081/files/thumb_image1.jpg",
          "fileSize": 1024
        }
      ],
      "lostFound": {  // 仅当postType为"lost"时存在
        "postId": 123,
        "itemName": "蓝色钱包",
        "itemFeature": "皮质，内有学生证",
        "lostTime": "2024-04-09T15:30:00",
        "foundTime": null,
        "locationDetail": "图书馆二楼自习室"
      }
    }
  }
  ```

- 错误响应
  - 401: 未授权
  - 404: 帖子不存在
  - 500: 服务器内部错误

### 1.4 获取帖子列表

分页获取帖子列表。

#### 请求信息

- 请求方法: `GET`
- 请求URL: `/posts`
- 请求头:
  ```
  Authorization: Bearer {token}
  ```
- 查询参数:
  - `pageNum`: 页码，默认1
  - `pageSize`: 每页数量，默认10

#### 响应信息

- 成功响应 (状态码: 200)
  ```json
  {
    "code": 200,
    "message": "success",
    "data": [
      {
        "postId": 123,
        "userId": 1001,
        "title": "帖子标题",
        "content": "<p>帖子内容</p>",
        "postType": "normal",
        "viewCount": 10,
        "likeCount": 5,
        "commentCount": 3,
        "shareCount": 1,
        "status": "published",
        "createdAt": "2024-04-10T12:00:00",
        "updatedAt": "2024-04-10T13:00:00"
      }
    ]
  }
  ```

- 错误响应
  - 401: 未授权
  - 500: 服务器内部错误

### 1.5 更新帖子状态

更新帖子的状态。

#### 请求信息

- 请求方法: `PUT`
- 请求URL: `/posts/{postId}/status`
- 请求头:
  ```
  Authorization: Bearer {token}
  ```
- 路径参数:
  - `postId`: 帖子ID
- 查询参数:
  - `status`: 新状态

#### 响应信息

- 成功响应 (状态码: 200)
  ```json
  {
    "code": 200,
    "message": "success",
    "data": true
  }
  ```

- 错误响应
  - 401: 未授权
  - 404: 帖子不存在
  - 500: 服务器内部错误

### 1.6 点赞帖子

对帖子进行点赞。

#### 请求信息

- 请求方法: `POST`
- 请求URL: `/posts/{postId}/like`
- 请求头:
  ```
  Authorization: Bearer {token}
  ```
- 路径参数:
  - `postId`: 帖子ID

#### 响应信息

- 成功响应 (状态码: 200)
  ```json
  {
    "code": 200,
    "message": "success",
    "data": true
  }
  ```

- 错误响应
  - 401: 未授权
  - 404: 帖子不存在
  - 500: 服务器内部错误

### 1.7 分享帖子

分享帖子。

#### 请求信息

- 请求方法: `POST`
- 请求URL: `/posts/{postId}/share`
- 请求头:
  ```
  Authorization: Bearer {token}
  ```
- 路径参数:
  - `postId`: 帖子ID

#### 响应信息

- 成功响应 (状态码: 200)
  ```json
  {
    "code": 200,
    "message": "success",
    "data": true
  }
  ```

- 错误响应
  - 401: 未授权
  - 404: 帖子不存在
  - 500: 服务器内部错误

### 1.8 搜索帖子

根据关键词搜索帖子。

#### 请求信息

- 请求方法: `GET`
- 请求URL: `/posts/search`
- 请求头:
  ```
  Authorization: Bearer {token}
  ```
- 查询参数:
  - `keyword`: 搜索关键词
  - `pageNum`: 页码，默认1
  - `pageSize`: 每页数量，默认10

#### 响应信息

- 成功响应 (状态码: 200)
  ```json
  {
    "code": 200,
    "message": "success",
    "data": [
      {
        "postId": 123,
        "userId": 1001,
        "title": "帖子标题",
        "content": "<p>帖子内容</p>",
        "postType": "normal",
        "viewCount": 10,
        "likeCount": 5,
        "commentCount": 3,
        "shareCount": 1,
        "status": "published",
        "createdAt": "2024-04-10T12:00:00",
        "updatedAt": "2024-04-10T13:00:00"
      }
    ]
  }
  ```

- 错误响应
  - 401: 未授权
  - 500: 服务器内部错误

## 2. 附件管理接口

### 2.1 上传单个文件

上传单个文件作为帖子的附件。

#### 请求信息

- 请求方法: `POST`
- 请求URL: `/api/attachments/upload`
- 请求头:
  ```
  Authorization: Bearer {token}
  Content-Type: multipart/form-data
  ```
- 请求参数:
  - 表单参数:
    - `file`: 文件数据，必填
    - `postId`: 关联的帖子ID，可选，类型为Long

#### 响应信息

- 成功响应 (状态码: 200)
  ```json
  {
    "code": 200,
    "message": "success",
    "data": 456  // 返回文件ID，数字类型
  }
  ```

- 错误响应
  - 400: 请求参数错误或文件格式不支持
  - 401: 未授权
  - 413: 文件过大
  - 500: 服务器内部错误

### 2.2 批量上传文件

批量上传多个文件作为帖子的附件。

#### 请求信息

- 请求方法: `POST`
- 请求URL: `/api/attachments/batch-upload`
- 请求头:
  ```
  Authorization: Bearer {token}
  Content-Type: multipart/form-data
  ```
- 请求参数:
  - 表单参数:
    - `files`: 文件数据列表，必填，支持多文件
    - `postId`: 关联的帖子ID，可选，类型为Long

#### 响应信息

- 成功响应 (状态码: 200)
  ```json
  {
    "code": 200,
    "message": "success",
    "data": [456, 457, 458]  // 返回文件ID列表，数组类型
  }
  ```

- 错误响应
  - 400: 请求参数错误或文件格式不支持
  - 401: 未授权
  - 413: 文件过大
  - 500: 服务器内部错误

### 2.3 获取帖子相关的所有附件

获取指定帖子关联的所有附件信息。

#### 请求信息

- 请求方法: `GET`
- 请求URL: `/api/attachments/post/{postId}`
- 请求头:
  ```
  Authorization: Bearer {token}
  ```
- 路径参数:
  - `postId`: 帖子ID，类型为Long

#### 响应信息

- 成功响应 (状态码: 200)
  ```json
  {
    "code": 200,
    "message": "success",
    "data": [
      {
        "fileId": 456,           // 文件ID
        "postId": 123,           // 关联帖子ID
        "fileType": "image",     // 文件类型
        "fileUrl": "http://localhost:8081/files/image1.jpg", // 文件URL
        "thumbnailUrl": "http://localhost:8081/files/thumb_image1.jpg", // 缩略图URL
        "fileSize": 1024,        // 文件大小(KB)
        "uploadTime": "2024-04-10T12:30:00" // 上传时间
      },
      {
        "fileId": 457,           // 文件ID
        "postId": 123,           // 关联帖子ID
        "fileType": "pdf",       // 文件类型
        "fileUrl": "http://localhost:8081/files/document.pdf", // 文件URL
        "thumbnailUrl": null,     // 缩略图URL
        "fileSize": 2048,        // 文件大小(KB)
        "uploadTime": "2024-04-10T12:35:00" // 上传时间
      }
    ]
  }
  ```

- 错误响应
  - 401: 未授权
  - 404: 帖子不存在
  - 500: 服务器内部错误

## 3. 话题管理接口

### 3.1 创建新话题

创建一个新的话题标签。

#### 请求信息

- 请求方法: `POST`
- 请求URL: `/api/topics`
- 请求头:
  ```
  Authorization: Bearer {token}
  Content-Type: application/json
  ```
- 请求体:
  ```json
  {
    "topicName": "校园生活",     // 话题名称，必填，不含#
    "creatorId": 1001          // 创建者ID，必填
  }
  ```

#### 响应信息

- 成功响应 (状态码: 200)
  ```json
  {
    "code": 200,
    "message": "success",
    "data": 789  // 返回话题ID，数字类型
  }
  ```

- 错误响应
  - 400: 请求参数错误
  - 401: 未授权
  - 409: 话题已存在
  - 500: 服务器内部错误

### 3.2 根据名称获取话题

根据话题名称获取话题信息。

#### 请求信息

- 请求方法: `GET`
- 请求URL: `/api/topics/name/{topicName}`
- 请求头:
  ```
  Authorization: Bearer {token}
  ```
- 路径参数:
  - `topicName`: 话题名称，字符串类型

#### 响应信息

- 成功响应 (状态码: 200)
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "topicId": 789,           // 话题ID
      "topicName": "校园生活",   // 话题名称
      "creatorId": 1001,        // 创建者ID
      "usageCount": 15,         // 使用次数
      "createdAt": "2024-04-01T10:00:00" // 创建时间
    }
  }
  ```

- 错误响应
  - 401: 未授权
  - 404: 话题不存在
  - 500: 服务器内部错误

### 3.3 获取热门话题列表

获取使用频率最高的热门话题列表。

#### 请求信息

- 请求方法: `GET`
- 请求URL: `/api/topics/hot`
- 请求头:
  ```
  Authorization: Bearer {token}
  ```
- 查询参数:
  - `limit`: 返回数量限制，默认10，整数类型

#### 响应信息

- 成功响应 (状态码: 200)
  ```json
  {
    "code": 200,
    "message": "success",
    "data": [
      {
        "topicId": 789,           // 话题ID
        "topicName": "校园生活",   // 话题名称
        "creatorId": 1001,        // 创建者ID
        "usageCount": 15,         // 使用次数
        "createdAt": "2024-04-01T10:00:00" // 创建时间
      },
      {
        "topicId": 790,           // 话题ID
        "topicName": "学习交流",   // 话题名称
        "creatorId": 1002,        // 创建者ID
        "usageCount": 12,         // 使用次数
        "createdAt": "2024-04-02T11:00:00" // 创建时间
      }
    ]
  }
  ```

- 错误响应
  - 401: 未授权
  - 500: 服务器内部错误

## 4. 失物招领接口

### 4.1 创建失物招领记录

创建一个新的失物招领记录，关联到特定帖子。

#### 请求信息

- 请求方法: `POST`
- 请求URL: `/api/lost-found`
- 请求头:
  ```
  Authorization: Bearer {token}
  Content-Type: application/json
  ```
- 请求体:
  ```json
  {
    "postId": 123,              // 关联帖子ID，必填
    "itemName": "蓝色钱包",     // 物品名称，必填
    "itemFeature": "皮质，内有学生证", // 物品特征描述，必填
    "lostTime": "2024-04-09T15:30:00", // 丢失/发现时间，必填
    "locationDetail": "图书馆二楼自习室", // 详细位置描述，必填
    "rewardType": "cash"       // 酬谢方式，可选值：cash现金/gift物品/points积分
  }
  ```

#### 响应信息

- 成功响应 (状态码: 200)
  ```json
  {
    "code": 200,
    "message": "success",
    "data": true  // 创建成功返回true
  }
  ```

- 错误响应
  - 400: 请求参数错误
  - 401: 未授权
  - 404: 关联帖子不存在
  - 500: 服务器内部错误

### 4.2 更新失物招领信息

更新已有的失物招领记录信息。

#### 请求信息

- 请求方法: `PUT`
- 请求URL: `/api/lost-found/{postId}`
- 请求头:
  ```
  Authorization: Bearer {token}
  Content-Type: application/json
  ```
- 路径参数:
  - `postId`: 关联的帖子ID，类型为Long
- 请求体:
  ```json
  {
    "itemName": "黑色钱包",     // 物品名称
    "itemFeature": "皮质，内有学生证和现金", // 物品特征描述
    "lostTime": "2024-04-09T16:00:00", // 丢失/发现时间
    "locationDetail": "图书馆三楼自习室", // 详细位置描述
    "rewardType": "gift"       // 酬谢方式
  }
  ```

#### 响应信息

- 成功响应 (状态码: 200)
  ```json
  {
    "code": 200,
    "message": "success",
    "data": true  // 更新成功返回true
  }
  ```

- 错误响应
  - 400: 请求参数错误
  - 401: 未授权
  - 404: 失物招领记录不存在
  - 500: 服务器内部错误

### 4.3 获取失物招领详情

获取指定失物招领记录的详细信息。

#### 请求信息

- 请求方法: `GET`
- 请求URL: `/api/lost-found/{postId}`
- 请求头:
  ```
  Authorization: Bearer {token}
  ```
- 路径参数:
  - `postId`: 关联的帖子ID，类型为Long

#### 响应信息

- 成功响应 (状态码: 200)
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "postId": 123,              // 关联帖子ID
      "itemName": "蓝色钱包",     // 物品名称
      "itemFeature": "皮质，内有学生证", // 物品特征描述
      "lostTime": "2024-04-09T15:30:00", // 丢失/发现时间
      "foundTime": null,          // 实际找回时间
      "locationDetail": "图书馆二楼自习室", // 详细位置描述
      "rewardType": "cash"       // 酬谢方式
    }
  }
  ```

- 错误响应
  - 401: 未授权
  - 404: 失物招领记录不存在
  - 500: 服务器内部错误

### 4.4 更新找到时间

更新失物招领记录的找到时间，标记为已找回。

#### 请求信息

- 请求方法: `PUT`
- 请求URL: `/api/lost-found/{postId}/found`
- 请求头:
  ```
  Authorization: Bearer {token}
  ```
- 路径参数:
  - `postId`: 关联的帖子ID，类型为Long

#### 响应信息

- 成功响应 (状态码: 200)
  ```json
  {
    "code": 200,
    "message": "success",
    "data": true  // 更新成功返回true
  }
  ```

- 错误响应
  - 401: 未授权
  - 404: 失物招领记录不存在
  - 500: 服务器内部错误

## 5. 文件上传配置

### 5.1 文件上传限制

- 支持的文件类型:
  - 图片: jpg, jpeg, png, gif, webp
  - 文档: pdf, doc, docx, ppt, pptx, xls, xlsx, txt
  - 视频: mp4, avi, mov, wmv
  - 音频: mp3, wav, ogg

- 文件大小限制:
  - 图片: 最大10MB
  - 文档: 最大20MB
  - 视频: 最大50MB
  - 音频: 最大20MB

### 5.2 文件存储路径

- 文件上传路径: `D:/develop/camps/uploads`
- 文件访问URL前缀: `http://localhost:8081/files`

## 6. 测试示例

### 6.1 创建帖子示例

#### 请求

```http
POST /api/posts HTTP/1.1
Host: localhost:8081
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW

------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="post"

{"userId":1001,"categoryId":2,"title":"校园活动分享","content":"<p>今天参加了一个很有意思的校园活动，分享给大家！</p><p>活动内容：...</p>","postType":"normal","emergencyLevel":0,"location":"116.404,39.915","status":"published"}
------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="files"; filename="activity.jpg"
Content-Type: image/jpeg

(二进制文件内容)
------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="topicNames"

校园生活
------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="topicNames"

活动分享
------WebKitFormBoundary7MA4YWxkTrZu0gW--
```

#### 响应

```json
{
  "code": 200,
  "message": "success",
  "data": 123
}
```

### 6.2 创建失物招领示例

#### 请求

```http
POST /api/posts HTTP/1.1
Host: localhost:8081
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW

------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="post"

{"userId":1001,"categoryId":5,"title":"寻找丢失的蓝色钱包","content":"<p>在图书馆二楼自习室丢失了一个蓝色钱包，内有学生证，急需找回！</p>","postType":"lost","emergencyLevel":3,"location":"116.350,39.930","status":"published"}
------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="files"; filename="wallet.jpg"
Content-Type: image/jpeg

(二进制文件内容)
------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="topicNames"

失物招领
------WebKitFormBoundary7MA4YWxkTrZu0gW--
```

#### 响应

```json
{
  "code": 200,
  "message": "success",
  "data": 124
}
```

#### 创建失物招领详情

```http
POST /api/lost-found HTTP/1.1
Host: localhost:8081
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "postId": 124,
  "itemName": "蓝色钱包",
  "itemFeature": "皮质，内有学生证",
  "lostTime": "2024-04-09T15:30:00",
  "locationDetail": "图书馆二楼自习室",
  "rewardType": "cash"
}
```

#### 响应

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```