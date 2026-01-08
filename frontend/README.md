# Shopee ERP Frontend

基于React和Ant Design的前端项目。

## 技术栈

- React 18
- Vite
- Ant Design
- React Router
- Axios

## 安装依赖

```bash
npm install
```

## 开发

```bash
npm run dev
```

## 构建

```bash
npm run build
```

## 项目结构

```
frontend/
├── src/
│   ├── api/           # API接口定义
│   ├── components/    # 公共组件
│   ├── pages/         # 页面组件
│   ├── utils/         # 工具函数
│   ├── App.jsx        # 主应用组件
│   └── main.jsx       # 入口文件
├── index.html
├── vite.config.js
└── package.json
```

## 页面路由

- `/` - 仪表盘
- `/customers` - 客户列表
- `/customers/new` - 新增客户
- `/customers/edit/:id` - 编辑客户
- `/products` - 产品列表
- `/products/new` - 新增产品
- `/products/edit/:id` - 编辑产品
- `/orders` - 订单列表
- `/orders/new` - 新增订单
- `/orders/edit/:id` - 编辑订单
- ... 其他模块类似
