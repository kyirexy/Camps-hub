# 热点帖子API文档

## 概述

本文档描述了校园社区平台的热点帖子功能相关接口。热点帖子功能通过计算帖子的热度值，为用户展示最受欢迎的内容，包括普通热点帖子和突发热点帖子。

## 接口列表

### 1. 获取热点帖子列表

获取按热度排序的帖子列表。

#### 请求信息

- **接口URL**: `/api/posts/hot`
- **请求方式**: GET
- **接口描述**: 获取热点帖子列表，按热度值降序排序
- **权限要求**: 无需登录，公开接口

#### 请求参数

| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|-------|------|------|--------|------|
| limit | Integer | 否 | 10 | 获取的帖子数量，默认10条 |

#### 响应结果

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "postId": 12,
      "userId": 1,
      "username": "张三",
      "avatar": "https://example.com/avatar.jpg",
      "categoryId": 1,
      "categoryName": "校园生活",
      "title": "校园美食推荐",
      "content": "今天发现了一家超级好吃的餐厅...",
      "postType": "NORMAL",
      "bountyAmount": null,
      "bountyStatus": null,
      "emergencyLevel": 0,
      "viewCount": 10,
      "likeCount": 5,
      "commentCount": 3,
      "shareCount": 2,
      "status": "PUBLISHED",
      "createdAt": "2024-04-25T10:30:00",
      "updatedAt": "2024-04-25T10:30:00",
      "hotness": 0.4664898906799226,
      "isNew": false,
      "isBurst": false,
      "rank": 1
    },
    // 更多帖子...
  ]
}
```

#### 响应字段说明

| 字段名 | 类型 | 描述 |
|-------|------|------|
| postId | Long | 帖子ID |
| userId | Long | 发布者用户ID |
| username | String | 发布者用户名 |
| avatar | String | 发布者头像URL |
| categoryId | Integer | 分类ID |
| categoryName | String | 分类名称 |
| title | String | 帖子标题 |
| content | String | 帖子内容 |
| postType | String | 帖子类型，NORMAL-普通帖子，BOUNTY-悬赏帖子 |
| bountyAmount | Decimal | 悬赏金额，仅当postType为BOUNTY时有效 |
| bountyStatus | String | 悬赏状态，OPEN-待接单，PROCESSING-进行中，COMPLETED-已完成 |
| emergencyLevel | Integer | 紧急程度（0-5级，0为普通） |
| viewCount | Integer | 浏览量 |
| likeCount | Integer | 点赞数 |
| commentCount | Integer | 评论数 |
| shareCount | Integer | 分享数 |
| status | String | 帖子状态，PUBLISHED-已发布，DRAFT-草稿，CLOSED-已关闭，DELETED-已删除 |
| createdAt | String | 创建时间，格式：yyyy-MM-ddTHH:mm:ss |
| updatedAt | String | 更新时间，格式：yyyy-MM-ddTHH:mm:ss |
| hotness | Double | 热度值，越高表示越热门 |
| isNew | Boolean | 是否为新发布（24小时内） |
| isBurst | Boolean | 是否为突发热点 |
| rank | Integer | 热度排名 |

### 2. 获取热点帖子列表（带分页）

获取按热度排序的帖子列表，支持分页。

#### 请求信息

- **接口URL**: `/api/posts/hot/page`
- **请求方式**: GET
- **接口描述**: 获取热点帖子列表，支持分页，按热度值降序排序
- **权限要求**: 无需登录，公开接口

#### 请求参数

| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|-------|------|------|--------|------|
| page | Integer | 否 | 1 | 页码，从1开始 |
| pageSize | Integer | 否 | 10 | 每页大小，默认10条 |

#### 响应结果

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "postId": 12,
        "userId": 1,
        "username": "张三",
        "avatar": "https://example.com/avatar.jpg",
        "categoryId": 1,
        "categoryName": "校园生活",
        "title": "校园美食推荐",
        "content": "今天发现了一家超级好吃的餐厅...",
        "postType": "NORMAL",
        "bountyAmount": null,
        "bountyStatus": null,
        "emergencyLevel": 0,
        "viewCount": 10,
        "likeCount": 5,
        "commentCount": 3,
        "shareCount": 2,
        "status": "PUBLISHED",
        "createdAt": "2024-04-25T10:30:00",
        "updatedAt": "2024-04-25T10:30:00",
        "hotness": 0.4664898906799226,
        "isNew": false,
        "isBurst": false,
        "rank": 1
      },
      // 更多帖子...
    ],
    "total": 7,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

#### 响应字段说明

| 字段名 | 类型 | 描述 |
|-------|------|------|
| records | Array | 帖子列表，每个帖子字段同上一接口 |
| total | Integer | 总记录数 |
| size | Integer | 每页大小 |
| current | Integer | 当前页码 |
| pages | Integer | 总页数 |

### 3. 获取突发热点帖子列表

获取突发热点帖子列表。

#### 请求信息

- **接口URL**: `/api/posts/hot/burst`
- **请求方式**: GET
- **接口描述**: 获取突发热点帖子列表，按热度值降序排序
- **权限要求**: 无需登录，公开接口

#### 请求参数

| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|-------|------|------|--------|------|
| limit | Integer | 否 | 5 | 获取的帖子数量，默认5条 |

#### 响应结果

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "postId": 10,
      "userId": 2,
      "username": "李四",
      "avatar": "https://example.com/avatar.jpg",
      "categoryId": 2,
      "categoryName": "校园活动",
      "title": "校园歌手大赛报名开始",
      "content": "第十届校园歌手大赛报名开始啦...",
      "postType": "NORMAL",
      "bountyAmount": null,
      "bountyStatus": null,
      "emergencyLevel": 0,
      "viewCount": 50,
      "likeCount": 30,
      "commentCount": 20,
      "shareCount": 10,
      "status": "PUBLISHED",
      "createdAt": "2024-04-26T08:15:00",
      "updatedAt": "2024-04-26T08:15:00",
      "hotness": 0.0733696212268513,
      "isNew": true,
      "isBurst": true,
      "rank": 1
    },
    // 更多帖子...
  ]
}
```

