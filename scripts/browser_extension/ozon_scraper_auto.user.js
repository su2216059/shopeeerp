// ==UserScript==
// @name         Ozon Auto Scraper (自动批量抓取)
// @namespace    http://tampermonkey.net/
// @version      2.0
// @description  自动批量抓取 Ozon 商品数据，定时执行，只保存有销量的商品
// @author       ShopeeERP
// @match        https://www.ozon.ru/*
// @match        https://ozon.ru/*
// @grant        GM_xmlhttpRequest
// @grant        GM_notification
// @grant        GM_setValue
// @grant        GM_getValue
// @grant        GM_addStyle
// @connect      localhost
// @connect      127.0.0.1
// ==/UserScript==

(function() {
    'use strict';

    // ========== 配置 ==========
    const CONFIG = {
        backendUrl: 'http://localhost:8080',
        // 抓取间隔（毫秒）- 每个商品之间的间隔
        scrapeDelay: 2000,
        // 翻页间隔（毫秒）
        pageDelay: 3000,
        // 定时任务间隔（分钟）
        scheduleInterval: 30,
        // 每次最多抓取多少个商品
        maxProductsPerRun: 100,
        // 只保存有评论/销量数据的商品
        onlySaveWithSales: true,
        // 最小评论数（作为销量参考）
        minReviewCount: 1,
    };

    // ========== 状态管理 ==========
    let isRunning = false;
    let isPaused = false;
    let currentTask = null;
    let scrapedCount = 0;
    let savedCount = 0;
    let skippedCount = 0;
    let productQueue = [];

    // ========== 工具函数 ==========
    function log(msg, data = '') {
        console.log('[OzonAutoScraper]', msg, data);
        updateLog(msg);
    }

    function sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    function notify(title, text) {
        if (typeof GM_notification !== 'undefined') {
            GM_notification({ title, text, timeout: 3000 });
        }
    }

    function sendToBackend(endpoint, data) {
        return new Promise((resolve, reject) => {
            GM_xmlhttpRequest({
                method: 'POST',
                url: `${CONFIG.backendUrl}${endpoint}`,
                headers: { 'Content-Type': 'application/json' },
                data: JSON.stringify(data),
                onload: (response) => {
                    if (response.status >= 200 && response.status < 300) {
                        try {
                            resolve(JSON.parse(response.responseText || '{}'));
                        } catch {
                            resolve({});
                        }
                    } else {
                        reject(new Error(`HTTP ${response.status}`));
                    }
                },
                onerror: (error) => reject(error),
            });
        });
    }

    // ========== 数据提取 ==========
    function extractProductFromCurrentPage() {
        const url = window.location.href;
        if (!url.includes('/product/')) return null;

        const productIdMatch = url.match(/product\/[^/]*-(\d+)/);
        const productId = productIdMatch ? productIdMatch[1] : null;
        if (!productId) return null;

        // 标题
        const titleEl = document.querySelector('h1, [data-widget="webProductHeading"]');
        const title = titleEl ? titleEl.textContent.trim() : null;

        // 价格 - 尝试多种选择器
        let price = null;
        const priceSelectors = [
            '[data-widget="webPrice"] span[class*="price"]',
            '[data-widget="webPrice"] span',
            'span[class*="price-number"]',
            'span[class*="Price_price"]',
        ];
        for (const selector of priceSelectors) {
            const el = document.querySelector(selector);
            if (el) {
                const text = el.textContent.replace(/[^\d]/g, '');
                if (text) {
                    price = parseFloat(text);
                    break;
                }
            }
        }

        // 评分和评论数
        let rating = null;
        let reviewCount = null;
        
        // 方法1: 从评分区域提取
        const ratingWidgets = document.querySelectorAll('[data-widget="webReviewProductScore"], [class*="rating"], [class*="review"]');
        for (const widget of ratingWidgets) {
            const text = widget.textContent;
            
            // 提取评分 (如 4.8, 4,8)
            const ratingMatch = text.match(/(\d[.,]\d)/);
            if (ratingMatch && !rating) {
                rating = parseFloat(ratingMatch[1].replace(',', '.'));
            }
            
            // 提取评论数 (如 "1234 отзыва", "12K отзывов")
            const reviewMatch = text.match(/(\d+(?:[.,]\d+)?)\s*[KkКк]?\s*(?:отзыв|оценк|review)/i);
            if (reviewMatch && !reviewCount) {
                let count = parseFloat(reviewMatch[1].replace(',', '.'));
                if (text.toLowerCase().includes('k') || text.toLowerCase().includes('к')) {
                    count *= 1000;
                }
                reviewCount = Math.round(count);
            }
        }

        // 方法2: 从页面文本搜索评论数
        if (!reviewCount) {
            const pageText = document.body.innerText;
            const reviewPatterns = [
                /(\d+(?:\s*\d+)*)\s*(?:отзыв|оценк)/i,
                /(\d+[.,]?\d*)\s*[KkКк]\s*(?:отзыв|оценк)/i,
            ];
            for (const pattern of reviewPatterns) {
                const match = pageText.match(pattern);
                if (match) {
                    let count = parseFloat(match[1].replace(/\s/g, '').replace(',', '.'));
                    if (match[0].toLowerCase().includes('k') || match[0].toLowerCase().includes('к')) {
                        count *= 1000;
                    }
                    reviewCount = Math.round(count);
                    break;
                }
            }
        }

        // 销量估算 (基于评论数)
        // Ozon 平均评论率约 2-5%，这里用 3%
        const estimatedSales = reviewCount ? Math.round(reviewCount / 0.03) : null;

        // 库存状态
        let availabilityStatus = 'unknown';
        const pageText = document.body.innerText.toLowerCase();
        if (pageText.includes('в корзину') || pageText.includes('добавить в корзину')) {
            availabilityStatus = 'in_stock';
        } else if (pageText.includes('нет в наличии') || pageText.includes('закончился')) {
            availabilityStatus = 'out_of_stock';
        }

        // 品牌
        let brand = null;
        const brandEl = document.querySelector('[data-widget="webBrand"] a, [class*="brand"]');
        if (brandEl) {
            brand = brandEl.textContent.trim();
        }

        // 分类
        let categoryPath = null;
        const breadcrumbs = document.querySelectorAll('[data-widget="breadcrumbs"] a, nav[aria-label="breadcrumb"] a');
        if (breadcrumbs.length > 0) {
            categoryPath = Array.from(breadcrumbs).map(a => a.textContent.trim()).join(' > ');
        }

        return {
            platform: 'ozon',
            market: 'RU',
            platform_product_id: productId,
            title,
            brand,
            category_path: categoryPath,
            price,
            rating,
            review_count: reviewCount,
            estimated_sales: estimatedSales,
            availability_status: availabilityStatus,
            snapshot_date: new Date().toISOString().split('T')[0],
            data_source: 'detail_page',
        };
    }

    function extractProductLinksFromPage() {
        const links = new Set();
        
        // 多种选择器匹配商品链接
        const selectors = [
            'a[href*="/product/"]',
            '[data-widget="searchResultsV2"] a',
            '[data-widget="skuGrid"] a',
        ];
        
        for (const selector of selectors) {
            document.querySelectorAll(selector).forEach(el => {
                const href = el.getAttribute('href');
                if (href && href.includes('/product/')) {
                    const fullUrl = href.startsWith('/') ? 'https://www.ozon.ru' + href : href;
                    links.add(fullUrl.split('?')[0]);
                }
            });
        }
        
        return [...links];
    }

    // ========== 自动抓取逻辑 ==========
    async function startAutoScrape() {
        if (isRunning) {
            log('已经在运行中');
            return;
        }

        isRunning = true;
        isPaused = false;
        scrapedCount = 0;
        savedCount = 0;
        skippedCount = 0;

        log('开始自动抓取...');
        updateStatus('运行中');

        // 检查当前页面类型
        const url = window.location.href;
        
        if (url.includes('/product/')) {
            // 在商品详情页 - 直接抓取当前商品
            await scrapeCurrentProduct();
        } else if (url.includes('/category/') || url.includes('/search/') || url.includes('?text=')) {
            // 在分类页/搜索页 - 收集商品链接并逐个抓取
            await scrapeProductList();
        } else {
            log('请在商品分类页或搜索结果页启动');
            notify('Ozon Scraper', '请在商品分类页或搜索结果页启动');
        }

        isRunning = false;
        updateStatus(`完成 (抓取:${scrapedCount} 保存:${savedCount} 跳过:${skippedCount})`);
        log(`抓取完成！抓取:${scrapedCount} 保存:${savedCount} 跳过:${skippedCount}`);
        notify('Ozon Scraper', `抓取完成！保存了 ${savedCount} 个商品`);
    }

    async function scrapeCurrentProduct() {
        const product = extractProductFromCurrentPage();
        if (!product) {
            log('无法提取商品数据');
            return;
        }

        scrapedCount++;
        log(`抓取商品: ${product.platform_product_id} - ${product.title?.substring(0, 30)}...`);

        // 检查是否有销量数据
        if (CONFIG.onlySaveWithSales && (!product.review_count || product.review_count < CONFIG.minReviewCount)) {
            log(`跳过 (无评论数据): ${product.platform_product_id}`);
            skippedCount++;
            return;
        }

        try {
            await sendToBackend('/market/snapshots/ingest', [product]);
            savedCount++;
            log(`保存成功: ${product.platform_product_id}, 评论:${product.review_count}, 估算销量:${product.estimated_sales}`);
        } catch (error) {
            log(`保存失败: ${error.message}`);
        }
    }

    async function scrapeProductList() {
        // 收集当前页面的商品链接
        let allLinks = extractProductLinksFromPage();
        log(`当前页面发现 ${allLinks.length} 个商品链接`);

        // 限制数量
        if (allLinks.length > CONFIG.maxProductsPerRun) {
            allLinks = allLinks.slice(0, CONFIG.maxProductsPerRun);
            log(`限制为 ${CONFIG.maxProductsPerRun} 个`);
        }

        productQueue = [...allLinks];
        
        // 逐个打开商品页面抓取
        for (let i = 0; i < productQueue.length; i++) {
            if (!isRunning || isPaused) break;

            const productUrl = productQueue[i];
            updateStatus(`抓取中 ${i + 1}/${productQueue.length}`);
            updateProgress(i + 1, productQueue.length);

            try {
                // 使用 fetch 获取商品页面
                const html = await fetchProductPage(productUrl);
                const product = parseProductFromHtml(html, productUrl);
                
                if (product) {
                    scrapedCount++;
                    
                    // 检查是否有销量数据
                    if (CONFIG.onlySaveWithSales && (!product.review_count || product.review_count < CONFIG.minReviewCount)) {
                        log(`跳过 (评论数:${product.review_count || 0}): ${product.platform_product_id}`);
                        skippedCount++;
                    } else {
                        try {
                            await sendToBackend('/market/snapshots/ingest', [product]);
                            savedCount++;
                            log(`✓ ${product.platform_product_id} | 评论:${product.review_count} | 估算销量:${product.estimated_sales}`);
                        } catch (error) {
                            log(`保存失败: ${error.message}`);
                        }
                    }
                }
            } catch (error) {
                log(`抓取失败: ${productUrl} - ${error.message}`);
            }

            // 延迟
            await sleep(CONFIG.scrapeDelay);
        }
    }

    async function fetchProductPage(url) {
        return new Promise((resolve, reject) => {
            GM_xmlhttpRequest({
                method: 'GET',
                url: url,
                headers: {
                    'Accept': 'text/html',
                    'Accept-Language': 'ru-RU,ru;q=0.9',
                },
                onload: (response) => {
                    if (response.status === 200) {
                        resolve(response.responseText);
                    } else {
                        reject(new Error(`HTTP ${response.status}`));
                    }
                },
                onerror: (error) => reject(error),
            });
        });
    }

    function parseProductFromHtml(html, url) {
        // 从 HTML 字符串解析商品数据
        const parser = new DOMParser();
        const doc = parser.parseFromString(html, 'text/html');

        const productIdMatch = url.match(/product\/[^/]*-(\d+)/);
        const productId = productIdMatch ? productIdMatch[1] : null;
        if (!productId) return null;

        // 标题
        const titleEl = doc.querySelector('h1');
        const title = titleEl ? titleEl.textContent.trim() : null;

        // 价格
        let price = null;
        const priceMatch = html.match(/"price":\s*"?(\d+)/);
        if (priceMatch) {
            price = parseFloat(priceMatch[1]);
        }

        // 评分和评论数 - 从 JSON 数据中提取
        let rating = null;
        let reviewCount = null;

        // 尝试从 JSON-LD 或页面数据提取
        const ratingMatch = html.match(/"ratingValue":\s*"?(\d+[.,]?\d*)/) ||
                           html.match(/"rating":\s*"?(\d+[.,]?\d*)/);
        if (ratingMatch) {
            rating = parseFloat(ratingMatch[1].replace(',', '.'));
        }

        const reviewMatch = html.match(/"reviewCount":\s*"?(\d+)/) ||
                           html.match(/"ratingCount":\s*"?(\d+)/) ||
                           html.match(/(\d+)\s*отзыв/);
        if (reviewMatch) {
            reviewCount = parseInt(reviewMatch[1]);
        }

        // 销量估算
        const estimatedSales = reviewCount ? Math.round(reviewCount / 0.03) : null;

        // 库存状态
        let availabilityStatus = 'unknown';
        if (html.includes('в корзину') || html.includes('Добавить в корзину')) {
            availabilityStatus = 'in_stock';
        } else if (html.includes('нет в наличии')) {
            availabilityStatus = 'out_of_stock';
        }

        return {
            platform: 'ozon',
            market: 'RU',
            platform_product_id: productId,
            title,
            price,
            rating,
            review_count: reviewCount,
            estimated_sales: estimatedSales,
            availability_status: availabilityStatus,
            snapshot_date: new Date().toISOString().split('T')[0],
            data_source: 'detail_page',
        };
    }

    function stopScrape() {
        isRunning = false;
        isPaused = false;
        log('已停止');
        updateStatus('已停止');
    }

    function pauseScrape() {
        isPaused = !isPaused;
        log(isPaused ? '已暂停' : '继续运行');
        updateStatus(isPaused ? '已暂停' : '运行中');
    }

    // ========== 定时任务 ==========
    let scheduleTimer = null;

    function startSchedule() {
        if (scheduleTimer) {
            clearInterval(scheduleTimer);
        }
        
        const intervalMs = CONFIG.scheduleInterval * 60 * 1000;
        scheduleTimer = setInterval(() => {
            log(`定时任务触发 (每 ${CONFIG.scheduleInterval} 分钟)`);
            startAutoScrape();
        }, intervalMs);

        log(`定时任务已启动，间隔 ${CONFIG.scheduleInterval} 分钟`);
        updateScheduleStatus(`每 ${CONFIG.scheduleInterval} 分钟`);
    }

    function stopSchedule() {
        if (scheduleTimer) {
            clearInterval(scheduleTimer);
            scheduleTimer = null;
        }
        log('定时任务已停止');
        updateScheduleStatus('未启动');
    }

    // ========== UI ==========
    function createPanel() {
        GM_addStyle(`
            #ozon-auto-scraper {
                position: fixed;
                bottom: 20px;
                right: 20px;
                z-index: 999999;
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                font-size: 13px;
            }
            #ozon-auto-scraper .panel {
                background: linear-gradient(135deg, #005bff 0%, #0044cc 100%);
                border-radius: 12px;
                padding: 15px;
                box-shadow: 0 8px 32px rgba(0,91,255,0.3);
                color: white;
                min-width: 280px;
            }
            #ozon-auto-scraper .title {
                font-size: 16px;
                font-weight: bold;
                margin-bottom: 12px;
                display: flex;
                align-items: center;
                gap: 8px;
            }
            #ozon-auto-scraper .btn-row {
                display: flex;
                gap: 8px;
                margin-bottom: 10px;
            }
            #ozon-auto-scraper button {
                flex: 1;
                padding: 10px;
                border: none;
                border-radius: 8px;
                font-size: 13px;
                font-weight: 500;
                cursor: pointer;
                transition: all 0.2s;
            }
            #ozon-auto-scraper button.primary {
                background: white;
                color: #005bff;
            }
            #ozon-auto-scraper button.primary:hover {
                background: #f0f0f0;
                transform: translateY(-1px);
            }
            #ozon-auto-scraper button.danger {
                background: #ff4444;
                color: white;
            }
            #ozon-auto-scraper button.secondary {
                background: rgba(255,255,255,0.2);
                color: white;
            }
            #ozon-auto-scraper .stats {
                background: rgba(0,0,0,0.2);
                border-radius: 8px;
                padding: 10px;
                margin-top: 10px;
            }
            #ozon-auto-scraper .stat-row {
                display: flex;
                justify-content: space-between;
                margin: 4px 0;
            }
            #ozon-auto-scraper .progress-bar {
                height: 4px;
                background: rgba(255,255,255,0.3);
                border-radius: 2px;
                margin-top: 8px;
                overflow: hidden;
            }
            #ozon-auto-scraper .progress-fill {
                height: 100%;
                background: #00ff88;
                width: 0%;
                transition: width 0.3s;
            }
            #ozon-auto-scraper .log {
                max-height: 100px;
                overflow-y: auto;
                font-size: 11px;
                background: rgba(0,0,0,0.3);
                border-radius: 6px;
                padding: 8px;
                margin-top: 10px;
            }
            #ozon-auto-scraper .log-entry {
                margin: 2px 0;
                opacity: 0.9;
            }
            #ozon-auto-scraper .collapse-btn {
                position: absolute;
                top: 10px;
                right: 10px;
                background: none;
                border: none;
                color: white;
                cursor: pointer;
                font-size: 18px;
                padding: 0;
                width: auto;
            }
        `);

        const container = document.createElement('div');
        container.id = 'ozon-auto-scraper';
        container.innerHTML = `
            <div class="panel">
                <button class="collapse-btn" id="collapse-btn">−</button>
                <div class="title">🛒 Ozon Auto Scraper</div>
                
                <div id="panel-content">
                    <div class="btn-row">
                        <button class="primary" id="btn-start">▶ 开始抓取</button>
                        <button class="danger" id="btn-stop">■ 停止</button>
                    </div>
                    
                    <div class="btn-row">
                        <button class="secondary" id="btn-schedule">⏰ 定时任务</button>
                        <button class="secondary" id="btn-test">🔗 测试连接</button>
                    </div>
                    
                    <div class="stats">
                        <div class="stat-row">
                            <span>状态:</span>
                            <span id="status">就绪</span>
                        </div>
                        <div class="stat-row">
                            <span>抓取:</span>
                            <span id="scraped-count">0</span>
                        </div>
                        <div class="stat-row">
                            <span>保存:</span>
                            <span id="saved-count">0</span>
                        </div>
                        <div class="stat-row">
                            <span>跳过 (无销量):</span>
                            <span id="skipped-count">0</span>
                        </div>
                        <div class="stat-row">
                            <span>定时:</span>
                            <span id="schedule-status">未启动</span>
                        </div>
                        <div class="progress-bar">
                            <div class="progress-fill" id="progress-fill"></div>
                        </div>
                    </div>
                    
                    <div class="log" id="log"></div>
                </div>
            </div>
        `;

        document.body.appendChild(container);

        // 绑定事件
        document.getElementById('btn-start').onclick = startAutoScrape;
        document.getElementById('btn-stop').onclick = stopScrape;
        document.getElementById('btn-schedule').onclick = () => {
            if (scheduleTimer) {
                stopSchedule();
            } else {
                startSchedule();
            }
        };
        document.getElementById('btn-test').onclick = testConnection;
        document.getElementById('collapse-btn').onclick = togglePanel;
    }

    let panelCollapsed = false;
    function togglePanel() {
        panelCollapsed = !panelCollapsed;
        document.getElementById('panel-content').style.display = panelCollapsed ? 'none' : 'block';
        document.getElementById('collapse-btn').textContent = panelCollapsed ? '+' : '−';
    }

    function updateStatus(text) {
        const el = document.getElementById('status');
        if (el) el.textContent = text;
    }

    function updateScheduleStatus(text) {
        const el = document.getElementById('schedule-status');
        if (el) el.textContent = text;
    }

    function updateProgress(current, total) {
        const el = document.getElementById('progress-fill');
        if (el) el.style.width = `${(current / total) * 100}%`;
        
        document.getElementById('scraped-count').textContent = scrapedCount;
        document.getElementById('saved-count').textContent = savedCount;
        document.getElementById('skipped-count').textContent = skippedCount;
    }

    function updateLog(msg) {
        const logEl = document.getElementById('log');
        if (!logEl) return;
        
        const entry = document.createElement('div');
        entry.className = 'log-entry';
        entry.textContent = `[${new Date().toLocaleTimeString()}] ${msg}`;
        logEl.insertBefore(entry, logEl.firstChild);
        
        // 限制日志条数
        while (logEl.children.length > 50) {
            logEl.removeChild(logEl.lastChild);
        }
    }

    async function testConnection() {
        updateStatus('测试中...');
        try {
            await sendToBackend('/market/tasks/pull', { worker_id: 'browser-test', limit: 1 });
            updateStatus('✓ 连接成功');
            notify('Ozon Scraper', '后端连接成功！');
        } catch (error) {
            updateStatus('✗ 连接失败');
            notify('Ozon Scraper', `连接失败: ${error.message}`);
        }
    }

    // ========== 初始化 ==========
    function init() {
        log('Ozon Auto Scraper v2.0 已加载');
        
        if (document.readyState === 'complete') {
            createPanel();
        } else {
            window.addEventListener('load', createPanel);
        }
    }

    init();
})();
