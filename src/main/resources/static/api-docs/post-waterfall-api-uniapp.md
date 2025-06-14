# 帖子瀑布流接口文档 (UniApp版)

## 1. 瀑布流加载帖子列表

### 接口描述
- 用于实现帖子列表的无限滚动瀑布流加载
- 按时间倒序排列
- 支持首次加载和加载更多
- 返回帖子基本信息、分类信息和发布者信息

### 请求信息
- 请求路径：`/api/posts/waterfall`
- 请求方法：`GET`
- 请求参数：

| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| lastTime | String | 否 | 最后一条记录的时间戳，格式：yyyy-MM-dd HH:mm:ss | 2024-04-07 10:00:00 |
| limit | Integer | 否 | 每次加载的数量，默认10条 | 10 |

### 响应信息
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "items": [
            {
                "postId": 1,
                "title": "帖子标题",
                "content": "帖子内容",
                "postType": "normal",
                "createdAt": "2024-04-07 10:00:00",
                "viewCount": 100,
                "likeCount": 50,
                "commentCount": 20,
                "categoryName": "分类名称",
                "username": "用户名",
                "avatar": "头像URL"
            }
        ],
        "hasMore": true,
        "nextTimestamp": "2024-04-07 09:50:00"
    }
}
```

## 2. UniApp 实现示例

### 2.1 页面结构 (pages/post/list.vue)
```vue
<template>
  <view class="post-list">
    <!-- 帖子列表 -->
    <view class="post-item" v-for="post in posts" :key="post.postId">
      <view class="post-header">
        <image class="avatar" :src="post.avatar" mode="aspectFill"></image>
        <view class="user-info">
          <text class="username">{{post.username}}</text>
          <text class="time">{{post.createdAt}}</text>
        </view>
      </view>
      <view class="post-content">
        <text class="title">{{post.title}}</text>
        <text class="content">{{post.content}}</text>
      </view>
      <view class="post-footer">
        <view class="stats">
          <text class="views">{{post.viewCount}} 浏览</text>
          <text class="likes">{{post.likeCount}} 点赞</text>
          <text class="comments">{{post.commentCount}} 评论</text>
        </view>
      </view>
    </view>
    
    <!-- 加载更多 -->
    <view class="loading" v-if="loading">
      <text>加载中...</text>
    </view>
    
    <!-- 无更多数据 -->
    <view class="no-more" v-if="!hasMore && posts.length > 0">
      <text>没有更多数据了</text>
    </view>
    
    <!-- 空状态 -->
    <view class="empty" v-if="!loading && posts.length === 0">
      <text>暂无数据</text>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      posts: [],
      loading: false,
      hasMore: true,
      lastTime: null,
      pageSize: 10
    }
  },
  
  onLoad() {
    this.loadPosts()
  },
  
  // 下拉刷新
  onPullDownRefresh() {
    this.refreshPosts()
  },
  
  // 触底加载更多
  onReachBottom() {
    if (this.hasMore && !this.loading) {
      this.loadPosts()
    }
  },
  
  methods: {
    // 加载帖子列表
    async loadPosts() {
      if (this.loading || !this.hasMore) return
      
      try {
        this.loading = true
        const response = await uni.request({
          url: this.globalData.serverUrl + '/api/posts/waterfall',
          method: 'GET',
          data: {
            lastTime: this.lastTime,
            limit: this.pageSize
          }
        })
        
        if (response.data.code === 200) {
          const { items, hasMore, nextTimestamp } = response.data.data
          this.posts = [...this.posts, ...items]
          this.hasMore = hasMore
          this.lastTime = nextTimestamp
        } else {
          uni.showToast({
            title: response.data.message || '加载失败',
            icon: 'none'
          })
        }
      } catch (error) {
        console.error('加载失败:', error)
        uni.showToast({
          title: '网络错误，请重试',
          icon: 'none'
        })
      } finally {
        this.loading = false
        uni.stopPullDownRefresh()
      }
    },
    
    // 刷新帖子列表
    async refreshPosts() {
      this.posts = []
      this.lastTime = null
      this.hasMore = true
      await this.loadPosts()
    }
  }
}
</script>

<style lang="scss">
.post-list {
  padding: 20rpx;
  
  .post-item {
    background: #fff;
    border-radius: 12rpx;
    padding: 20rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.05);
    
    .post-header {
      display: flex;
      align-items: center;
      margin-bottom: 20rpx;
      
      .avatar {
        width: 80rpx;
        height: 80rpx;
        border-radius: 50%;
        margin-right: 20rpx;
      }
      
      .user-info {
        .username {
          font-size: 28rpx;
          font-weight: bold;
        }
        
        .time {
          font-size: 24rpx;
          color: #999;
          margin-top: 4rpx;
        }
      }
    }
    
    .post-content {
      .title {
        font-size: 32rpx;
        font-weight: bold;
        margin-bottom: 10rpx;
      }
      
      .content {
        font-size: 28rpx;
        color: #666;
        line-height: 1.5;
      }
    }
    
    .post-footer {
      margin-top: 20rpx;
      border-top: 1rpx solid #eee;
      padding-top: 20rpx;
      
      .stats {
        display: flex;
        font-size: 24rpx;
        color: #999;
        
        text {
          margin-right: 30rpx;
        }
      }
    }
  }
  
  .loading, .no-more, .empty {
    text-align: center;
    padding: 30rpx;
    color: #999;
    font-size: 28rpx;
  }
}
</style>
```

### 2.2 性能优化建议

1. 图片懒加载
```vue
<image 
  :src="post.avatar" 
  mode="aspectFill" 
  lazy-load
  @error="handleImageError"
