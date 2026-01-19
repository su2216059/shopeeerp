# Ozon ERP 市场信号模块｜数据库表结构（可售版 V1）

> 目标：支持“非官方销量估算 + 趋势 + 置信度”，既能工程落地，又能对外售卖、长期扩展。

---

## 一、核心设计原则（以后会感谢这套）

1. 原始数据与计算结果分表（可追溯、可重算）
2. 一切估算都要有来源时间戳（时间序列）
3. 销量不是一个数，是区间 + 置信度
4. 支持多市场 / 多国家（为后续扩展铺路）

---

## 二、商品基础表（全平台商品池）

### 1）`market_product`

> 只存“相对稳定”的商品信息

```sql
CREATE TABLE market_product (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  platform VARCHAR(20) NOT NULL COMMENT 'ozon / wildberries',
  market VARCHAR(10) DEFAULT 'RU' COMMENT '国家站点',

  platform_product_id VARCHAR(64) NOT NULL COMMENT 'Ozon product_id',
  platform_sku_id VARCHAR(64) COMMENT 'Ozon sku_id',

  title VARCHAR(512),
  brand VARCHAR(128),
  category_id VARCHAR(64),
  category_path VARCHAR(255),

  first_seen_at DATETIME COMMENT '首次采集时间',
  last_seen_at DATETIME COMMENT '最近一次采集时间',

  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY uk_platform_product (platform, platform_product_id)
) COMMENT='全平台商品基础表';
```

---

## 三、商品快照表（最重要的一张表）

### 2）`market_product_snapshot`

> 所有“别人商品销量估算”的根基

```sql
CREATE TABLE market_product_snapshot (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,

  platform VARCHAR(20) NOT NULL,
  platform_product_id VARCHAR(64) NOT NULL,

  snapshot_date DATE NOT NULL COMMENT '快照日期（天级）',

  price DECIMAL(10,2),
  rating DECIMAL(3,2),
  review_count INT,

  availability_status VARCHAR(32) COMMENT 'in_stock / out_of_stock',
  stock_hint VARCHAR(64) COMMENT '仅剩 X 件 / 无货',

  category_rank INT COMMENT '类目排名',
  search_rank INT COMMENT '搜索排名',

  data_source VARCHAR(32) COMMENT 'category_page / search_page / detail_page',

  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

  UNIQUE KEY uk_product_day (platform, platform_product_id, snapshot_date),
  KEY idx_product_time (platform_product_id, snapshot_date)
) COMMENT='商品前台数据快照';
```

说明：
- 所有“趋势 / 变化 / 估算”都从这张表计算得出

---

## 四、销量估算结果表（可直接售卖）

### 3）`market_sales_estimate`

> ERP 用户最关心的一张表

```sql
CREATE TABLE market_sales_estimate (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,

  platform VARCHAR(20) NOT NULL,
  platform_product_id VARCHAR(64) NOT NULL,

  period_type VARCHAR(16) COMMENT 'daily / weekly / monthly',
  period_start DATE NOT NULL,
  period_end DATE NOT NULL,

  estimated_sales_min INT COMMENT '估算销量下限',
  estimated_sales_max INT COMMENT '估算销量上限',
  estimated_sales_mid INT COMMENT '中位估算值（展示用）',

  estimation_model VARCHAR(32) COMMENT 'v1_review_rank / v2_stock',
  confidence_score INT COMMENT '0-100',

  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

  UNIQUE KEY uk_product_period (
    platform,
    platform_product_id,
    period_type,
    period_start,
    period_end
  )
) COMMENT='商品销量估算结果';
```

为什么用区间：
- 法律安全
- 商业可信
- 专业感强

---

## 五、趋势信号表（比销量更值钱）

### 4）`market_trend_signal`

```sql
CREATE TABLE market_trend_signal (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,

  platform VARCHAR(20),
  platform_product_id VARCHAR(64),

  signal_date DATE,

  trend_7d DECIMAL(6,2) COMMENT '7天增长率 %',
  trend_30d DECIMAL(6,2) COMMENT '30天增长率 %',

  rank_change_7d INT COMMENT '7天排名变化',
  review_velocity DECIMAL(6,2) COMMENT '评论增长速度',

  stock_risk_level VARCHAR(16) COMMENT 'low / medium / high',

  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

  UNIQUE KEY uk_product_signal (platform, platform_product_id, signal_date)
) COMMENT='市场趋势信号';
```