#### 响应字段说明

响应字段同第一个接口。

## 热度计算说明

热点帖子的热度值通过以下因素计算：

1. **基础指标**：
   - 浏览量（权重：0.2）
   - 点赞量（权重：0.3）
   - 评论量（权重：0.25）
   - 分享量（权重：0.1）
   - 收藏量（权重：0.15）

2. **时间衰减**：
   - 帖子发布时间越久，热度值越低
   - 衰减系数：0.2

3. **突发系数**：
   - 当帖子在短时间内（如1小时内）评论量超过阈值（100条）时，会触发突发系数
   - 突发系数倍数：1.5

## 前端展示建议

1. **热点标记**：
   - 在帖子列表或详情页面，为热度值高的帖子添加"热门"标记
   - 为新发布（24小时内）的帖子添加"新"标记
   - 为突发热点帖子添加"爆"标记

2. **热度值展示**：
   - 可以在帖子卡片上显示热度值或热度排名
   - 使用不同颜色或图标表示热度等级（如：🔥🔥🔥）

3. **突发热点专区**：
   - 在首页或社区页面设置"突发热点"专区，展示突发热点帖子
   - 可以使用轮播或特殊样式突出显示

4. **热度趋势**：
   - 可以展示帖子热度变化趋势（上升、下降或稳定）
   - 使用箭头或图表直观展示

## 错误码说明

| 错误码 | 描述 |
|-------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 500 | 服务器内部错误 |

## 示例代码

### 获取热点帖子列表

```javascript
// 使用fetch API
fetch('/api/posts/hot?limit=10')
  .then(response => response.json())
  .then(data => {
    if (data.code === 200) {
      const hotPosts = data.data;
      // 处理热点帖子数据
      renderHotPosts(hotPosts);
    } else {
      console.error('获取热点帖子失败:', data.message);
    }
  })
  .catch(error => {
    console.error('请求失败:', error);
  });

// 使用axios
axios.get('/api/posts/hot', {
  params: {
    limit: 10
  }
})
.then(response => {
  if (response.data.code === 200) {
    const hotPosts = response.data.data;
    // 处理热点帖子数据
    renderHotPosts(hotPosts);
  } else {
    console.error('获取热点帖子失败:', response.data.message);
  }
})
.catch(error => {
  console.error('请求失败:', error);
});
```

### 获取突发热点帖子

```javascript
// 使用fetch API
fetch('/api/posts/hot/burst?limit=5')
  .then(response => response.json())
  .then(data => {
    if (data.code === 200) {
      const burstPosts = data.data;
      // 处理突发热点帖子数据
      renderBurstPosts(burstPosts);
    } else {
      console.error('获取突发热点帖子失败:', data.message);
    }
  })
  .catch(error => {
    console.error('请求失败:', error);
  });
``` 