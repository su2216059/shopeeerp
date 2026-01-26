@echo off
REM 测试Web管理界面API的脚本 (Windows版本)

set BASE_URL=http://localhost:8080

echo ========== 测试Web管理界面API ==========
echo.

REM 1. 查询Worker列表
echo 1. 查询Worker列表...
curl -X GET %BASE_URL%/market/workers/list

echo.

REM 2. 查询任务列表（所有任务）
echo 2. 查询任务列表（所有任务）...
curl -X GET "%BASE_URL%/market/tasks/list?limit=10"

echo.

REM 3. 查询待处理任务
echo 3. 查询待处理任务...
curl -X GET "%BASE_URL%/market/tasks/list?limit=10&status=PENDING"

echo.

REM 4. 查询已完成任务
echo 4. 查询已完成任务...
curl -X GET "%BASE_URL%/market/tasks/list?limit=10&status=DONE"

echo.

REM 5. 添加测试任务
echo 5. 添加测试任务...
curl -X POST %BASE_URL%/market/tasks/enqueue ^
  -H "Content-Type: application/json" ^
  -d "[{\"platform\":\"ozon\",\"market\":\"RU\",\"url\":\"https://www.ozon.ru/product/smartfon-apple-iphone-15-128-gb-rozovyy-1210605889/\",\"data_type\":\"product_detail\",\"priority\":1}]"

echo.
echo ========== 测试完成 ==========
echo.
echo 接下来的步骤:
echo 1. 打开浏览器访问 http://localhost:8080/worker-management.html
echo 2. 查看Worker监控和任务管理页面
echo 3. 尝试添加新任务
echo.
pause
