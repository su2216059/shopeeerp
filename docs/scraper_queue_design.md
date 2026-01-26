# 任务队列 + 多浏览器Worker + 断点续跑 设计方案

## 系统架构

```
后端任务调度器 ←→ 任务队列表 ←→ 多个浏览器Worker
     ↓                ↓                ↓
  创建任务        持久化存储        拉取&执行
  监控进度        断点续跑          上报结果
```

## 核心流程

### 1. 任务创建流程

```
管理员/定时器 → 创建任务 → 插入task表(status=PENDING)
                              ↓
                         设置priority、max_retries
```

### 2. Worker工作流程

```
Worker启动 → 注册到worker表 → 循环拉取任务
    ↓
拉取任务(status=PENDING) → 锁定任务(status=IN_PROGRESS, lock_owner=worker_id)
    ↓
执行抓取 → 更新进度(payload_json) → 完成/失败
    ↓
更新任务状态(DONE/FAILED) → 继续拉取下一个任务
```

### 3. 断点续跑机制

- Worker崩溃：任务保持IN_PROGRESS状态，超时后自动释放
- 任务失败：retry_count < max_retries时自动重试
- 进度保存：每抓取N个商品更新一次payload_json

---

## 后端API设计

### API 1: Worker注册/心跳

**POST** `/market/tasks/worker/register`

```json
{
  "worker_id": "chrome-worker-001",
  "worker_name": "Chrome浏览器1",
  "browser_type": "chrome",
  "browser_version": "120.0",
  "script_version": "2.7"
}
```

**响应**:
```json
{
  "success": true,
  "worker_id": "chrome-worker-001"
}
```

---

### API 2: 拉取任务

**POST** `/market/tasks/pull`

```json
{
  "worker_id": "chrome-worker-001",
  "limit": 1
}
```

**响应**:
```json
{
  "success": true,
  "task": {
    "id": 123,
    "platform": "ozon",
    "market": "RU",
    "url": "https://www.ozon.ru/category/smartfony-15502/",
    "data_type": "category_page",
    "priority": 10,
    "payload_json": "{\"max_products\":100,\"max_pages\":5}"
  }
}
```

**逻辑**:
1. 查找status=PENDING的任务，按priority DESC排序
2. 检查是否有超时的IN_PROGRESS任务（lock_at > 30分钟），释放它们
3. 锁定任务：UPDATE status=IN_PROGRESS, lock_owner=worker_id, lock_at=NOW()
4. 返回任务详情

---

### API 3: 更新任务进度

**POST** `/market/tasks/update`

```json
{
  "task_id": 123,
  "worker_id": "chrome-worker-001",
  "status": "IN_PROGRESS",
  "progress": {
    "current_page": 2,
    "total_pages": 5,
    "scraped_count": 45,
    "saved_count": 38,
    "skipped_count": 7,
    "last_product_id": "1234567890"
  }
}
```

**响应**:
```json
{
  "success": true
}
```

---

### API 4: 完成/失败任务

**POST** `/market/tasks/complete`

```json
{
  "task_id": 123,
  "worker_id": "chrome-worker-001",
  "status": "DONE",  // 或 "FAILED"
  "result": {
    "scraped_count": 100,
    "saved_count": 85,
    "skipped_count": 15,
    "error_message": null
  }
}
```

---

### API 5: 创建任务（批量）

**POST** `/market/tasks/create`

```json
{
  "tasks": [
    {
      "platform": "ozon",
      "market": "RU",
      "url": "https://www.ozon.ru/category/smartfony-15502/",
      "data_type": "category_page",
      "priority": 10,
      "payload_json": "{\"max_products\":100,\"max_pages\":5}"
    },
    {
      "platform": "ozon",
      "market": "RU",
      "url": "https://www.ozon.ru/category/noutbuki-15692/",
      "data_type": "category_page",
      "priority": 5
    }
  ]
}
```

---