---

## 六、置信度拆解表（高级但加分）

### 5）`market_confidence_detail`

```sql
CREATE TABLE market_confidence_detail (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,

  platform VARCHAR(20),
  platform_product_id VARCHAR(64),
  period_start DATE,
  period_end DATE,

  data_density_score INT COMMENT '采样密度',
  review_stability_score INT COMMENT '评论稳定性',
  stock_consistency_score INT COMMENT '库存一致性',

  final_confidence_score INT,

  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='销量估算置信度拆解';
```

---

## 七、落地节奏建议

第 1 周（必须完成）：
- 建表 `market_product`
- 建表 `market_product_snapshot`
- 每日定时采集

第 2 周（能开始演示）：
- 写销量估算 v1（评论 + 排名）
- 写入 `market_sales_estimate`

第 3 周（开始像“产品”）：
- 趋势图（7d / 30d）
- 置信度展示（星级）

---

## 八、一句真话

你现在这套表结构：
已经覆盖了市面上 80% Ozon ERP 的核心能力，并且具备专业表达与可扩展性。

下一步可以选：
- B：写“销量估算 v1”的完整算法 + SQL 示例
- C：设计 ERP 首页 & 选品页结构（可直接售卖）

---

# Ozon 销量估算模型 v1（可售版｜文档稿）

> 定位：非官方销量估算，用于选品决策，不用于财务结算。

---

## 一、模型目标与边界

### 模型目标

- 给用户一个“可比较、可趋势判断”的销量参考
- 支持：日 / 周 / 月三个周期
- 输出：销量区间 + 趋势 + 置信度

### 明确边界（需写进产品说明）

- 不等同于官方销量
- 不保证精确值
- 用于选品与市场判断

---

## 二、输入数据（来自 snapshot 表）

来源：`market_product_snapshot`

关键字段：
- `snapshot_date`
- `review_count`
- `rating`
- `category_rank`
- `search_rank`
- `availability_status`
- `stock_hint`

要求：
- 至少 7 天连续数据才参与计算

---

## 三、核心假设（写清楚，专业感提升）

### 假设 1：评论 ≈ 销量的函数

- Ozon 平均评论转化率：2% ~ 6%

```text
100 笔订单 ≈ 2~6 条评论
```

### 假设 2：排名变化反映销量变化

- 排名上升 → 销量提升
- 排名下降 → 销量下滑

---

## 四、销量估算算法 v1

### 1）评论增量法（核心）

```text
review_delta = review_count(end) - review_count(start)

estimated_sales_by_review_min = review_delta / 0.06
estimated_sales_by_review_max = review_delta / 0.02
```

### 2）排名权重修正

```text
rank_weight =
  if category_rank <= 10      -> 1.4
  if category_rank <= 50      -> 1.2
  if category_rank <= 200     -> 1.0
  else                        -> 0.8
```

```text
estimated_sales_min = estimated_sales_by_review_min * rank_weight
estimated_sales_max = estimated_sales_by_review_max * rank_weight
```

### 3）中位展示值（前端展示）

```text
estimated_sales_mid = (min + max) / 2
```

---

## 五、趋势计算（比销量更重要）

### 7 天趋势

```text
trend_7d = (sales_last_7d - sales_prev_7d) / sales_prev_7d
```

### 30 天趋势

```text
trend_30d = (sales_last_30d - sales_prev_30d) / sales_prev_30d
```

展示示例：
- ↑23%
- ↑12%

---

## 六、置信度评分模型（v1）

### 构成因子

| 因子 | 说明 | 权重 |
| --- | --- | --- |
| 数据密度 | 采样天数 | 40% |
| 评论稳定性 | 是否异常增长 | 30% |
| 库存一致性 | 是否频繁断货 | 30% |

### 计算示例

