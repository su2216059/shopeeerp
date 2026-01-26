@echo off
REM 测试Worker API的脚本 (Windows版本)

set BASE_URL=http://localhost:8080

echo ========== 测试Worker API ==========
echo.

REM 1. 添加测试任务
echo 1. 添加测试任务到队列...
curl -X POST %BASE_URL%/market/tasks/enqueue ^
  -H "Content-Type: application/json" ^
  -d "[{\"platform\":\"ozon\",\"market\":\"RU\",\"url\":\"https://www.ozon.ru/product/smartfon-apple-iphone-15-128-gb-rozovyy-1210605889/\",\"data_type\":\"product_detail\",\"priority\":1},{\"platform\":\"ozon\",\"market\":\"RU\",\"url\":\"https://www.ozon.ru/product/smartfon-samsung-galaxy-s24-ultra-256-gb-chernyy-1234567890/\",\"data_type\":\"product_detail\",\"priority\":2},{\"platform\":\"ozon\",\"market\":\"RU\",\"url\":\"https://www.ozon.ru/category/smartfony-15502/\",\"data_type\":\"category_list\",\"priority\":3,\"payload_json\":\"{\\\"max_products\\\":10,\\\"max_pages\\\":1}\"}]"

echo.
echo 2. 查询在线Worker列表...
curl -X GET %BASE_URL%/market/workers/list

echo.
echo 3. 模拟Worker拉取任务...
curl -X POST %BASE_URL%/market/tasks/pull ^
  -H "Content-Type: application/json" ^
  -d "{\"worker_id\":\"test-worker-001\",\"limit\":1}"

echo.
echo ========== 测试完成 ==========
echo.
echo 接下来的步骤:
echo 1. 打开浏览器访问 https://www.ozon.ru/
echo 2. 点击脚本面板中的 '🤖 Worker模式' 按钮
echo 3. 观察Worker自动拉取并处理任务
echo.
pause
