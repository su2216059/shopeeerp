# Shopee ERP System

这是一个基于Spring Boot和React的ERP系统，用于管理电商平台的订单、产品、库存等业务。

## 技术栈

### 后端
- Spring Boot 2.x
- MyBatis
- MySQL 8.0
- Maven

### 前端
- React 18
- Vite
- Ant Design
- React Router
- Axios

## 项目结构

```
shopeeerp/
├── src/main/java/com/example/shopeeerp/
│   ├── pojo/          # 实体类
│   ├── mapper/        # MyBatis Mapper接口
│   ├── service/       # 业务逻辑层
│   ├── controller/    # REST API控制器
│   └── config/       # 配置类
├── src/main/resources/
│   ├── mapper/        # MyBatis XML映射文件
│   └── application.properties
├── sql/               # 数据库SQL脚本
└── frontend/          # React前端项目
```

## 数据库配置

在 `src/main/resources/application.properties` 中配置数据库连接：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/shopeeerp?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=your_password
```

## 运行项目

### 后端

1. 确保MySQL数据库已启动
2. 执行 `sql/` 目录下的SQL脚本创建数据库表
3. 运行 `ShopeeerpSysApplication.java`

### 前端

1. 进入 `frontend` 目录
2. 安装依赖：`npm install`
3. 启动开发服务器：`npm run dev`

## API接口

所有API接口都支持CORS跨域访问，前端可以通过以下接口访问：

- `/api/customers` - 客户管理
- `/api/products` - 产品管理
- `/api/orders` - 订单管理
- `/api/inventory` - 库存管理
- `/api/payments` - 支付管理
- `/api/invoices` - 发票管理
- `/api/users` - 用户管理
- `/api/roles` - 角色管理
- `/api/warehouses` - 仓库管理
- `/api/customer-support` - 客服支持
- `/api/order-items` - 订单项管理
- `/api/sales-data` - 销售数据

## 功能模块

- 客户管理
- 产品管理
- 订单管理
- 库存管理
- 支付管理
- 发票管理
- 用户管理
- 角色管理
- 仓库管理
- 客服支持
- 订单项管理
- 销售数据分析