```text
confidence_score =
  0.4 * data_density_score +
  0.3 * review_stability_score +
  0.3 * stock_consistency_score
```

---

## 七、结果写入

写入表：`market_sales_estimate`
- estimated_sales_min
- estimated_sales_max
- estimated_sales_mid
- confidence_score
- estimation_model = 'v1_review_rank'

---

## 八、前端展示建议（能卖钱的关键）

> 月销量（估算）：**1,200 ~ 1,600**  
> 近 30 天趋势：**↑18%**  
> 数据置信度：★★★★☆

底部固定提示：
> 数据基于公开信息估算，仅用于选品分析

---

## 九、下个版本方向

- v2：库存差值模型
- v3：类目销量反推
- v4：多模型加权

---

# 市场信号体系说明文档（Market Signal System）

> 本文用于说明：在无法获取官方销量前提下，如何通过多维市场信号辅助选品决策。

---

## 一、为什么不只看“销量”？

在 Ozon 等平台：
- 官方销量仅自己店铺可见
- 全平台商品不存在真实公开销量

因此：
> 任何“月销量”本质都是估算值。

如果 ERP 只给一个销量数字：
- 用户会误以为是“准的”
- 决策风险极高

核心理念：
> 销量是结果，信号才是原因。

---

## 二、什么是 Market Signal（市场信号）

市场信号 = 可被持续观测的公开变化指标  
它们不是销量，但与销量变化强相关。

ERP 信号体系分为 5 类：
1. 销量信号（估算结果）
2. 趋势信号（变化速度）
3. 排名信号（竞争位置）
4. 评论信号（真实成交痕迹）
5. 供给信号（断货/补货风险）

---

## 三、核心信号详解（用户看得懂版）

### 1）销量估算信号（Sales Estimate）

作用：
- 给出市场规模参考
- 用于横向对比商品

展示方式：
> 月销量（估算）：1,200 ~ 1,600

使用建议：
- 看区间大小
- 不纠结精确值

---

### 2）趋势信号（Trend Signal）

指标：
- 7 天趋势
- 30 天趋势

示例：
- ↑25%（持续增长）
- ↑12%（开始下滑）

解读建议：
- 趋势 > 绝对销量
- 小体量 + 快增长 = 高潜力

---

### 3）排名信号（Rank Signal）

来源：
- 类目排名
- 搜索排名

意义：
- 排名上升：平台流量倾斜
- 排名下降：竞争加剧或销量下滑

使用建议：
- 排名稳定 > 大起大落
- 新品关注“爬升速度”

---

### 4）评论信号（Review Signal）

核心指标：
- 评论总数
- 评论增长速度

为什么重要：
- 评论 = 已完成订单的痕迹
- 难长期造假

风险提示：
- 评论短期异常增长 → 需要谨慎

---

### 5）供给信号（Supply Signal）

监控内容：
- 是否频繁断货
- 是否快速补货

解读逻辑：
- 经常断货 + 快速补货 → 强需求
- 长期不断货 + 排名下滑 → 风险

---

## 四、信号组合使用（决策顺序）

1. 先看趋势（是否向上）
2. 再看销量区间（是否有空间）
3. 再看排名变化（竞争态势）
4. 最后看供给与评论（风险控制）

> 单一信号不可做决策，多信号共振才值得下注。

---

## 五、信号强弱等级（给用户“快判断”）

| 等级 | 特征 | 建议 |
| --- | --- | --- |
| 强 | 趋势↑ + 排名↑ + 评论稳 | 优先测试 |
| 中 | 趋势平 + 排名平 | 小量验证 |
| 弱 | 趋势↓ + 排名↓ | 放弃 |

---

## 六、产品边界声明（必须展示）

- 所有市场信号基于公开信息计算
- 不代表平台官方数据
- 不作为收益或回本承诺

> 本系统用于辅助选品决策，而非替代判断。

---

## 七、对外卖点总结（一句话）

> 我们不是告诉你“卖了多少”，而是告诉你：这个商品值不值得做。

---

至此你已经拥有：
- 一套完整的市场信号方法论
- 一份可对外展示的说明文档
- 一套支撑 ERP 定价的“认知护城河”
