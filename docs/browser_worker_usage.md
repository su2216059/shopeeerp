# 浏览器Worker使用指南

## 概述

浏览器Worker模式允许Tampermonkey脚本作为分布式Worker，从后端任务队列中拉取任务并执行抓取。这实现了"任务队列 + 多浏览器Worker + 断点续跑"的架构。

## 版本更新

**v2.8** - 新增Worker模式支持
- Worker注册和心跳机制
- 从任务队列拉取任务
- 任务进度实时上报
- 任务完成状态报告
- 自动断点续跑

## 两种工作模式

### 1. 独立模式（原有功能）
- **当前页**: 抓取当前商品详情页
- **遍历分类**: 按配置的分类列表批量抓取
- **定时任务**: 定时自动执行抓取

### 2. Worker模式（新增）
- 自动从后端任务队列拉取任务
- 支持多个浏览器实例并行工作
- 任务失败自动重试
- 断点续跑（浏览器关闭后重新打开会自动恢复）

## Worker模式使用步骤

### 步骤1: 确保后端服务运行

```bash
cd d:\javaEverything\shopeeerp
mvn spring-boot:run
```

后端应该在 `http://localhost:8080` 运行。

### 步骤2: 打开Ozon网站

在浏览器中打开任意Ozon页面（例如首页）：
```
https://www.ozon.ru/
```

### 步骤3: 启动Worker模式

1. 点击右下角的脚本面板
2. 点击 **🤖 Worker模式** 按钮
3. 脚本会自动：
   - 生成唯一的Worker ID
   - 向后端注册
   - 开始发送心跳（每60秒）
   - 开始拉取任务（每5秒检查一次）

### 步骤4: 添加任务到队列

使用后端API添加任务：

```bash
# 添加商品详情页任务
curl -X POST http://localhost:8080/market/tasks/enqueue \
  -H "Content-Type: application/json" \
  -d '{
    "tasks": [
      {
        "platform": "ozon",
        "market": "RU",
        "url": "https://www.ozon.ru/product/smartfon-apple-iphone-15-128-gb-rozovyy-1210605889/",
        "data_type": "product_detail",
        "priority": 1
      }
    ]
  }'

# 添加分类列表页任务
curl -X POST http://localhost:8080/market/tasks/enqueue \
  -H "Content-Type: application/json" \
  -d '{
    "tasks": [
      {
        "platform": "ozon",
        "market": "RU",
        "url": "https://www.ozon.ru/category/smartfony-15502/",
        "data_type": "category_list",
        "priority": 2,
        "payload_json": "{\"max_products\":50,\"max_pages\":3}"
      }
    ]
  }'
```

### 步骤5: 监控Worker状态

在脚本面板中可以看到：
- **Worker状态**: 在线/离线/处理任务中
- **抓取/保存/跳过**: 实时统计
- **日志**: 详细的执行日志

## Worker模式特性

### 自动注册
- Worker ID格式: `{browser}-{random}-{timestamp}`
- 例如: `chrome-a3f9k2-lm4n5p`
- ID会保存在本地，重启浏览器后保持不变

### 心跳机制
- 每60秒发送一次心跳
- 后端会标记5分钟内无心跳的Worker为离线

### 任务拉取
- 每5秒检查一次任务队列
- 每次拉取1个任务（可配置）
- 只有在空闲时才拉取新任务

### 进度上报
- 处理商品详情页时上报进度
- 处理分类列表时实时更新已抓取数量

### 任务完成
- 成功: 上报抓取/保存/跳过统计
- 失败: 上报错误信息，后端会自动重试

### 断点续跑
- Worker模式状态保存在本地
- 关闭浏览器后重新打开会自动恢复Worker模式
- 正在处理的任务会被后端超时释放（30分钟）

## 多Worker部署

可以在多个浏览器实例中同时运行Worker：

1. **同一台电脑多个浏览器**
   - Chrome浏览器1: Worker ID = `chrome-xxx-1`
   - Firefox浏览器: Worker ID = `firefox-yyy-2`
   - Chrome浏览器2（隐身模式）: Worker ID = `chrome-zzz-3`

2. **多台电脑**
   - 电脑A: 运行2个Worker
   - 电脑B: 运行3个Worker
   - 所有Worker连接到同一个后端服务

3. **查看在线Worker**
```bash
curl http://localhost:8080/market/workers/list
```

## 配置选项

在脚本中可以修改Worker配置：

```javascript
const WORKER_CONFIG = {
    enabled: false,              // Worker模式是否启用
    workerId: null,              // Worker唯一ID（自动生成）
    workerName: 'Browser Worker', // Worker名称
    heartbeatInterval: 60000,    // 心跳间隔（毫秒）- 1分钟
    pullInterval: 5000,          // 拉取任务间隔（毫秒）- 5秒
    maxTasksPerPull: 1,          // 每次拉取的任务数
};
```

## 任务类型

### product_detail - 商品详情页
- 抓取单个商品的详细信息
- 包含价格、评分、评论数、销量等
- 自动补充Ozon官方销量数据

### category_list - 分类列表页
- 抓取分类页面的所有商品
- 支持配置最大商品数和页数
- 逐个访问商品详情页获取完整数据

### 通用任务
- 自动判断URL类型
- 商品页按product_detail处理
- 列表页按category_list处理

## 故障排查

### Worker无法注册
- 检查后端服务是否运行
- 检查网络连接
- 查看浏览器控制台错误信息

### 拉取不到任务
- 确认任务队列中有待处理任务
- 检查任务状态是否为PENDING
- 查看后端日志

### 任务执行失败
- 查看脚本日志中的错误信息
- 检查URL是否有效
- 确认Ozon网站是否可访问

### Worker显示离线
- 检查心跳是否正常发送
- 查看网络连接
- 重启Worker模式

## API端点

Worker模式使用以下后端API：

- `POST /market/workers/register` - Worker注册
- `POST /market/workers/heartbeat` - 发送心跳
- `GET /market/workers/list` - 查询在线Worker
- `POST /market/tasks/pull` - 拉取任务
- `POST /market/tasks/update` - 更新进度
- `POST /market/tasks/complete` - 完成任务

## 最佳实践

1. **任务优先级**: 重要任务设置更高的priority值
2. **批量添加**: 一次性添加多个任务到队列
3. **监控日志**: 定期查看Worker日志和后端日志
4. **合理配置**: 根据网络情况调整拉取间隔
5. **多Worker**: 部署多个Worker提高抓取效率

## 注意事项

- Worker模式和独立模式不能同时使用
- 启动Worker模式后，"当前页"和"遍历分类"按钮仍可用
- 停止按钮会同时停止Worker模式和独立模式
- Worker ID一旦生成会永久保存，除非清除浏览器数据
