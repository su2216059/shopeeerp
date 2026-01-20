// ==UserScript==
// @name         Ozon Auto Scraper (自动批量抓取)
// @namespace    http://tampermonkey.net/
// @version      2.3
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
        // 分类切换间隔（毫秒）
        categoryDelay: 5000,
        // 定时任务间隔（小时）- 每天每隔4小时抓取一次
        scheduleIntervalHours: 4,
        // 每个分类最多抓取多少个商品
        maxProductsPerCategory: 100,
        // 每个分类最多翻多少页
        maxPagesPerCategory: 5,
        // 只保存有评论/销量数据的商品
        onlySaveWithSales: true,
        // 最小评论数（作为销量参考）
        minReviewCount: 1,
    };

    // ========== 分类URL列表 ==========
    // 在这里配置要抓取的分类URL
    // 可以从 Ozon 网站复制分类链接
    let CATEGORY_URLS = [
        // 示例分类（请替换为实际需要抓取的分类）
        // 'https://www.ozon.ru/category/smartfony-15502/',           // 智能手机
        // 'https://www.ozon.ru/category/noutbuki-15692/',            // 笔记本电脑
        // 'https://www.ozon.ru/category/televizory-15754/',          // 电视
        // 'https://www.ozon.ru/category/naushniki-i-garnitury-15542/', // 耳机
    ];

    // 从本地存储加载分类列表
    function loadCategoryUrls() {
        const saved = GM_getValue('categoryUrls', null);
        if (saved) {
            try {
                CATEGORY_URLS = JSON.parse(saved);
            } catch (e) {
                console.error('Failed to parse saved category URLs', e);
            }
        }
    }

    // 保存分类列表到本地存储
    function saveCategoryUrls() {
        GM_setValue('categoryUrls', JSON.stringify(CATEGORY_URLS));
    }

    // ========== 状态管理 ==========
    let isRunning = false;
    let isPaused = false;
    let currentTask = null;
    let scrapedCount = 0;
    let savedCount = 0;
    let skippedCount = 0;
    let productQueue = [];
    
    // 分类遍历状态
    let currentCategoryIndex = 0;
    let currentPage = 1;
    let isCategoryMode = false;  // 是否在分类遍历模式
    let totalCategories = 0;

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

        // 品牌 - 没有就显示空字符串
        let brand = '';
        const brandSelectors = [
            '[data-widget="webBrand"] a',
            '[data-widget="webBrand"] span',
            'a[href*="/brand/"]',
            '[class*="brand"] a',
            '[class*="Brand"] a',
        ];
        for (const selector of brandSelectors) {
            const brandEl = document.querySelector(selector);
            if (brandEl) {
                const text = brandEl.textContent.trim();
                if (text && text.length > 0 && text.length < 100) {
                    brand = text;
                    break;
                }
            }
        }

        // 分类/类目 - 从面包屑导航提取
        let categoryId = '';
        let categoryPath = '';
        const breadcrumbSelectors = [
            '[data-widget="breadcrumbs"] a',
            'nav[aria-label="breadcrumb"] a',
            '[class*="breadcrumb"] a',
            '[class*="Breadcrumb"] a',
        ];
        for (const selector of breadcrumbSelectors) {
            const breadcrumbs = document.querySelectorAll(selector);
            if (breadcrumbs.length > 0) {
                const categories = Array.from(breadcrumbs)
                    .map(a => a.textContent.trim())
                    .filter(t => t && t !== 'Главная' && t !== 'OZON');
                if (categories.length > 0) {
                    categoryPath = categories.join(' > ');
                    // 尝试从链接提取分类ID
                    const lastBreadcrumb = breadcrumbs[breadcrumbs.length - 1];
                    const href = lastBreadcrumb?.getAttribute('href') || '';
                    const categoryMatch = href.match(/category\/[^/]*-(\d+)/);
                    if (categoryMatch) {
                        categoryId = categoryMatch[1];
                    }
                    break;
                }
            }
        }

        return {
            platform: 'ozon',
            market: 'RU',
            platform_product_id: productId,
            title: title || '',
            brand: brand,  // 没有就是空字符串
            category_id: categoryId,  // 分类ID
            category_path: categoryPath,  // 分类路径
            price,
            rating,
            review_count: reviewCount,
            estimated_sales: estimatedSales,
            availability_status: availabilityStatus,
            snapshot_date: new Date().toISOString().split('T')[0],
            data_source: 'detail_page',
        };
    }

    // 提取商品链接（带排名位置）
    function extractProductLinksFromPage() {
        const links = new Map(); // url -> rank
        let rank = 1;
        
        // 商品卡片容器选择器
        const cardSelectors = [
            '[data-widget="searchResultsV2"] > div > div',
            '[data-widget="skuGrid"] > div > div',
            '[class*="tile-root"]',
            '[class*="product-card"]',
            '[class*="tsBody500Medium"]',
        ];
        
        // 首先尝试从商品卡片获取（有序）
        for (const containerSelector of cardSelectors) {
            const cards = document.querySelectorAll(containerSelector);
            cards.forEach(card => {
                const link = card.querySelector('a[href*="/product/"]');
                if (link) {
                    const href = link.getAttribute('href');
                    if (href && href.includes('/product/')) {
                        const fullUrl = href.startsWith('/') ? 'https://www.ozon.ru' + href : href;
                        const cleanUrl = fullUrl.split('?')[0];
                        if (!links.has(cleanUrl)) {
                            links.set(cleanUrl, rank++);
                        }
                    }
                }
            });
            if (links.size > 0) break;
        }
        
        // 备用方法：直接找所有商品链接
        if (links.size === 0) {
            document.querySelectorAll('a[href*="/product/"]').forEach(el => {
                const href = el.getAttribute('href');
                if (href && href.includes('/product/')) {
                    const fullUrl = href.startsWith('/') ? 'https://www.ozon.ru' + href : href;
                    const cleanUrl = fullUrl.split('?')[0];
                    if (!links.has(cleanUrl)) {
                        links.set(cleanUrl, rank++);
                    }
                }
            });
        }
        
        // 返回带排名的数组 [{url, rank}]
        return Array.from(links.entries()).map(([url, rank]) => ({ url, rank }));
    }
    
    // 获取当前页面的分类ID（用于标记category_rank）
    function getCurrentCategoryId() {
        const url = window.location.href;
        const match = url.match(/category\/[^/]*-(\d+)/);
        return match ? match[1] : null;
    }
    
    // 判断是搜索页还是分类页
    function getPageType() {
        const url = window.location.href;
        if (url.includes('/search/') || url.includes('?text=')) {
            return 'search';
        } else if (url.includes('/category/')) {
            return 'category';
        }
        return 'other';
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
        // 收集当前页面的商品链接（带排名）
        let allProducts = extractProductLinksFromPage();
        const pageType = getPageType();
        const categoryId = getCurrentCategoryId();
        
        log(`当前页面发现 ${allProducts.length} 个商品 (类型:${pageType}, 分类:${categoryId || '-'})`);

        // 限制数量
        if (allProducts.length > CONFIG.maxProductsPerCategory) {
            allProducts = allProducts.slice(0, CONFIG.maxProductsPerCategory);
            log(`限制为 ${CONFIG.maxProductsPerCategory} 个`);
        }

        productQueue = [...allProducts];
        
        // 计算当前页的基础排名（第2页从第N+1开始）
        const baseRank = (currentPage - 1) * 36; // Ozon每页约36个商品
        
        // 逐个打开商品页面抓取
        for (let i = 0; i < productQueue.length; i++) {
            if (!isRunning || isPaused) break;

            const { url: productUrl, rank: pageRank } = productQueue[i];
            const absoluteRank = baseRank + pageRank; // 绝对排名
            
            updateStatus(`抓取中 ${i + 1}/${productQueue.length} (排名:${absoluteRank})`);
            updateProgress(i + 1, productQueue.length);

            try {
                // 使用 fetch 获取商品页面
                const html = await fetchProductPage(productUrl);
                const product = parseProductFromHtml(html, productUrl);
                
                if (product) {
                    // 添加排名信息
                    if (pageType === 'category') {
                        product.category_rank = absoluteRank;
                        product.data_source = 'category_page';
                    } else if (pageType === 'search') {
                        product.search_rank = absoluteRank;
                        product.data_source = 'search_page';
                    }
                    
                    scrapedCount++;
                    
                    // 检查是否有销量数据
                    if (CONFIG.onlySaveWithSales && (!product.review_count || product.review_count < CONFIG.minReviewCount)) {
                        log(`跳过 #${absoluteRank}: ${product.platform_product_id} (评论:${product.review_count || 0})`);
                        skippedCount++;
                    } else {
                        try {
                            await sendToBackend('/market/snapshots/ingest', [product]);
                            savedCount++;
                            const rankInfo = product.category_rank ? `分类排名:${product.category_rank}` : `搜索排名:${product.search_rank}`;
                            log(`✓ #${absoluteRank} ${product.platform_product_id} | ${rankInfo} | 评论:${product.review_count}`);
                        } catch (error) {
                            log(`保存失败: ${error.message}`);
                        }
                    }
                }
            } catch (error) {
                log(`抓取失败 #${absoluteRank}: ${error.message}`);
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
        const title = titleEl ? titleEl.textContent.trim() : '';

        // 价格
        let price = null;
        const priceMatch = html.match(/"price":\s*"?(\d+)/) ||
                          html.match(/"finalPrice":\s*"?(\d+)/);
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

        // 品牌 - 没有就显示空字符串
        let brand = '';
        const brandMatch = html.match(/"brand":\s*"([^"]+)"/) ||
                          html.match(/"brandName":\s*"([^"]+)"/);
        if (brandMatch) {
            brand = brandMatch[1];
        } else {
            // 从 DOM 提取
            const brandEl = doc.querySelector('[data-widget="webBrand"] a, a[href*="/brand/"]');
            if (brandEl) {
                brand = brandEl.textContent.trim();
            }
        }

        // 分类 - 从面包屑或 JSON 提取
        let categoryId = '';
        let categoryPath = '';
        
        // 从 JSON 提取
        const categoryMatch = html.match(/"categoryPath":\s*"([^"]+)"/) ||
                             html.match(/"category":\s*"([^"]+)"/);
        if (categoryMatch) {
            categoryPath = categoryMatch[1];
        }
        
        // 从 DOM 面包屑提取
        if (!categoryPath) {
            const breadcrumbs = doc.querySelectorAll('[data-widget="breadcrumbs"] a, nav a');
            if (breadcrumbs.length > 0) {
                const categories = Array.from(breadcrumbs)
                    .map(a => a.textContent.trim())
                    .filter(t => t && t !== 'Главная' && t !== 'OZON' && t.length < 50);
                if (categories.length > 0) {
                    categoryPath = categories.join(' > ');
                }
            }
        }

        // 提取分类ID
        const categoryIdMatch = html.match(/"categoryId":\s*"?(\d+)/) ||
                               url.match(/category\/[^/]*-(\d+)/);
        if (categoryIdMatch) {
            categoryId = categoryIdMatch[1];
        }

        return {
            platform: 'ozon',
            market: 'RU',
            platform_product_id: productId,
            title: title,
            brand: brand,  // 没有就是空字符串
            category_id: categoryId,
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

    function stopScrape() {
        isRunning = false;
        isPaused = false;
        isCategoryMode = false;
        currentCategoryIndex = 0;
        currentPage = 1;
        // 清除保存的进度
        GM_setValue('categoryProgress', null);
        log('已停止');
        updateStatus('已停止');
        updateCategoryStatus('--');
    }

    function pauseScrape() {
        isPaused = !isPaused;
        log(isPaused ? '已暂停' : '继续运行');
        updateStatus(isPaused ? '已暂停' : '运行中');
    }

    // ========== 分类遍历模式 ==========
    
    // 开始分类遍历
    async function startCategoryScrape() {
        if (CATEGORY_URLS.length === 0) {
            log('请先配置分类URL列表！点击"编辑分类"按钮添加');
            notify('Ozon Scraper', '请先配置分类URL');
            return;
        }

        if (isRunning) {
            log('已经在运行中');
            return;
        }

        isCategoryMode = true;
        isRunning = true;
        isPaused = false;
        scrapedCount = 0;
        savedCount = 0;
        skippedCount = 0;
        totalCategories = CATEGORY_URLS.length;

        // 检查是否有保存的进度
        const savedProgress = GM_getValue('categoryProgress', null);
        if (savedProgress) {
            try {
                const progress = JSON.parse(savedProgress);
                currentCategoryIndex = progress.categoryIndex || 0;
                currentPage = 1;
                log(`从上次进度恢复: 分类 ${currentCategoryIndex + 1}/${totalCategories}`);
            } catch (e) {
                currentCategoryIndex = 0;
                currentPage = 1;
            }
        } else {
            currentCategoryIndex = 0;
            currentPage = 1;
        }

        log(`开始分类遍历模式，共 ${totalCategories} 个分类`);
        updateStatus('分类遍历中');
        
        // 跳转到第一个分类
        await navigateToCategory(currentCategoryIndex);
    }

    // 跳转到指定分类
    async function navigateToCategory(index) {
        if (index >= CATEGORY_URLS.length) {
            // 所有分类已完成
            log(`所有 ${totalCategories} 个分类已完成！`);
            notify('Ozon Scraper', `分类遍历完成！共保存 ${savedCount} 个商品`);
            isRunning = false;
            isCategoryMode = false;
            GM_setValue('categoryProgress', null);
            updateStatus(`完成 (${savedCount}个商品)`);
            updateCategoryStatus('全部完成');
            return;
        }

        const url = CATEGORY_URLS[index];
        currentCategoryIndex = index;
        currentPage = 1;

        // 保存进度
        GM_setValue('categoryProgress', JSON.stringify({
            categoryIndex: index,
            timestamp: Date.now()
        }));

        log(`跳转到分类 ${index + 1}/${totalCategories}: ${url}`);
        updateCategoryStatus(`${index + 1}/${totalCategories}`);
        
        // 跳转页面
        window.location.href = url;
    }

    // 页面加载后继续抓取（在分类遍历模式下）
    async function continueCategroyScrape() {
        if (!isCategoryMode || !isRunning) return;

        const url = window.location.href;
        
        // 确认在分类页
        if (!url.includes('/category/')) {
            log('不在分类页面，等待跳转...');
            return;
        }

        log(`开始抓取分类 ${currentCategoryIndex + 1}/${totalCategories}，第 ${currentPage} 页`);
        updateStatus(`分类 ${currentCategoryIndex + 1}/${totalCategories} | 第${currentPage}页`);

        // 等待页面加载
        await sleep(2000);

        // 滚动加载更多商品
        await scrollToLoadMore();

        // 抓取当前页商品
        const productsScraped = await scrapeCurrentCategoryPage();

        // 检查是否需要翻页
        if (currentPage < CONFIG.maxPagesPerCategory && productsScraped > 0) {
            const hasNextPage = await goToNextPage();
            if (hasNextPage) {
                currentPage++;
                log(`翻到第 ${currentPage} 页...`);
                // 页面会刷新，onload 时继续
                return;
            }
        }

        // 当前分类完成，跳转下一个
        log(`分类 ${currentCategoryIndex + 1} 完成，${CONFIG.categoryDelay / 1000}秒后跳转下一个...`);
        await sleep(CONFIG.categoryDelay);
        
        await navigateToCategory(currentCategoryIndex + 1);
    }

    // 抓取当前分类页的商品
    async function scrapeCurrentCategoryPage() {
        let allProducts = extractProductLinksFromPage();
        const pageType = getPageType();
        const categoryId = getCurrentCategoryId();
        
        log(`当前页发现 ${allProducts.length} 个商品 (分类ID:${categoryId || '-'})`);

        if (allProducts.length === 0) {
            return 0;
        }

        // 限制每页数量
        const maxPerPage = Math.min(allProducts.length, Math.floor(CONFIG.maxProductsPerCategory / CONFIG.maxPagesPerCategory));
        allProducts = allProducts.slice(0, maxPerPage);

        // 计算当前页的基础排名
        const baseRank = (currentPage - 1) * 36;

        let pageScraped = 0;

        for (let i = 0; i < allProducts.length; i++) {
            if (!isRunning || isPaused) break;

            const { url: productUrl, rank: pageRank } = allProducts[i];
            const absoluteRank = baseRank + pageRank;
            
            updateStatus(`分类${currentCategoryIndex + 1} | 页${currentPage} | #${absoluteRank}`);
            updateProgress(i + 1, allProducts.length);

            try {
                const html = await fetchProductPage(productUrl);
                const product = parseProductFromHtml(html, productUrl);
                
                if (product) {
                    // 添加排名信息
                    if (pageType === 'category') {
                        product.category_rank = absoluteRank;
                        product.data_source = 'category_page';
                    } else if (pageType === 'search') {
                        product.search_rank = absoluteRank;
                        product.data_source = 'search_page';
                    }
                    
                    scrapedCount++;
                    pageScraped++;
                    
                    if (CONFIG.onlySaveWithSales && (!product.review_count || product.review_count < CONFIG.minReviewCount)) {
                        log(`跳过 #${absoluteRank}: ${product.platform_product_id} (评论:${product.review_count || 0})`);
                        skippedCount++;
                    } else {
                        try {
                            await sendToBackend('/market/snapshots/ingest', [product]);
                            savedCount++;
                            log(`✓ #${absoluteRank} ${product.platform_product_id} | ${product.brand || '-'} | ${product.review_count}评`);
                        } catch (error) {
                            log(`保存失败: ${error.message}`);
                        }
                    }
                }
            } catch (error) {
                log(`抓取失败 #${absoluteRank}: ${error.message}`);
            }

            // 更新统计
            document.getElementById('scraped-count').textContent = scrapedCount;
            document.getElementById('saved-count').textContent = savedCount;
            document.getElementById('skipped-count').textContent = skippedCount;

            await sleep(CONFIG.scrapeDelay);
        }

        return pageScraped;
    }

    // 滚动页面加载更多商品
    async function scrollToLoadMore() {
        log('滚动加载更多商品...');
        
        for (let i = 0; i < 5; i++) {
            window.scrollTo(0, document.body.scrollHeight * (i + 1) / 5);
            await sleep(500);
        }
        
        // 回到顶部
        window.scrollTo(0, 0);
        await sleep(500);
    }

    // 翻页
    async function goToNextPage() {
        // 查找下一页按钮或链接
        const nextPageSelectors = [
            'a[href*="page="][href*="' + (currentPage + 1) + '"]',
            'a[data-page="' + (currentPage + 1) + '"]',
            '[class*="pagination"] a:last-child',
            'button[class*="next"]',
            'a[class*="next"]',
        ];

        for (const selector of nextPageSelectors) {
            const nextBtn = document.querySelector(selector);
            if (nextBtn) {
                const href = nextBtn.getAttribute('href');
                if (href) {
                    // 有链接，直接跳转
                    log(`找到下一页链接: ${href}`);
                    window.location.href = href.startsWith('/') ? 'https://www.ozon.ru' + href : href;
                    return true;
                } else {
                    // 按钮，点击
                    nextBtn.click();
                    await sleep(CONFIG.pageDelay);
                    return true;
                }
            }
        }

        // 尝试修改 URL 参数
        const url = new URL(window.location.href);
        const currentUrlPage = parseInt(url.searchParams.get('page')) || 1;
        if (currentUrlPage < CONFIG.maxPagesPerCategory) {
            url.searchParams.set('page', currentUrlPage + 1);
            log(`通过URL翻页: ${url.toString()}`);
            window.location.href = url.toString();
            return true;
        }

        log('未找到下一页');
        return false;
    }

    // 编辑分类URL列表
    function openCategoryEditor() {
        const modal = document.createElement('div');
        modal.id = 'category-editor-modal';
        modal.innerHTML = `
            <div style="position:fixed;top:0;left:0;right:0;bottom:0;background:rgba(0,0,0,0.7);z-index:9999999;display:flex;align-items:center;justify-content:center;">
                <div style="background:white;border-radius:12px;padding:20px;width:500px;max-height:80vh;overflow:auto;">
                    <h3 style="margin:0 0 15px 0;color:#333;">编辑分类URL列表</h3>
                    <p style="color:#666;font-size:12px;margin-bottom:10px;">每行一个分类URL，从 Ozon 网站复制分类链接</p>
                    <textarea id="category-urls-input" style="width:100%;height:300px;padding:10px;border:1px solid #ddd;border-radius:8px;font-size:13px;font-family:monospace;">${CATEGORY_URLS.join('\n')}</textarea>
                    <div style="display:flex;gap:10px;margin-top:15px;">
                        <button id="save-categories" style="flex:1;padding:10px;background:#005bff;color:white;border:none;border-radius:8px;cursor:pointer;">保存</button>
                        <button id="cancel-categories" style="flex:1;padding:10px;background:#eee;color:#333;border:none;border-radius:8px;cursor:pointer;">取消</button>
                    </div>
                    <div style="margin-top:10px;padding:10px;background:#f5f5f5;border-radius:8px;font-size:11px;color:#666;">
                        <strong>示例URL:</strong><br>
                        https://www.ozon.ru/category/smartfony-15502/<br>
                        https://www.ozon.ru/category/noutbuki-15692/
                    </div>
                </div>
            </div>
        `;
        document.body.appendChild(modal);

        document.getElementById('save-categories').onclick = () => {
            const input = document.getElementById('category-urls-input').value;
            CATEGORY_URLS = input.split('\n')
                .map(url => url.trim())
                .filter(url => url && url.includes('ozon.ru'));
            saveCategoryUrls();
            log(`已保存 ${CATEGORY_URLS.length} 个分类URL`);
            modal.remove();
        };

        document.getElementById('cancel-categories').onclick = () => {
            modal.remove();
        };
    }

    // ========== 定时任务 ==========
    let scheduleTimer = null;
    let nextRunTime = null;

    function startSchedule() {
        if (scheduleTimer) {
            clearInterval(scheduleTimer);
        }
        
        // 每 4 小时执行一次
        const intervalMs = CONFIG.scheduleIntervalHours * 60 * 60 * 1000;
        
        // 计算下次执行时间
        nextRunTime = new Date(Date.now() + intervalMs);
        
        // 立即执行一次
        log(`定时任务已启动，每 ${CONFIG.scheduleIntervalHours} 小时执行一次`);
        log('立即执行第一次抓取...');
        startAutoScrape();
        
        // 设置定时器
        scheduleTimer = setInterval(() => {
            const now = new Date();
            log(`定时任务触发 (每 ${CONFIG.scheduleIntervalHours} 小时) - ${now.toLocaleString()}`);
            nextRunTime = new Date(Date.now() + intervalMs);
            updateScheduleStatus(`每${CONFIG.scheduleIntervalHours}h | 下次: ${nextRunTime.toLocaleTimeString()}`);
            startAutoScrape();
        }, intervalMs);

        updateScheduleStatus(`每${CONFIG.scheduleIntervalHours}h | 下次: ${nextRunTime.toLocaleTimeString()}`);
        
        // 保存定时状态到本地存储
        GM_setValue('scheduleEnabled', true);
        GM_setValue('lastRunTime', Date.now());
    }

    function stopSchedule() {
        if (scheduleTimer) {
            clearInterval(scheduleTimer);
            scheduleTimer = null;
        }
        nextRunTime = null;
        log('定时任务已停止');
        updateScheduleStatus('未启动');
        GM_setValue('scheduleEnabled', false);
    }
    
    // 显示下次执行倒计时
    function updateCountdown() {
        if (!nextRunTime) return;
        
        const now = Date.now();
        const remaining = nextRunTime.getTime() - now;
        
        if (remaining > 0) {
            const hours = Math.floor(remaining / (60 * 60 * 1000));
            const minutes = Math.floor((remaining % (60 * 60 * 1000)) / (60 * 1000));
            updateScheduleStatus(`每${CONFIG.scheduleIntervalHours}h | ${hours}h${minutes}m后执行`);
        }
    }
    
    // 每分钟更新倒计时
    setInterval(updateCountdown, 60000);

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
                <div class="title">🛒 Ozon Auto Scraper v2.2</div>
                
                <div id="panel-content">
                    <div class="btn-row">
                        <button class="primary" id="btn-start">▶ 当前页</button>
                        <button class="primary" id="btn-category">📁 遍历分类</button>
                    </div>
                    
                    <div class="btn-row">
                        <button class="secondary" id="btn-edit-category">✏️ 编辑分类</button>
                        <button class="danger" id="btn-stop">■ 停止</button>
                    </div>
                    
                    <div class="btn-row">
                        <button class="secondary" id="btn-schedule">⏰ 定时(每${CONFIG.scheduleIntervalHours}h)</button>
                        <button class="secondary" id="btn-test">🔗 测试</button>
                    </div>
                    
                    <div class="stats">
                        <div class="stat-row">
                            <span>状态:</span>
                            <span id="status">就绪</span>
                        </div>
                        <div class="stat-row">
                            <span>分类进度:</span>
                            <span id="category-status">--</span>
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
                            <span>跳过:</span>
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
        document.getElementById('btn-category').onclick = startCategoryScrape;
        document.getElementById('btn-edit-category').onclick = openCategoryEditor;
        document.getElementById('btn-stop').onclick = stopScrape;
        document.getElementById('btn-schedule').onclick = () => {
            if (scheduleTimer) {
                stopSchedule();
            } else {
                startScheduleWithCategories();
            }
        };
        document.getElementById('btn-test').onclick = testConnection;
        document.getElementById('collapse-btn').onclick = togglePanel;
    }
    
    // 定时任务使用分类遍历模式
    function startScheduleWithCategories() {
        if (CATEGORY_URLS.length === 0) {
            log('请先配置分类URL！');
            notify('Ozon Scraper', '请先配置分类URL');
            return;
        }
        
        if (scheduleTimer) {
            clearInterval(scheduleTimer);
        }
        
        const intervalMs = CONFIG.scheduleIntervalHours * 60 * 60 * 1000;
        nextRunTime = new Date(Date.now() + intervalMs);
        
        log(`定时分类遍历已启动，每 ${CONFIG.scheduleIntervalHours} 小时执行一次`);
        log(`共 ${CATEGORY_URLS.length} 个分类`);
        
        // 立即执行一次
        startCategoryScrape();
        
        scheduleTimer = setInterval(() => {
            log(`定时任务触发 - ${new Date().toLocaleString()}`);
            nextRunTime = new Date(Date.now() + intervalMs);
            updateScheduleStatus(`每${CONFIG.scheduleIntervalHours}h | 下次: ${nextRunTime.toLocaleTimeString()}`);
            startCategoryScrape();
        }, intervalMs);

        updateScheduleStatus(`每${CONFIG.scheduleIntervalHours}h | 下次: ${nextRunTime.toLocaleTimeString()}`);
        GM_setValue('scheduleEnabled', true);
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
    
    function updateCategoryStatus(text) {
        const el = document.getElementById('category-status');
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
        // 加载保存的分类URL
        loadCategoryUrls();
        
        log(`Ozon Auto Scraper v2.2 已加载`);
        log(`分类数: ${CATEGORY_URLS.length} | 定时: 每${CONFIG.scheduleIntervalHours}小时`);
        
        if (document.readyState === 'complete') {
            createPanel();
            checkAndResume();
        } else {
            window.addEventListener('load', () => {
                createPanel();
                checkAndResume();
            });
        }
    }
    
    // 检查并恢复之前的状态
    function checkAndResume() {
        // 检查是否在分类遍历中
        const savedProgress = GM_getValue('categoryProgress', null);
        if (savedProgress) {
            try {
                const progress = JSON.parse(savedProgress);
                // 检查是否是最近30分钟内的进度（防止旧进度干扰）
                if (Date.now() - progress.timestamp < 30 * 60 * 1000) {
                    currentCategoryIndex = progress.categoryIndex || 0;
                    totalCategories = CATEGORY_URLS.length;
                    isCategoryMode = true;
                    isRunning = true;
                    
                    log(`恢复分类遍历: ${currentCategoryIndex + 1}/${totalCategories}`);
                    updateCategoryStatus(`${currentCategoryIndex + 1}/${totalCategories}`);
                    
                    // 延迟后继续抓取
                    setTimeout(() => {
                        continueCategroyScrape();
                    }, 3000);
                    return;
                }
            } catch (e) {
                console.error('Failed to restore progress', e);
            }
        }
        
        // 检查定时任务
        const scheduleEnabled = GM_getValue('scheduleEnabled', false);
        if (scheduleEnabled) {
            log('检测到定时任务已启用');
            // 不自动恢复定时，需要用户手动启动
        }
    }

    init();
})();
