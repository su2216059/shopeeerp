"""
Market Scrape Worker - 使用真实浏览器引擎 (Playwright)
解决 TLS 指纹 + JavaScript Challenge 问题

安装依赖：
    pip install playwright
    playwright install chromium
    
运行：
    python scripts/market_scrape_worker_browser.py --debug
"""
import argparse
import datetime as dt
from datetime import UTC
import hashlib
import json
import os
import random
import re
import sys
import time
import urllib.error
import urllib.request

try:
    from playwright.sync_api import sync_playwright, TimeoutError as PlaywrightTimeout
    HAS_PLAYWRIGHT = True
except ImportError:
    HAS_PLAYWRIGHT = False
    print('WARNING: playwright not installed, run: pip install playwright && playwright install chromium', file=sys.stderr)


def build_url(base, path):
    return base.rstrip('/') + '/' + path.lstrip('/')


def http_request(method, url, payload=None, timeout=20):
    data = None
    headers = {}
    if payload is not None:
        data = json.dumps(payload).encode('utf-8')
        headers['Content-Type'] = 'application/json'
    req = urllib.request.Request(url, data=data, method=method, headers=headers)
    try:
        with urllib.request.urlopen(req, timeout=timeout) as resp:
            body = resp.read()
            if not body:
                return {}
            try:
                return json.loads(body.decode('utf-8'))
            except Exception:
                return {'raw': body.decode('utf-8', errors='ignore')}
    except urllib.error.HTTPError as e:
        body = e.read().decode('utf-8', errors='ignore')
        raise RuntimeError(f'HTTP {e.code} {e.reason}: {body}')


def extract_product_id(url, html):
    patterns = [
        r'/product/[^/]*-(\d+)',
        r'/product/(\d+)',
        r'"product_id"\s*:\s*"?(\d+)',
        r'"productId"\s*:\s*"?(\d+)',
    ]
    for pattern in patterns:
        match = re.search(pattern, url)
        if match:
            return match.group(1)
    for pattern in patterns:
        match = re.search(pattern, html)
        if match:
            return match.group(1)
    return None


def extract_title(html):
    match = re.search(r'<title>(.*?)</title>', html, re.IGNORECASE | re.DOTALL)
    if not match:
        return None
    title = re.sub(r'\s+', ' ', match.group(1)).strip()
    return title or None


def fallback_id(url):
    return hashlib.md5(url.encode('utf-8')).hexdigest()


def now_iso():
    return dt.datetime.now(UTC).replace(microsecond=0).isoformat() + 'Z'


