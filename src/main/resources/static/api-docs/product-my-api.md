# 我的商品接口文档

## 1. 获取我发布的商品列表

### 接口说明
- 接口名称：获取我发布的商品列表
- 接口路径：`GET /api/v1/products/my`
- 接口描述：获取当前登录用户发布的商品列表，支持分页、排序和状态筛选
- 权限要求：需要用户登录

### 请求参数
#### 请求头
```
Authorization: Bearer {token}
```

#### 查询参数
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| status | String | 否 | 出售中 | 商品状态：出售中、已下架、已达成 |
| sortField | String | 否 | createTime | 排序字段：createTime-创建时间、viewCount-浏览量 |
| sortOrder | String | 否 | desc | 排序方式：asc-升序、desc-降序 |
| pageNum | Integer | 否 | 1 | 页码，从1开始 |
| pageSize | Integer | 否 | 10 | 每页记录数，最大50 |

### 响应参数
```typescript
interface Response {
    code: number;        // 状态码：200-成功
    message: string;     // 响应消息
    data: {
        products: Product[];  // 商品列表
        total: number;        // 总记录数
        pageNum: number;      // 当前页码
        pageSize: number;     // 每页记录数
    }
}

interface Product {
    productId: number;       // 商品ID
    sellerId: number;        // 发布者ID
    sellerUsername: string;  // 发布者用户名
    categoryId: number;      // 分类ID
    categoryName: string;    // 分类名称
    title: string;          // 商品标题
    description: string;     // 详细描述
    priceType: string;      // 报价类型：面议、区间报价、固定价
    minPrice?: number;      // 最低价（区间报价时生效）
    maxPrice?: number;      // 最高价（区间报价时生效）
    expectPrice?: number;   // 期望价（固定价时存储）
    coverImages: string[];  // 封面图数组
    contactWechat?: string; // 卖家微信ID
    isContactVisible: boolean; // 是否公开联系方式
    viewCount: number;      // 浏览数
    status: string;         // 商品状态：出售中、已下架、已达成
    createTime: string;     // 创建时间
    updateTime: string;     // 更新时间
}
```

### 响应示例
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "products": [
            {
                "productId": 1,
                "sellerId": 1001,
                "sellerUsername": "张三",
                "categoryId": 1,
                "categoryName": "数码产品",
                "title": "iPhone 13",
                "description": "9成新iPhone 13",
                "priceType": "固定价",
                "expectPrice": 4999.00,
                "coverImages": [
                    "https://example.com/images/products/2024/04/07/image1.jpg",
                    "https://example.com/images/products/2024/04/07/image2.jpg"
                ],
                "contactWechat": "zhangsan123",
                "isContactVisible": true,
                "viewCount": 100,
                "status": "出售中",
                "createTime": "2024-04-07 10:00:00",
                "updateTime": "2024-04-07 10:00:00"
            }
        ],
        "total": 1,
        "pageNum": 1,
        "pageSize": 10
    }
}
```

### 错误码
| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 401 | 未登录或token已过期 | 跳转到登录页面 |
| 400 | 请求参数错误 | 检查参数是否正确 |
| 500 | 服务器内部错误 | 提示用户稍后重试 |

## 2. 前端开发指南

### 1. 分页实现
```typescript
// 分页参数
interface PaginationParams {
    pageNum: number;
    pageSize: number;
    total: number;
}

// 分页组件示例
const Pagination: React.FC<{
    params: PaginationParams;
    onChange: (page: number) => void;
}> = ({ params, onChange }) => {
    const { pageNum, pageSize, total } = params;
    const totalPages = Math.ceil(total / pageSize);
    
    return (
        <div className="pagination">
            <button 
                disabled={pageNum <= 1}
                onClick={() => onChange(pageNum - 1)}
            >
                上一页
            </button>
            <span>{pageNum} / {totalPages}</span>
            <button 
                disabled={pageNum >= totalPages}
                onClick={() => onChange(pageNum + 1)}
            >
                下一页
            </button>
        </div>
    );
};
```

### 2. 状态筛选实现
```typescript
// 状态选项
const STATUS_OPTIONS = [
    { value: '出售中', label: '出售中' },
    { value: '已下架', label: '已下架' },
    { value: '已达成', label: '已达成' }
];