></image>
```

2. 数据缓存
```javascript
// 缓存数据
const cacheKey = 'post_list_cache'
const cacheExpire = 5 * 60 * 1000 // 5分钟

// 保存缓存
function saveCache(data) {
  const cache = {
    data,
    timestamp: Date.now()
  }
  uni.setStorageSync(cacheKey, JSON.stringify(cache))
}

// 读取缓存
function getCache() {
  const cache = uni.getStorageSync(cacheKey)
  if (!cache) return null
  
  const { data, timestamp } = JSON.parse(cache)
  if (Date.now() - timestamp > cacheExpire) {
    uni.removeStorageSync(cacheKey)
    return null
  }
  
  return data
}
```

3. 防抖处理
```javascript
// 防抖函数
function debounce(fn, delay = 200) {
  let timer = null
  return function (...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => fn.apply(this, args), delay)
  }
}

// 使用防抖
const handleScroll = debounce(() => {
  if (this.hasMore && !this.loading) {
    this.loadPosts()
  }
})
```

### 2.3 错误处理

```javascript
// 统一错误处理
function handleError(error) {
  console.error('请求错误:', error)
  
  // 网络错误
  if (error.errMsg && error.errMsg.includes('request:fail')) {
    uni.showToast({
      title: '网络连接失败，请检查网络设置',
      icon: 'none'
    })
    return
  }
  
  // 服务器错误
  if (error.statusCode >= 500) {
    uni.showToast({
      title: '服务器错误，请稍后重试',
      icon: 'none'
    })
    return
  }
  
  // 其他错误
  uni.showToast({
    title: error.message || '操作失败，请重试',
    icon: 'none'
  })
}
```

### 2.4 用户体验优化

1. 下拉刷新配置
```json
{
  "pages": [
    {
      "path": "pages/post/list",
      "style": {
        "enablePullDownRefresh": true,
        "backgroundTextStyle": "dark"
      }
    }
  ]
}
```

2. 骨架屏
```vue
<template>
  <view class="skeleton" v-if="loading && posts.length === 0">
    <view class="skeleton-item" v-for="i in 3" :key="i">
      <view class="skeleton-header">
        <view class="skeleton-avatar"></view>
        <view class="skeleton-info">
          <view class="skeleton-name"></view>
          <view class="skeleton-time"></view>
        </view>
      </view>
      <view class="skeleton-content">
        <view class="skeleton-title"></view>
        <view class="skeleton-text"></view>
      </view>
    </view>
  </view>
</template>

<style lang="scss">
.skeleton {
  padding: 20rpx;
  
  .skeleton-item {
    background: #fff;
    border-radius: 12rpx;
    padding: 20rpx;
    margin-bottom: 20rpx;
    
    .skeleton-header {
      display: flex;
      align-items: center;
      margin-bottom: 20rpx;
      
      .skeleton-avatar {
        width: 80rpx;
        height: 80rpx;
        border-radius: 50%;
        background: #f5f5f5;
        margin-right: 20rpx;
      }
      
      .skeleton-info {
        flex: 1;
        
        .skeleton-name {
          width: 200rpx;
          height: 32rpx;
          background: #f5f5f5;
          margin-bottom: 10rpx;
        }
        
        .skeleton-time {
          width: 150rpx;
          height: 24rpx;
          background: #f5f5f5;
        }
      }
    }
    
    .skeleton-content {
      .skeleton-title {
        width: 80%;
        height: 32rpx;
        background: #f5f5f5;
        margin-bottom: 20rpx;
      }
      
      .skeleton-text {
        width: 100%;
        height: 24rpx;
        background: #f5f5f5;
        margin-bottom: 10rpx;
        
        &:last-child {
          width: 60%;
        }
      }
    }
  }
}
</style>
```

3. 错误重试
```vue
<template>
  <view class="error-container" v-if="error">
    <text class="error-text">{{error}}</text>
    <button class="retry-btn" @tap="retryLoad">重试</button>
  </view>
</template>

<style lang="scss">
.error-container {
  padding: 40rpx;
  text-align: center;
  
  .error-text {
    color: #999;
    font-size: 28rpx;
    margin-bottom: 20rpx;
  }
  
  .retry-btn {
    width: 200rpx;
    height: 80rpx;
    line-height: 80rpx;
    text-align: center;
    background: #007AFF;
    color: #fff;
    border-radius: 40rpx;
    font-size: 28rpx;
  }
}
</style>
``` 