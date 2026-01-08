# 平台适配器系统

## 概述

平台适配器系统提供了一个统一的接口来访问不同电商平台（如Shopee、Lazada）的API。通过适配器模式，系统可以轻松地集成新的电商平台，而无需修改核心业务逻辑。

## 架构设计

### 核心接口

- `PlatformAdapter`: 定义所有平台适配器必须实现的接口
  - `getPlatformName()`: 获取平台名称
  - `fetchOrders()`: 获取订单列表
  - `fetchProducts()`: 获取产品列表
  - `fetchCost()`: 获取成本信息

### 数据模型

- `PlatformOrder`: 平台无关的订单模型
- `PlatformOrderItem`: 平台无关的订单项模型
- `PlatformProduct`: 平台无关的产品模型
- `PlatformCost`: 平台无关的成本模型

### 实现类

- `ShopeeAdapter`: Shopee平台适配器实现
- `LazadaAdapter`: Lazada平台适配器实现

### 工厂类

- `PlatformAdapterFactory`: 管理和获取平台适配器的工厂类

## 使用方法

### 1. 添加新的平台适配器

1. 创建新的适配器类，实现 `PlatformAdapter` 接口
2. 使用 `@Component` 注解标记该类
3. 实现所有接口方法

示例：

```java
@Component
public class NewPlatformAdapter implements PlatformAdapter {
    @Override
    public String getPlatformName() {
        return "NewPlatform";
    }
    
    // 实现其他方法...
}
```

### 2. 通过工厂获取适配器

```java
@Autowired
private PlatformAdapterFactory adapterFactory;

PlatformAdapter adapter = adapterFactory.getAdapter("shopee");
List<PlatformOrder> orders = adapter.fetchOrders("2024-01-01", "2024-01-31");
```

### 3. 通过REST API访问

```
GET /api/platform/platforms - 获取所有支持的平台
GET /api/platform/{platform}/orders - 获取指定平台的订单
GET /api/platform/{platform}/products - 获取指定平台的产品
GET /api/platform/{platform}/costs/{productId} - 获取指定平台的产品成本
```

## 扩展说明

每个适配器实现类中的方法都标记了 `TODO`，需要根据实际的平台API文档来实现具体的API调用逻辑。
