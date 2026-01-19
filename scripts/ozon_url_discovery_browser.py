"""
Ozon URL Discovery Worker - 使用真实浏览器引擎 (Playwright)
从分类页面发现商品URL并入队

安装依赖：
    pip install playwright
    playwright install chromium
    
运行：
    python scripts/ozon_url_discovery_browser.py --debug
"""
import argparse
import json
import os
import random
import re
import sys
import time
import urllib.error
import urllib.parse
import urllib.request

try:
    from playwright.sync_api import sync_playwright, TimeoutError as PlaywrightTimeout
    HAS_PLAYWRIGHT = True
except ImportError:
    HAS_PLAYWRIGHT = False
    print('WARNING: playwright not installed', file=sys.stderr)


def http_request(method, url, payload=None, timeout=20):
    """发送 HTTP 请求到后端 API"""
    data = None
    headers = {}
    if payload is not None:
        data = json.dumps(payload).encode('utf-8')
        headers['Content-Type'] = 'application/json'
    req = urllib.request.Request(url, data=data, method=method, headers=headers)
    with urllib.request.urlopen(req, timeout=timeout) as resp:
        body = resp.read()
        if not body:
            return {}
        return json.loads(body.decode('utf-8'))


TRACKING_PARAMS = {
    '_fr', '__rr', 'abt_att',
    'utm_source', 'utm_medium', 'utm_campaign', 'utm_content', 'utm_term',
    'gclid', 'yclid', 'ysclid',
}


def sanitize_seed_url(url):
    """清理 URL 中的跟踪参数"""
    parsed = urllib.parse.urlsplit(url)
    if not parsed.query:
        return url
    query = urllib.parse.parse_qsl(parsed.query, keep_blank_values=True)
    filtered = [(k, v) for k, v in query if k not in TRACKING_PARAMS]
    new_query = urllib.parse.urlencode(filtered)
    return urllib.parse.urlunsplit((parsed.scheme, parsed.netloc, parsed.path, new_query, parsed.fragment))


def normalize_product_url(url):
    """标准化商品 URL"""
    if url.startswith('//'):
        return 'https:' + url
    if url.startswith('/'):
        return 'https://www.ozon.ru' + url
    return url


def extract_product_urls(html):
    """从 HTML 中提取商品 URL"""
    urls = set()
    patterns = [
        r'href="(https?://[^"\s]+/product/[^"\s]+)"',
        r'href="(/product/[^"\s]+)"',
        r'"link"\s*:\s*"(https?://[^"\s]+/product/[^"\s]+)"',
        r'"url"\s*:\s*"(https?://[^"\s]+/product/[^"\s]+)"',
        r'"href"\s*:\s*"(/product/[^"\s]+)"',
    ]
    for pattern in patterns:
        for match in re.findall(pattern, html):
            url = normalize_product_url(match)
            if '/product/' in url:
                # 去掉查询参数
                urls.add(url.split('?')[0])
    return urls


def load_seeds(path):
    """加载种子 URL 文件"""
    with open(path, 'r', encoding='utf-8') as f:
        lines = [line.strip() for line in f if line.strip()]
    return [line for line in lines if not line.startswith('#')]


def load_cache(path):
    """加载已发现的 URL 缓存"""
    if not os.path.exists(path):
        return set()
    try:
        with open(path, 'r', encoding='utf-8') as f:
            data = json.load(f)
        return set(data if isinstance(data, list) else [])
    except Exception:
        return set()


def save_cache(path, urls):
    """保存 URL 缓存"""
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(sorted(urls), f)


def enqueue_tasks(base_url, urls, data_type, platform, market, timeout, batch_size=100):
    """将发现的 URL 入队到后端"""
    if not urls:
        return 0
    count = 0
    url_list = list(urls)
    for i in range(0, len(url_list), batch_size):
        batch = url_list[i:i + batch_size]
        payload = []
        for url in batch:
            payload.append({
                'platform': platform,
                'market': market,
                'url': url,
                'data_type': data_type,
                'priority': 0,
            })
        try:
            http_request('POST', base_url.rstrip('/') + '/market/tasks/enqueue', payload, timeout)
            count += len(batch)
        except Exception as e:
            print(f'enqueue failed: {e}', file=sys.stderr)
    return count


