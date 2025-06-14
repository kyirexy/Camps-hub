# 天气查询接口文档

## 接口说明
本接口用于查询指定地区的实时天气信息，数据来源于中国气象局官方数据。该接口为公开接口，无需登录即可访问。

## 接口列表

### 1. 获取指定地区天气信息

#### 接口描述
获取指定省份和城市的实时天气信息，包括温度、湿度、风向、风速等详细信息。

#### 请求信息
- 请求路径: `/api/weather/query`
- 请求方式: GET
- 接口标签: 天气查询接口
- 权限要求: 公开接口，无需认证
- 响应格式: JSON

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| province | string | 是 | 省份名称，不加"省"字 | 黑龙江 |
| city | string | 是 | 城市名称，不加"市"字 | 哈尔滨 |

#### 响应参数
| 参数名 | 类型 | 说明 | 示例 |
|--------|------|------|------|
| code | integer | 状态码，200表示成功，400表示失败 | 200 |
| msg | string | 错误信息，成功时为null | null |
| precipitation | double | 降水量，单位：毫米 | 0.0 |
| temperature | double | 温度，单位：摄氏度 | 16.5 |
| pressure | integer | 气压，单位：hPa | 995 |
| humidity | integer | 湿度，单位：百分比 | 29 |
| windDirection | string | 风向 | "西北风" |
| windDirectionDegree | integer | 风向角度，0-360度 | 285 |
| windSpeed | double | 风速，单位：米/秒 | 3.9 |
| windScale | string | 风力等级 | "3级" |
| place | string | 查询地区，格式：国家, 省份, 城市 | "中国, 黑龙江, 哈尔滨" |

#### 响应示例
```json
{
    "precipitation": 0.0,
    "temperature": 16.5,
    "pressure": 995,
    "humidity": 29,
    "windDirection": "西北风",
    "windDirectionDegree": 285,
    "windSpeed": 3.9,
    "windScale": "3级",
    "place": "中国, 黑龙江, 哈尔滨",
    "code": 200,
    "msg": null
}
```

#### 错误响应示例
```json
{
    "code": 400,
    "msg": "获取天气信息失败：网络异常",
    "precipitation": null,
    "temperature": null,
    "pressure": null,
    "humidity": null,
    "windDirection": null,
    "windDirectionDegree": null,
    "windSpeed": null,
    "windScale": null,
    "place": null
}
```

## 注意事项
1. 请确保传入的省份和城市名称正确，不要包含"省"、"市"等后缀
2. 接口返回的温度单位为摄氏度
3. 接口返回的降水量单位为毫米
4. 接口返回的风速单位为米/秒
5. 接口返回的气压单位为hPa
6. 接口返回的湿度为百分比
7. 如遇到接口调用失败，请检查网络连接或联系管理员

## 调用示例
```javascript
// 使用axios调用示例
axios.get('/api/weather/query', {
    params: {
        province: '黑龙江',
        city: '哈尔滨'
    }
}).then(response => {
    const weather = response.data;
    console.log(`当前温度：${weather.temperature}℃`);
    console.log(`当前湿度：${weather.humidity}%`);
    console.log(`当前风向：${weather.windDirection}`);
    console.log(`当前风速：${weather.windSpeed}m/s`);
}).catch(error => {
    console.error('获取天气信息失败：', error);
});
```

## 更新日志
- 2024-04-26: 接口文档创建
- 2024-04-26: 更新实际响应数据格式和示例 