## 浏览器Worker改造方案

### 核心改动

**原来**: 脚本自己决定抓取哪些分类
**现在**: 从后端拉取任务，执行后上报结果

### Worker生命周期

```javascript
// 1. 启动时注册
async function registerWorker() {
  const workerId = generateWorkerId(); // 如: chrome-{timestamp}
  await sendToBackend('/market/tasks/worker/register', {
    worker_id: workerId,
    worker_name: `Chrome Worker ${workerId}`,
    browser_type: 'chrome',
    browser_version: navigator.userAgent,
    script_version: '2.8'
  });
  return workerId;
}

// 2. 主循环：拉取任务
async function workerMainLoop(workerId) {
  while (true) {
    // 拉取任务
    const task = await pullTask(workerId);

    if (!task) {
      log('没有待处理任务，等待30秒...');
      await sleep(30000);
      continue;
    }

    // 执行任务
    await executeTask(task, workerId);

    // 短暂休息
    await sleep(2000);
  }
}

// 3. 执行任务
async function executeTask(task, workerId) {
  try {
    log(`开始执行任务 #${task.id}: ${task.url}`);

    // 解析任务参数
    const params = JSON.parse(task.payload_json || '{}');

    // 根据任务类型执行
    if (task.data_type === 'category_page') {
      await scrapeCategoryPage(task, params, workerId);
    } else if (task.data_type === 'detail_page') {
      await scrapeDetailPage(task, params, workerId);
    }

    // 标记完成
    await completeTask(task.id, workerId, {
      status: 'DONE',
      result: { scraped_count, saved_count, skipped_count }
    });

  } catch (error) {
    log(`任务失败: ${error.message}`);
    await completeTask(task.id, workerId, {
      status: 'FAILED',
      result: { error_message: error.message }
    });
  }
}