class BrowserScraper:
    """
    使用 Playwright 真实浏览器抓取
    - 绕过 TLS 指纹检测
    - 自动执行 JavaScript
    - 自动处理 Cloudflare Challenge
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
        
        # 浏览器启动参数
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
        
        # 创建浏览器上下文（模拟真实用户）
        self.context = self.browser.new_context(
            viewport={'width': 1920, 'height': 1080},
            locale='ru-RU',
            timezone_id='Europe/Moscow',
            user_agent='Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
        )
        
        # 注入脚本绕过检测
        self.context.add_init_script("""
            // 隐藏 webdriver 标记
            Object.defineProperty(navigator, 'webdriver', { get: () => undefined });
            
            // 伪装 plugins
            Object.defineProperty(navigator, 'plugins', {
                get: () => [1, 2, 3, 4, 5]
            });
            
            // 伪装 languages
            Object.defineProperty(navigator, 'languages', {
                get: () => ['ru-RU', 'ru', 'en-US', 'en']
            });
        """)
        
        self.page = self.context.new_page()
        
        if self.debug:
            print('browser started')
    
    def warmup(self, timeout=30000):
        """
        预热：访问首页，等待 JS 执行完成，获取 Cookie
        """
        if self.warmed_up:
            return True
        
        warmup_urls = [
            'https://www.ozon.ru/',
        ]
        
        for url in warmup_urls:
            try:
                if self.debug:
                    print(f'warmup: navigating to {url}')
                
                self.page.goto(url, timeout=timeout, wait_until='networkidle')
                
                # 等待页面完全加载
                time.sleep(random.uniform(2, 4))
                
                # 模拟人类行为：滚动页面
                self.page.evaluate('window.scrollTo(0, document.body.scrollHeight / 3)')
                time.sleep(random.uniform(1, 2))
                
                # 检查是否有 captcha
                content = self.page.content()
                if 'captcha' in content.lower():
                    if self.debug:
                        print('WARNING: captcha detected during warmup')
                    return False
                
                cookies = self.context.cookies()
                if self.debug:
                    print(f'warmup OK: {url} cookies={[c["name"] for c in cookies]}')
                
            except Exception as e:
                if self.debug:
                    print(f'warmup FAILED: {url} error={e}')
                return False
        
        self.warmed_up = True
        return True
    
    def fetch(self, url, timeout=30000):
        """
        抓取页面
        """
        try:
            if self.debug:
                print(f'fetching: {url}')
            
            self.page.goto(url, timeout=timeout, wait_until='domcontentloaded')
            
            # 等待页面内容加载
            time.sleep(random.uniform(1, 2))
            
            # 获取 HTML
            html = self.page.content()
            
            if self.debug:
                print(f'fetched {len(html)} bytes')
            
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
    parser = argparse.ArgumentParser(description='Market scrape worker using real browser (Playwright)')
    parser.add_argument('--base-url', default=os.getenv('MARKET_BASE_URL', 'http://localhost:8080/'))
    parser.add_argument('--worker-id', default=os.getenv('WORKER_ID', f'browser-worker-{os.getpid()}'))
    parser.add_argument('--pull-limit', type=int, default=int(os.getenv('PULL_LIMIT', '5')))
    parser.add_argument('--poll-interval', type=float, default=float(os.getenv('POLL_INTERVAL', '5')))
    parser.add_argument('--heartbeat-interval', type=float, default=float(os.getenv('HEARTBEAT_INTERVAL', '30')))
    parser.add_argument('--timeout', type=int, default=int(os.getenv('REQUEST_TIMEOUT', '30')))
    parser.add_argument('--raw-limit', type=int, default=int(os.getenv('RAW_LIMIT', '500000')))
    # 代理参数
    parser.add_argument('--proxy', default=os.getenv('HTTP_PROXY'), help='代理地址')
    # 请求间隔
    parser.add_argument('--sleep-min', type=float, default=float(os.getenv('SLEEP_MIN', '2.0')), help='请求间隔最小秒数')
    parser.add_argument('--sleep-max', type=float, default=float(os.getenv('SLEEP_MAX', '5.0')), help='请求间隔最大秒数')
    # 浏览器参数
    parser.add_argument('--headless', action='store_true', default=True, help='无头模式')
    parser.add_argument('--no-headless', action='store_true', help='显示浏览器窗口（调试用）')
    parser.add_argument('--debug', action='store_true', help='打印调试信息')
    args = parser.parse_args()

    if not HAS_PLAYWRIGHT:
        print('ERROR: playwright not installed', file=sys.stderr)
        print('Run: pip install playwright && playwright install chromium', file=sys.stderr)
        sys.exit(1)

    base = args.base_url
    last_heartbeat = 0
    headless = not args.no_headless
    
    # 初始化浏览器
    scraper = BrowserScraper(headless=headless, proxy=args.proxy, debug=args.debug)
    
    try:
        print('starting browser...')
        scraper.start()
        
        print('performing warmup...')
        if scraper.warmup(args.timeout * 1000):
            print('warmup successful, starting worker loop')
        else:
            print('warmup failed, continuing anyway...', file=sys.stderr)

        while True:
            now = time.time()
            if now - last_heartbeat >= args.heartbeat_interval:
                heartbeat_payload = {
                    'worker_id': args.worker_id,
                    'status': 'RUNNING',
                    'current_concurrency': 1,
                    'max_concurrency': 1,
                    'last_heartbeat': now_iso(),
                }
                try:
                    http_request('POST', build_url(base, '/market/schedulers/heartbeat'), heartbeat_payload, args.timeout)
                except Exception as e:
                    print(f'heartbeat failed: {e}', file=sys.stderr)
                last_heartbeat = now

            try:
                if args.debug:
                    print(f'pulling tasks from {base}...')
                pull_payload = {'worker_id': args.worker_id, 'limit': args.pull_limit}
                pull_resp = http_request('POST', build_url(base, '/market/tasks/pull'), pull_payload, args.timeout)
                tasks = pull_resp.get('tasks') or []
                if args.debug:
                    print(f'pull response: {len(tasks)} tasks')
            except Exception as e:
                print(f'pull failed: {e}', file=sys.stderr)
                time.sleep(args.poll_interval)
                continue

            if not tasks:
                if args.debug:
                    print(f'no tasks available, waiting {args.poll_interval}s...')
                time.sleep(args.poll_interval)
                continue

            for task in tasks:
                task_id = task.get('id')
                url = task.get('url')
                data_type = task.get('dataType') or task.get('data_type')
                try:
                    html = scraper.fetch(url, args.timeout * 1000)
                    if args.raw_limit > 0:
                        html = html[: args.raw_limit]
                    product_id = extract_product_id(url, html) or fallback_id(url)
                    title = extract_title(html)

                    ingest_payload = {
                        'platform': task.get('platform') or 'ozon',
                        'market': task.get('market') or 'RU',
                        'url': url,
                        'data_type': data_type,
                        'fetched_at': now_iso(),
                        'raw_payload': html,
                        'parsed_payload': json.dumps({'product_id': product_id, 'title': title}),
                        'item': {
                            'platform_product_id': product_id,
                            'title': title,
                            'snapshot_date': dt.date.today().isoformat(),
                            'data_source': data_type or 'detail_page',
                        },
                    }
                    http_request('POST', build_url(base, '/market/scrape/ingest'), ingest_payload, args.timeout)
                    report_payload = {
                        'task_id': task_id,
                        'status': 'SUCCESS',
                        'fetched_at': now_iso(),
                    }
                    http_request('POST', build_url(base, '/market/tasks/report'), report_payload, args.timeout)
                    
                    if args.debug:
                        print(f'task {task_id} SUCCESS: product_id={product_id}, title={title[:50] if title else None}...')
                except Exception as e:
                    err = str(e)
                    report_payload = {
                        'task_id': task_id,
                        'status': 'FAILED',
                        'error_message': err[:255],
                    }
                    try:
                        http_request('POST', build_url(base, '/market/tasks/report'), report_payload, args.timeout)
                    except Exception as report_err:
                        print(f'report failed: {report_err}', file=sys.stderr)
                    print(f'task {task_id} failed: {err}', file=sys.stderr)
                
                # 请求间隔
                sleep_time = random.uniform(args.sleep_min, args.sleep_max)
                if args.debug:
                    print(f'sleeping {sleep_time:.2f}s')
                time.sleep(sleep_time)
                
    except KeyboardInterrupt:
        print('\nshutting down...')
    finally:
        scraper.stop()


if __name__ == '__main__':
    main()