// 状态筛选组件示例
const StatusFilter: React.FC<{
    value: string;
    onChange: (status: string) => void;
}> = ({ value, onChange }) => {
    return (
        <select value={value} onChange={e => onChange(e.target.value)}>
            {STATUS_OPTIONS.map(option => (
                <option key={option.value} value={option.value}>
                    {option.label}
                </option>
            ))}
        </select>
    );
};
```

### 3. 排序实现
```typescript
// 排序选项
const SORT_OPTIONS = [
    { value: 'createTime', label: '发布时间' },
    { value: 'viewCount', label: '浏览量' }
];

// 排序组件示例
const SortFilter: React.FC<{
    field: string;
    order: 'asc' | 'desc';
    onChange: (field: string, order: 'asc' | 'desc') => void;
}> = ({ field, order, onChange }) => {
    return (
        <div className="sort-filter">
            <select 
                value={field} 
                onChange={e => onChange(e.target.value, order)}
            >
                {SORT_OPTIONS.map(option => (
                    <option key={option.value} value={option.value}>
                        {option.label}
                    </option>
                ))}
            </select>
            <button 
                onClick={() => onChange(field, order === 'asc' ? 'desc' : 'asc')}
            >
                {order === 'asc' ? '↑' : '↓'}
            </button>
        </div>
    );
};
```

### 4. 数据加载示例
```typescript
// 商品列表组件示例
const MyProducts: React.FC = () => {
    const [params, setParams] = useState({
        status: '出售中',
        sortField: 'createTime',
        sortOrder: 'desc',
        pageNum: 1,
        pageSize: 10
    });
    const [data, setData] = useState<Response['data'] | null>(null);
    const [loading, setLoading] = useState(false);

    const loadData = async () => {
        setLoading(true);
        try {
            const response = await fetch(`/api/v1/products/my?${new URLSearchParams(params)}`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });
            const result = await response.json();
            if (result.code === 200) {
                setData(result.data);
            } else {
                // 处理错误
                console.error(result.message);
            }
        } catch (error) {
            console.error('加载失败', error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadData();
    }, [params]);

    return (
        <div className="my-products">
            <div className="filters">
                <StatusFilter 
                    value={params.status}
                    onChange={status => setParams({...params, status, pageNum: 1})}
                />
                <SortFilter 
                    field={params.sortField}
                    order={params.sortOrder}
                    onChange={(field, order) => setParams({...params, sortField: field, sortOrder: order})}
                />
            </div>
            
            {loading ? (
                <div className="loading">加载中...</div>
            ) : data ? (
                <>
                    <div className="product-list">
                        {data.products.map(product => (
                            <ProductCard key={product.productId} product={product} />
                        ))}
                    </div>
                    <Pagination 
                        params={{
                            pageNum: data.pageNum,
                            pageSize: data.pageSize,
                            total: data.total
                        }}
                        onChange={pageNum => setParams({...params, pageNum})}
                    />
                </>
            ) : null}
        </div>
    );
};
```

## 3. 注意事项

1. 分页说明：
   - 页码从1开始
   - 每页默认显示10条记录
   - 可以通过pageSize参数调整每页显示数量，最大50条
   - 建议实现前端分页缓存，提升用户体验

2. 排序说明：
   - 默认按创建时间降序排序
   - 支持按浏览量排序
   - 排序方式支持升序和降序
   - 切换排序时建议重置到第一页

3. 状态筛选：
   - 默认显示"出售中"状态的商品
   - 可以筛选"已下架"和"已达成"状态的商品
   - 不传status参数则显示所有状态的商品
   - 切换状态时建议重置到第一页

4. 权限说明：
   - 只能查看自己发布的商品
   - 需要用户登录才能访问
   - 未登录用户访问会返回401错误
   - 建议在路由层面做权限控制

5. 性能优化建议：
   - 实现数据缓存
   - 添加加载状态
   - 实现错误重试机制
   - 添加下拉刷新功能
   - 考虑实现无限滚动 