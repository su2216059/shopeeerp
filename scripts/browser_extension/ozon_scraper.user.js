// ==UserScript==
// @name         Ozon Product Scraper
// @namespace    http://tampermonkey.net/
// @version      1.0
// @description  在 Ozon 页面内抓取商品数据并发送到后端
// @author       ShopeeERP
// @match        https://www.ozon.ru/*
// @match        https://ozon.ru/*
// @grant        GM_xmlhttpRequest
// @grant        GM_notification
// @grant        GM_setValue
// @grant        GM_getValue
// @connect      localhost
// @connect      127.0.0.1
// ==/UserScript==

(function() {
    'use strict';

    // ========== 配置 ==========
    const CONFIG = {
        // 后端 API 地址
        backendUrl: 'http://localhost:8080',
        // 自动抓取间隔（毫秒）
        scrapeInterval: 5000,
        // 是否自动抓取当前页面
        autoScrape: false,
        // 是否显示悬浮按钮
        showFloatingButton: true,
    };

    // ========== 工具函数 ==========
    function log(msg) {
        console.log('[OzonScraper]', msg);
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
                        resolve(JSON.parse(response.responseText || '{}'));
                    } else {
                        reject(new Error(`HTTP ${response.status}`));
                    }
                },
                onerror: (error) => reject(error),
            });
        });
    }

    // ========== 数据提取 ==========
    function extractProductFromPage() {
        const url = window.location.href;

        // 检查是否是商品详情页
        if (!url.includes('/product/')) {
            return null;
        }

        // 提取商品 ID
        const productIdMatch = url.match(/product\/[^/]*-(\d+)/);
        const productId = productIdMatch ? productIdMatch[1] : null;

        if (!productId) {
            log('无法提取商品 ID');
            return null;
        }

        // 提取标题
        const titleEl = document.querySelector('h1') ||
                        document.querySelector('[data-widget="webProductHeading"]');
        const title = titleEl ? titleEl.textContent.trim() : null;

        // 提取价格
        let price = null;
        const priceEl = document.querySelector('[data-widget="webPrice"] span') ||
                        document.querySelector('.price') ||
                        document.querySelector('[class*="price"]');
        if (priceEl) {
            const priceText = priceEl.textContent.replace(/[^\d.,]/g, '').replace(',', '.');
            price = parseFloat(priceText) || null;
        }

        // 提取评分
        let rating = null;
        const ratingEl = document.querySelector('[data-widget="webReviewProductScore"]') ||
                         document.querySelector('[class*="rating"]');
        if (ratingEl) {
            const ratingMatch = ratingEl.textContent.match(/(\d[.,]\d)/);
            rating = ratingMatch ? parseFloat(ratingMatch[1].replace(',', '.')) : null;
        }

        // 提取评论数
        let reviewCount = null;
        const reviewEl = document.querySelector('[data-widget="webReviewProductScore"]') ||
                         document.querySelector('[class*="review"]');
        if (reviewEl) {
            const reviewMatch = reviewEl.textContent.match(/(\d+)/);
            reviewCount = reviewMatch ? parseInt(reviewMatch[1]) : null;
        }

        // 提取库存状态
        let availabilityStatus = 'unknown';
        const pageText = document.body.innerText.toLowerCase();
        if (pageText.includes('в корзину') || pageText.includes('добавить')) {
            availabilityStatus = 'in_stock';
        } else if (pageText.includes('нет в наличии') || pageText.includes('закончился')) {
            availabilityStatus = 'out_of_stock';
        }

        return {
            platform: 'ozon',
            market: 'RU',
            platform_product_id: productId,
            title: title,
            price: price,
            rating: rating,
            review_count: reviewCount,
            availability_status: availabilityStatus,
            snapshot_date: new Date().toISOString().split('T')[0],
            data_source: 'detail_page',
        };
    }

    function extractProductListFromPage() {
        const products = [];
        const productCards = document.querySelectorAll('[data-widget="searchResultsV2"] a[href*="/product/"]');

        productCards.forEach((card) => {
            const href = card.getAttribute('href');
            if (href && href.includes('/product/')) {
                const fullUrl = href.startsWith('/') ? 'https://www.ozon.ru' + href : href;
                const cleanUrl = fullUrl.split('?')[0];
                products.push(cleanUrl);
            }
        });

        // 去重
        return [...new Set(products)];
    }

    // ========== 操作函数 ==========
    async function scrapeCurrentPage() {
        const product = extractProductFromPage();
        if (!product) {
            log('当前页面不是商品详情页');
            notify('Ozon Scraper', '当前页面不是商品详情页');
            return;
        }

        log('抓取到商品:', product);

        try {
            const result = await sendToBackend('/market/snapshots/ingest', [product]);
            log('发送成功:', result);
            notify('Ozon Scraper', `商品 ${product.platform_product_id} 已保存`);
        } catch (error) {
            log('发送失败:', error);
            notify('Ozon Scraper', `发送失败: ${error.message}`);
        }
    }

    async function enqueueProductUrls() {
        const urls = extractProductListFromPage();
        if (urls.length === 0) {
            log('当前页面没有找到商品链接');
            notify('Ozon Scraper', '当前页面没有找到商品链接');
            return;
        }

        log(`找到 ${urls.length} 个商品链接`);

        const tasks = urls.map(url => ({
            platform: 'ozon',
            market: 'RU',
            url: url,
            data_type: 'detail_page',
            priority: 0,
        }));

        try {
            const result = await sendToBackend('/market/tasks/enqueue', tasks);
            log('入队成功:', result);
            notify('Ozon Scraper', `${urls.length} 个商品链接已入队`);
        } catch (error) {
            log('入队失败:', error);
            notify('Ozon Scraper', `入队失败: ${error.message}`);
        }
    }

    // ========== UI ==========
    function createFloatingButton() {
        const container = document.createElement('div');
        container.id = 'ozon-scraper-panel';
        container.innerHTML = `
            <style>
                #ozon-scraper-panel {
                    position: fixed;
                    bottom: 20px;
                    right: 20px;
                    z-index: 999999;
                    font-family: Arial, sans-serif;
                }
                #ozon-scraper-panel .panel {
                    background: #005bff;
                    border-radius: 8px;
                    padding: 10px;
                    box-shadow: 0 4px 12px rgba(0,0,0,0.3);
                    color: white;
                }
                #ozon-scraper-panel button {
                    display: block;
                    width: 100%;
                    padding: 8px 16px;
                    margin: 5px 0;
                    border: none;
                    border-radius: 4px;
                    background: white;
                    color: #005bff;
                    font-size: 14px;
                    cursor: pointer;
                    transition: background 0.2s;
                }
                #ozon-scraper-panel button:hover {
                    background: #e0e0e0;
                }
                #ozon-scraper-panel .title {
                    font-weight: bold;
                    margin-bottom: 10px;
                    text-align: center;
                }
                #ozon-scraper-panel .status {
                    font-size: 12px;
                    margin-top: 10px;
                    padding: 5px;
                    background: rgba(255,255,255,0.2);
                    border-radius: 4px;
                }
            </style>
            <div class="panel">
                <div class="title">🛒 Ozon Scraper</div>
                <button id="btn-scrape-product">抓取当前商品</button>
                <button id="btn-enqueue-list">入队页面商品链接</button>
                <button id="btn-test-connection">测试后端连接</button>
                <div class="status" id="scraper-status">就绪</div>
            </div>
        `;

        document.body.appendChild(container);

        // 绑定事件
        document.getElementById('btn-scrape-product').addEventListener('click', async () => {
            setStatus('抓取中...');
            await scrapeCurrentPage();
            setStatus('完成');
        });

        document.getElementById('btn-enqueue-list').addEventListener('click', async () => {
            setStatus('入队中...');
            await enqueueProductUrls();
            setStatus('完成');
        });

        document.getElementById('btn-test-connection').addEventListener('click', async () => {
            setStatus('测试连接...');
            try {
                await sendToBackend('/market/tasks/pull', { worker_id: 'browser-test', limit: 1 });
                setStatus('✓ 连接成功');
                notify('Ozon Scraper', '后端连接成功！');
            } catch (error) {
                setStatus('✗ 连接失败');
                notify('Ozon Scraper', `连接失败: ${error.message}`);
            }
        });
    }

    function setStatus(text) {
        const el = document.getElementById('scraper-status');
        if (el) el.textContent = text;
    }

    // ========== 初始化 ==========
    function init() {
        log('Ozon Scraper 已加载');
        log('后端地址:', CONFIG.backendUrl);

        if (CONFIG.showFloatingButton) {
            // 等待页面加载完成
            if (document.readyState === 'complete') {
                createFloatingButton();
            } else {
                window.addEventListener('load', createFloatingButton);
            }
        }
    }

    init();
})();
