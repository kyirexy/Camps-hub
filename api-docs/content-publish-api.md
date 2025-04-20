## 1.3 帖子详情接口

### 接口说明
获取指定帖子的完整详情信息，包含基础信息、关联话题、附件列表和悬赏状态

**请求方式**  
`GET`

**请求路径**  
`/api/posts/{postId}`

**路径参数**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|-----|
| postId | Long | 是  | 帖子ID |

**响应字段说明**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "postId": 123,
    "title": "求购二手教材",
    "content": "求购数据结构教材...",
    "postType": "bounty",
    "viewCount": 150,
    "likeCount": 30,
    "createdAt": "2024-04-10 14:30:00",
    "topics": [
      {"topicId": 1, "topicName": "教材交易"}
    ],
    "attachments": [
      {"fileId": 1, "fileUrl": "https://example.com/1.jpg"}
    ],
    "bountyInfo": {
      "bountyAmount": 50.00,
      "emergencyLevel": "high",
      "status": "open"
    }
  }
}
```

**状态码说明**
| 状态码 | 说明 |
|--------|-----|
| 200 | 请求成功 |
| 404 | 帖子不存在 |
| 500 | 服务端异常 |

**调用示例**
```
GET /api/posts/123
```

**注意事项**
1. 每次调用会自动增加浏览数
2. 悬赏类帖子会返回bountyInfo字段
3. 图片附件返回可访问的完整URL