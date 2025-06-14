    # 用户帖子列表 API

    ## 获取用户发布的帖子列表

    ### 请求信息

    - **请求方法**: GET
    - **请求路径**: `/api/posts/my`
    - **认证要求**: 需要用户登录

    ### 请求参数

    | 参数名 | 类型 | 必填 | 默认值 | 描述 |
    |--------|------|------|--------|------|
    | page | Integer | 否 | 1 | 页码，从1开始 |
    | pageSize | Integer | 否 | 10 | 每页记录数 |

    ### 响应信息

    #### 成功响应 (200 OK)

    ```json
    {
        "code": 200,
        "message": "success",
        "data": {
            "items": [
                {
                    "id": 123,
                    "title": "帖子标题",
                    "content": "帖子内容",
                    "userId": 456,
                    "createTime": "2024-04-21 12:00:00",
                    "updateTime": "2024-04-21 12:00:00",
                    "status": 1,
                    "authorName": "用户名",
                    "authorAvatar": "用户头像URL",
                    "commentCount": 10,
                    "likeCount": 20
                }
            ],
            "hasMore": true,
            "nextTimestamp": "2024-04-21 12:00:00"
        }
    }
    ```

    #### 响应字段说明

    - `items`: 帖子列表数组
    - `id`: 帖子ID
    - `title`: 帖子标题
    - `content`: 帖子内容
    - `userId`: 发布者用户ID
    - `createTime`: 创建时间
    - `updateTime`: 更新时间
    - `status`: 帖子状态（1: 正常）
    - `authorName`: 作者用户名
    - `authorAvatar`: 作者头像URL
    - `commentCount`: 评论数
    - `likeCount`: 点赞数
    - `hasMore`: 是否还有更多数据
    - `nextTimestamp`: 下一页的时间戳

    #### 错误响应

    ```json
    {
        "code": 401,
        "message": "未登录",
        "data": null
    }
    ```

    ```json
    {
        "code": 500,
        "message": "获取帖子列表失败: 具体错误信息",
        "data": null
    }
    ```

    ### 错误码说明

    | 错误码 | 描述 |
    |--------|------|
    | 401 | 未登录或登录已过期 |
    | 500 | 服务器内部错误 |

    ### 示例

    #### 请求示例

    ```http
    GET /api/posts/my?page=1&pageSize=10
    Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
    ```

    #### 响应示例

    ```json
    {
        "code": 200,
        "message": "success",
        "data": {
            "items": [
                {
                    "id": 123,
                    "title": "校园活动通知",
                    "content": "本周五下午2点在图书馆举办读书分享会...",
                    "userId": 456,
                    "createTime": "2024-04-21 12:00:00",
                    "updateTime": "2024-04-21 12:00:00",
                    "status": 1,
                    "authorName": "张三",
                    "authorAvatar": "https://example.com/avatar.jpg",
                    "commentCount": 5,
                    "likeCount": 10
                }
            ],
            "hasMore": true,
            "nextTimestamp": "2024-04-21 12:00:00"
        }
    }
    ```

    ### 注意事项

    1. 接口需要用户登录才能访问
    2. 返回的帖子列表按创建时间倒序排序
    3. 只返回状态为正常的帖子（status=1）
    4. 分页参数page从1开始计数
    5. 建议pageSize不要设置过大，以免影响性能 