class BrowserScraper:
    """
    使用 Playwright 真实浏览器抓取
    """
    
    def __init__(self, headless=True, proxy=None, debug=False):
        self.headless = headless
        self.proxy = proxy
        self.debug = debug
        self.playwright = None
        self.browser = None
        self.context = None
        self.page = None
        self.warmed_up = False
    
    def start(self):
        """启动浏览器"""
        if not HAS_PLAYWRIGHT:
            raise RuntimeError('playwright not installed')
        
        self.playwright = sync_playwright().start()
        
        launch_args = {
            'headless': self.headless,
            'args': [
                '--disable-blink-features=AutomationControlled',
                '--disable-dev-shm-usage',
                '--no-sandbox',
            ]
        }
        if self.proxy:
            launch_args['proxy'] = {'server': self.proxy}
        
        self.browser = self.playwright.chromium.launch(**launch_args)
        
        self.context = self.browser.new_context(
            viewport={'width': 1920, 'height': 1080},
            locale='ru-RU',
            timezone_id='Europe/Moscow',
            user_agent='Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
        )
        
        # 注入脚本绕过检测
        self.context.add_init_script("""
            Object.defineProperty(navigator, 'webdriver', { get: () => undefined });
            Object.defineProperty(navigator, 'plugins', { get: () => [1, 2, 3, 4, 5] });
            Object.defineProperty(navigator, 'languages', { get: () => ['ru-RU', 'ru', 'en-US', 'en'] });
        """)
        
        self.page = self.context.new_page()
        
        if self.debug:
            print('browser started')
    
    def warmup(self, timeout=30000):
        """预热：访问首页获取 Cookie"""
        if self.warmed_up:
            return True
        
        try:
            if self.debug:
                print('warmup: navigating to https://www.ozon.ru/')
            
            self.page.goto('https://www.ozon.ru/', timeout=timeout, wait_until='networkidle')
            time.sleep(random.uniform(2, 4))
            
            # 模拟滚动
            self.page.evaluate('window.scrollTo(0, document.body.scrollHeight / 3)')
            time.sleep(random.uniform(1, 2))
            
            content = self.page.content()
            if 'captcha' in content.lower():
                if self.debug:
                    print('WARNING: captcha detected during warmup')
                return False
            
            cookies = self.context.cookies()
            if self.debug:
                print(f'warmup OK: cookies={[c["name"] for c in cookies]}')
            
            self.warmed_up = True
            return True
            
        except Exception as e:
            if self.debug:
                print(f'warmup FAILED: {e}')
            return False
    
    def fetch(self, url, timeout=30000, scroll=True):
        """
        抓取页面，支持滚动加载更多内容
        """
        try:
            if self.debug:
                print(f'fetching: {url}')
            
            self.page.goto(url, timeout=timeout, wait_until='networkidle')
            time.sleep(random.uniform(3, 5))
            
            # 滚动页面加载更多商品（多滚几次）
            if scroll:
                for i in range(5):
                    scroll_pos = (i + 1) / 6
                    self.page.evaluate(f'window.scrollTo(0, document.body.scrollHeight * {scroll_pos})')
                    if self.debug:
                        print(f'scrolled to {int(scroll_pos*100)}%')
                    time.sleep(random.uniform(1.5, 2.5))
                
                # 滚回顶部再滚一次（触发懒加载）
                self.page.evaluate('window.scrollTo(0, 0)')
                time.sleep(1)
                self.page.evaluate('window.scrollTo(0, document.body.scrollHeight)')
                time.sleep(2)
            
            html = self.page.content()
            
            if self.debug:
                print(f'fetched {len(html)} bytes')
                # 打印找到的链接数量（调试用）
                product_count = html.count('/product/')
                print(f'found {product_count} "/product/" occurrences in HTML')
            
            return html
            
        except PlaywrightTimeout:
            raise RuntimeError(f'timeout fetching {url}')
        except Exception as e:
            raise RuntimeError(f'error fetching {url}: {e}')
    
    def stop(self):
        """关闭浏览器"""
        if self.page:
            self.page.close()
        if self.context:
            self.context.close()
        if self.browser:
            self.browser.close()
        if self.playwright:
            self.playwright.stop()
        if self.debug:
            print('browser stopped')