// 4. 抓取分类页（支持断点续跑）
async function scrapeCategoryPage(task, params, workerId) {
  // 跳转到目标URL
  if (window.location.href !== task.url) {
    window.location.href = task.url;
    return; // 等待页面加载后继续
  }

  // 检查是否有保存的进度
  const progress = JSON.parse(task.payload_json || '{}');
  let currentPage = progress.current_page || 1;
  let scrapedCount = progress.scraped_count || 0;

  const maxPages = params.max_pages || 5;
  const maxProducts = params.max_products || 100;

  // 从断点继续
  for (let page = currentPage; page <= maxPages; page++) {
    log(`抓取第 ${page}/${maxPages} 页`);

    // 抓取当前页商品
    const products = await scrapeCurrentPage();

    for (const product of products) {
      await enrichWithSalesData(product);
      await sendToBackend('/market/snapshots/ingest', [product]);
      scrapedCount++;

      // 每10个商品更新一次进度
      if (scrapedCount % 10 === 0) {
        await updateTaskProgress(task.id, workerId, {
          current_page: page,
          total_pages: maxPages,
          scraped_count: scrapedCount
        });
      }

      if (scrapedCount >= maxProducts) break;
    }

    if (scrapedCount >= maxProducts) break;

    // 翻页
    if (page < maxPages) {
      await goToNextPage();
      await sleep(3000); // 等待页面加载
    }
  }

  return { scraped_count: scrapedCount };
}
```

---

## 断点续跑机制详解

### 场景1: Worker崩溃

**问题**: Worker执行到一半崩溃，任务卡在IN_PROGRESS状态

**解决方案**:
```java
// 后端定时任务：每5分钟检查一次
@Scheduled(cron = "0 */5 * * * ?")
public void releaseTimeoutTasks() {
    // 查找超过30分钟的IN_PROGRESS任务
    LocalDateTime timeout = LocalDateTime.now().minusMinutes(30);

    List<MarketScrapeTask> timeoutTasks = taskMapper.selectList(
        new QueryWrapper<MarketScrapeTask>()
            .eq("status", "IN_PROGRESS")
            .lt("lock_at", timeout)
    );

    for (MarketScrapeTask task : timeoutTasks) {
        // 释放任务，允许其他Worker拾取
        task.setStatus("PENDING");
        task.setLockOwner(null);
        task.setRetryCount(task.getRetryCount() + 1);

        if (task.getRetryCount() >= task.getMaxRetries()) {
            task.setStatus("FAILED");
            task.setLastError("Max retries exceeded");
        }

        taskMapper.updateById(task);
    }
}
```

### 场景2: 任务执行到一半

**问题**: 分类有100个商品，抓了50个后Worker重启

**解决方案**:
- 进度保存在`payload_json`字段
- Worker重新拉取任务时，从`current_page`和`scraped_count`继续

```json
{
  "max_products": 100,
  "max_pages": 5,
  "current_page": 3,
  "scraped_count": 50,
  "last_product_id": "1234567890"
}
```

### 场景3: 网络错误

**问题**: 抓取过程中网络断开

**解决方案**:
- 每个商品抓取失败时记录错误
- 任务完成后，如果失败率>30%，标记为FAILED
- 允许重试（retry_count < max_retries）

---

## 部署方案

### 单机部署（开发/测试）

```
1台服务器 + 1个浏览器实例
- 后端: Spring Boot (localhost:8080)
- Worker: Chrome + Tampermonkey
- 数据库: MySQL
```

### 中等规模部署（生产）

```
1台服务器 + 3-5个浏览器实例
- 后端: Spring Boot (公网IP:8080)
- Worker 1: Chrome (Windows)
- Worker 2: Chrome (Mac)
- Worker 3: Firefox (Linux)
- Worker 4-5: 云服务器 + Headless Chrome
```

### 大规模部署（未来扩展）

```
- 后端集群: 多个Spring Boot实例 + 负载均衡
- Worker池: 10+ Headless Chrome实例
- 消息队列: RabbitMQ/Kafka替代数据库轮询
- 分布式锁: Redis确保任务不重复执行
```

---

## 监控和管理

### 管理后台功能

1. **任务管理**
   - 创建任务（单个/批量）
   - 查看任务列表（按状态筛选）
   - 重试失败任务
   - 取消任务

2. **Worker监控**
   - 在线Worker列表
   - Worker状态（idle/busy/offline）
   - Worker统计（成功率、平均耗时）
   - 强制下线Worker

3. **进度监控**
   - 实时进度展示
   - 任务执行日志
   - 错误统计和告警

---

## 实施步骤

### 第1步: 数据库准备
```bash
# 执行SQL脚本
mysql -u root -p shopeeerp < sql/market_scrape_task.sql
```

### 第2步: 后端API开发
- 创建Controller: `MarketScrapeTaskController`
- 创建Service: `MarketScrapeTaskService`
- 创建Mapper: `MarketScrapeTaskMapper`
- 实现5个核心API

### 第3步: Worker脚本改造
- 添加Worker注册逻辑
- 添加任务拉取循环
- 改造现有抓取逻辑支持任务参数
- 添加进度上报

### 第4步: 测试
- 创建测试任务
- 启动1个Worker验证
- 启动多个Worker验证并发
- 模拟崩溃测试断点续跑

### 第5步: 上线
- 配置定时任务创建器
- 部署多个Worker实例
- 监控运行状态

---

## 优势总结

✅ **可扩展**: 随时增加Worker实例提升抓取速度
✅ **可靠性**: 任务持久化，Worker崩溃不丢失进度
✅ **灵活性**: 支持优先级、重试、超时控制
✅ **可监控**: 实时查看任务进度和Worker状态
✅ **断点续跑**: 任何时候中断都能从断点继续

---

## 下一步

需要我帮你实现以下哪部分？

1. 后端API代码（Controller + Service + Mapper）
2. 浏览器Worker改造代码
3. 管理后台前端页面
4. 定时任务创建器

请告诉我优先级！
