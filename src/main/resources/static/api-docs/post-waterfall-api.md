# 帖子瀑布流接口文档

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
            // ... 更多帖子
        ],
        "hasMore": true,
        "nextTimestamp": "2024-04-07 09:50:00"
    }
}
```

### 响应参数说明

| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 响应状态码，200表示成功 |
| message | String | 响应消息 |
| data.items | Array | 帖子列表 |
| data.hasMore | Boolean | 是否还有更多数据 |
| data.nextTimestamp | String | 下次加载的时间戳 |

#### 帖子对象(PostVO)字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| postId | Long | 帖子ID |
| title | String | 帖子标题 |
| content | String | 帖子内容 |
| postType | String | 帖子类型：normal(普通)/bounty(悬赏)/lost(失物)/trade(交易) |
| createdAt | String | 发布时间 |
| viewCount | Integer | 浏览数 |
| likeCount | Integer | 点赞数 |
| commentCount | Integer | 评论数 |
| categoryName | String | 分类名称 |
| username | String | 发布者用户名 |
| avatar | String | 发布者头像URL |

### 调用示例

1. 首次加载：
```javascript
// 使用 axios
const response = await axios.get('/api/posts/waterfall', {
    params: {
        limit: 10
    }
});

// 使用 fetch
const response = await fetch('/api/posts/waterfall?limit=10');
```

2. 加载更多：
```javascript
// 使用 axios
const response = await axios.get('/api/posts/waterfall', {
    params: {
        lastTime: '2024-04-07 10:00:00',
        limit: 10
    }
});

// 使用 fetch
const response = await fetch('/api/posts/waterfall?lastTime=2024-04-07 10:00:00&limit=10');
```

## 2. 前端实现建议

### 2.1 基本实现
```javascript
// 状态管理
const state = {
    posts: [],
    loading: false,
    hasMore: true,
    lastTime: null
};

// 加载数据
async function loadPosts() {
    if (state.loading || !state.hasMore) return;
    
    try {
        state.loading = true;
        const response = await axios.get('/api/posts/waterfall', {
            params: {
                lastTime: state.lastTime,
                limit: 10
            }
        });
        
        const { items, hasMore, nextTimestamp } = response.data.data;
        state.posts = [...state.posts, ...items];
        state.hasMore = hasMore;
        state.lastTime = nextTimestamp;
    } catch (error) {
        console.error('加载失败:', error);
    } finally {
        state.loading = false;
    }
}

// 监听滚动
function setupInfiniteScroll() {
    const observer = new IntersectionObserver(
        (entries) => {
            if (entries[0].isIntersecting) {
                loadPosts();
            }
        },
        { threshold: 0.1 }
    );
    
    observer.observe(document.querySelector('.loading-trigger'));
}
```

### 2.2 性能优化建议

1. 图片懒加载
```javascript
// 使用浏览器原生懒加载
<img loading="lazy" src="image.jpg" alt="懒加载图片" />

// 或使用 Intersection Observer
function setupImageLazyLoad() {
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const img = entry.target;
                img.src = img.dataset.src;
                observer.unobserve(img);
            }
        });
    });
    
    document.querySelectorAll('img[data-src]').forEach(img => observer.observe(img));
}
```

2. 虚拟滚动（建议列表数量大时使用）
```javascript
function setupVirtualScroll() {
    const itemHeight = 200; // 每个帖子卡片的估计高度
    const visibleItems = Math.ceil(window.innerHeight / itemHeight);
    const bufferItems = 5; // 上下缓冲区的数量
    
    return {
        getVisibleRange() {
            const scrollTop = window.scrollY;
            const start = Math.max(0, Math.floor(scrollTop / itemHeight) - bufferItems);
            const end = Math.min(state.posts.length, start + visibleItems + 2 * bufferItems);
            return { start, end };
        },
        
        renderItems() {
            const { start, end } = this.getVisibleRange();
            return state.posts.slice(start, end).map(post => ({
                ...post,
                style: `position: absolute; top: ${start * itemHeight}px`
            }));
        }
    };
}
```

3. 防抖优化
```javascript
function debounce(fn, delay = 200) {
    let timer = null;
    return function (...args) {
        if (timer) clearTimeout(timer);
        timer = setTimeout(() => fn.apply(this, args), delay);
    };
}

const handleScroll = debounce(() => {
    if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 200) {
        loadPosts();
    }
});
```

### 2.3 错误处理建议

```javascript
async function loadPosts() {
    if (state.loading || !state.hasMore) return;
    
    try {
        state.loading = true;
        state.error = null;
        
        const response = await axios.get('/api/posts/waterfall', {
            params: {
                lastTime: state.lastTime,
                limit: 10
            }
        });
        
        const { items, hasMore, nextTimestamp } = response.data.data;
        state.posts = [...state.posts, ...items];
        state.hasMore = hasMore;
        state.lastTime = nextTimestamp;
        
    } catch (error) {
        state.error = '加载失败，请稍后重试';
        console.error('加载失败:', error);
        
        // 自动重试（最多3次）
        if (state.retryCount < 3) {
            state.retryCount++;
            setTimeout(() => loadPosts(), 1000 * state.retryCount);
        }
    } finally {
        state.loading = false;
    }
}
```

### 2.4 用户体验建议

1. 添加加载状态指示
```javascript
// 在加载时显示加载指示器
<div v-if="loading" class="loading-indicator">
    <span>加载中...</span>
</div>
```

2. 添加到顶部按钮
```javascript
// 当滚动超过一定距离时显示
<button v-if="showScrollTop" @click="scrollToTop" class="scroll-top">
    ↑
</button>
```

3. 添加下拉刷新功能
```javascript
// 使用 pull-to-refresh 库或自定义实现
function setupPullToRefresh() {
    let startY = 0;
    let currentY = 0;
    
    document.addEventListener('touchstart', (e) => {
        startY = e.touches[0].clientY;
    });
    
    document.addEventListener('touchmove', (e) => {
        currentY = e.touches[0].clientY;
        if (window.scrollY === 0 && currentY - startY > 50) {
            // 触发刷新
            refreshPosts();
        }
    });
}
```

4. 缓存已加载的数据
```javascript
// 使用 localStorage 缓存数据
function cachePosts(posts) {
    localStorage.setItem('cachedPosts', JSON.stringify(posts));
}

function getCachedPosts() {
    const cached = localStorage.getItem('cachedPosts');
    return cached ? JSON.parse(cached) : [];
}
```

5. 添加骨架屏
```javascript
// 在加载时显示骨架屏
<div v-if="loading" class="skeleton">
    <div class="skeleton-header"></div>
    <div class="skeleton-content"></div>
    <div class="skeleton-footer"></div>
</div>
```

6. 添加错误重试按钮
```javascript
// 当加载失败时显示重试按钮
<div v-if="error" class="error-container">
    <p>{{ error }}</p>
    <button @click="retryLoad">重试</button>
</div>
``` 