def main():
    parser = argparse.ArgumentParser(description='Ozon URL discovery worker using Playwright')
    parser.add_argument('--base-url', default=os.getenv('MARKET_BASE_URL', 'http://localhost:8080'))
    parser.add_argument('--seed-file', default=os.getenv('SEED_FILE', 'scripts/ozon_seed_urls.txt'))
    parser.add_argument('--cache-file', default=os.getenv('CACHE_FILE', 'scripts/cache/ozon_product_urls.json'))
    parser.add_argument('--interval', type=int, default=int(os.getenv('POLL_INTERVAL', '900')), help='轮询间隔（秒）')
    parser.add_argument('--timeout', type=int, default=int(os.getenv('REQUEST_TIMEOUT', '30')))
    parser.add_argument('--data-type', default=os.getenv('DATA_TYPE', 'detail_page'))
    parser.add_argument('--platform', default=os.getenv('PLATFORM', 'ozon'))
    parser.add_argument('--market', default=os.getenv('MARKET', 'RU'))
    parser.add_argument('--proxy', default=os.getenv('HTTP_PROXY'), help='代理地址')
    parser.add_argument('--sleep-min', type=float, default=float(os.getenv('SLEEP_MIN', '2.0')))
    parser.add_argument('--sleep-max', type=float, default=float(os.getenv('SLEEP_MAX', '5.0')))
    parser.add_argument('--headless', action='store_true', default=True, help='无头模式')
    parser.add_argument('--no-headless', action='store_true', help='显示浏览器窗口')
    parser.add_argument('--once', action='store_true', help='只运行一次，不循环')
    parser.add_argument('--debug', action='store_true', help='打印调试信息')
    args = parser.parse_args()

    if not HAS_PLAYWRIGHT:
        print('ERROR: playwright not installed', file=sys.stderr)
        print('Run: pip install playwright && playwright install chromium', file=sys.stderr)
        sys.exit(1)

    if not os.path.exists(args.seed_file):
        print(f'seed file not found: {args.seed_file}', file=sys.stderr)
        sys.exit(1)

    headless = not args.no_headless
    scraper = BrowserScraper(headless=headless, proxy=args.proxy, debug=args.debug)
    
    try:
        print('starting browser...')
        scraper.start()
        
        print('performing warmup...')
        if scraper.warmup(args.timeout * 1000):
            print('warmup successful')
        else:
            print('warmup failed, continuing anyway...', file=sys.stderr)

        while True:
            seeds = load_seeds(args.seed_file)
            cache = load_cache(args.cache_file)
            discovered = set()

            print(f'processing {len(seeds)} seed URLs...')
            
            for seed_url in seeds:
                sanitized = sanitize_seed_url(seed_url)
                if args.debug and sanitized != seed_url:
                    print(f'sanitized: {seed_url} -> {sanitized}')
                
                try:
                    html = scraper.fetch(sanitized, args.timeout * 1000)
                    urls = extract_product_urls(html)
                    discovered.update(urls)
                    
                    if args.debug:
                        print(f'found {len(urls)} product URLs from {seed_url}')
                        if 'captcha' in html.lower() or 'access denied' in html.lower():
                            print('WARNING: possible captcha or block detected')
                            
                except Exception as e:
                    print(f'fetch failed: {seed_url} - {e}', file=sys.stderr)
                
                # 请求间隔
                sleep_time = random.uniform(args.sleep_min, args.sleep_max)
                if args.debug:
                    print(f'sleeping {sleep_time:.2f}s')
                time.sleep(sleep_time)

            # 入队新发现的 URL
            new_urls = discovered - cache
            print(f'discovered {len(discovered)} URLs, {len(new_urls)} are new')
            
            if new_urls:
                count = enqueue_tasks(args.base_url, new_urls, args.data_type, args.platform, args.market, args.timeout)
                print(f'enqueued {count} new URLs to {args.base_url}')
                cache.update(new_urls)
                save_cache(args.cache_file, cache)
            else:
                print('no new URLs to enqueue')

            if args.once:
                print('--once flag set, exiting')
                break
                
            print(f'waiting {args.interval}s before next round...')
            time.sleep(max(args.interval, 1))

    except KeyboardInterrupt:
        print('\nshutting down...')
    finally:
        scraper.stop()


if __name__ == '__main__':
    main()
