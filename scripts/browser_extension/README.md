# Ozon Scraper 浏览器插件

在 Ozon 页面内运行的数据抓取工具，绕过地区限制和反爬虫检测。

## 安装方法

### 方法 1：油猴脚本（推荐）

1. **安装 Tampermonkey 扩展**
   - Chrome: [Tampermonkey](https://chrome.google.com/webstore/detail/tampermonkey/dhdgffkkebhmkfjojejmpbldmpobfkfo)
   - Firefox: [Tampermonkey](https://addons.mozilla.org/firefox/addon/tampermonkey/)
   - Edge: [Tampermonkey](https://microsoftedge.microsoft.com/addons/detail/tampermonkey/iikmkjmpaadaobahmlepeloendndfphd)

2. **安装脚本**
   - 打开 Tampermonkey 控制台
   - 点击 "添加新脚本"
   - 复制 `ozon_scraper.user.js` 的内容粘贴进去
   - 保存 (Ctrl+S)

3. **使用**
   - 打开 https://www.ozon.ru/
   - 页面右下角会出现蓝色控制面板
   - 点击按钮即可抓取数据

## 功能说明

### 控制面板按钮

| 按钮 | 功能 |
|------|------|
| **抓取当前商品** | 在商品详情页使用，抓取商品信息并发送到后端 |
| **入队页面商品链接** | 在分类页/搜索页使用，将页面上的商品链接入队 |
| **测试后端连接** | 测试与后端 API 的连接是否正常 |

### 抓取的数据

- 商品 ID
- 标题
- 价格
- 评分
- 评论数
- 库存状态

## 配置

在脚本开头的 `CONFIG` 对象中修改：

```javascript
const CONFIG = {
    // 后端 API 地址（修改为你的后端地址）
    backendUrl: 'http://localhost:8080',
    // 是否显示悬浮按钮
    showFloatingButton: true,
};
```

## 使用流程

1. 确保后端 Spring Boot 服务已启动
2. 打开浏览器，访问 Ozon 网站
3. 如果需要 VPN/代理，在浏览器中配置
4. 导航到商品分类页或搜索结果页
5. 点击 "入队页面商品链接" 将商品 URL 入队
6. 或者打开商品详情页，点击 "抓取当前商品" 直接抓取数据

## 故障排除

### 连接失败
- 检查后端服务是否运行在 `localhost:8080`
- 检查浏览器控制台是否有 CORS 错误
- 确保后端已配置 `@CrossOrigin(origins = "*")`

### 没有抓取到数据
- 确认在正确的页面类型（商品详情页 vs 列表页）
- 检查浏览器控制台的日志 `[OzonScraper]`
- Ozon 可能更新了页面结构，需要更新选择器

## 与 Python 脚本的对比

| 特性 | Python 脚本 | 浏览器插件 |
|------|-------------|-----------|
| 地区限制 | 需要俄罗斯代理 | 可用 VPN 或直接访问 |
| 反爬虫 | 可能被检测 | 完全绕过 |
| 自动化 | 全自动 | 半手动 |
| 速度 | 快 | 较慢（需要手动操作） |
| 适用场景 | 大规模抓取 | 测试/小规模抓取 |
