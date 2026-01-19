import argparse
import datetime as dt
from datetime import UTC
import gzip
import hashlib
import http.cookiejar
import json
import os
import random
import re
import sys
import time
import urllib.error
import urllib.parse
import urllib.request


# 真实浏览器 User-Agent 列表
USER_AGENTS = [
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36',
    'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
    'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:121.0) Gecko/20100101 Firefox/121.0',
    'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Safari/605.1.15',
]

# 预热 URL 列表（模拟真实用户浏览路径）
WARMUP_URLS = [
    'https://www.ozon.ru/',
    'https://www.ozon.ru/category/elektronika-15500/',
    'https://www.ozon.ru/category/smartfony-15502/',
]


class NoRedirectHandler(urllib.request.HTTPRedirectHandler):
    """禁止自动重定向，手动处理"""
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        return None


class BrowserSession:
    """
    模拟浏览器会话，维护 Cookie 和 User-Agent 一致性
    """
    def __init__(self, proxy=None):
        self.cookie_jar = http.cookiejar.CookieJar()
        self.user_agent = random.choice(USER_AGENTS)
        self.proxy = proxy
        self.last_warmup = 0
        self.warmup_interval = 300  # 5分钟重新预热一次
        self._build_opener()
    
    def _build_opener(self):
        handlers = [
            NoRedirectHandler(),
            urllib.request.HTTPCookieProcessor(self.cookie_jar),
        ]
        if self.proxy:
            handlers.append(urllib.request.ProxyHandler({'http': self.proxy, 'https': self.proxy}))
        self.opener = urllib.request.build_opener(*handlers)
    
    def set_proxy(self, proxy):
        """更换代理"""
        self.proxy = proxy
        self._build_opener()
    
    def get_headers(self, referer=None):
        """获取请求头"""
        return {
            'User-Agent': self.user_agent,
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8',
            'Accept-Language': 'ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7',
            'Accept-Encoding': 'gzip, deflate',
            'Cache-Control': 'no-cache',
            'Pragma': 'no-cache',
            'Sec-Ch-Ua': '"Not_A Brand";v="8", "Chromium";v="120", "Google Chrome";v="120"',
            'Sec-Ch-Ua-Mobile': '?0',
            'Sec-Ch-Ua-Platform': '"Windows"',
            'Sec-Fetch-Dest': 'document',
            'Sec-Fetch-Mode': 'navigate',
            'Sec-Fetch-Site': 'same-origin' if referer else 'none',
            'Sec-Fetch-User': '?1',
            'Upgrade-Insecure-Requests': '1',
            'Referer': referer or 'https://www.ozon.ru/',
        }
    
    def warmup(self, timeout=20, debug=False):
        """
        预热会话：访问首页和分类页，获取 Cookie
        模拟真实用户浏览行为
        """
        now = time.time()
        if now - self.last_warmup < self.warmup_interval:
            if debug:
                print(f'warmup skipped, last warmup {int(now - self.last_warmup)}s ago')
            return True
        
        if debug:
            print('starting warmup session...')
        
        success = True
        referer = None
        
        for url in WARMUP_URLS:
            try:
                headers = self.get_headers(referer)
                req = urllib.request.Request(url, headers=headers)
                
                with self.opener.open(req, timeout=timeout) as resp:
                    content = resp.read()
                    # 解压
                    encoding = resp.headers.get('Content-Encoding', '')
                    if 'gzip' in encoding:
                        content = gzip.decompress(content)
                    html = content.decode('utf-8', errors='ignore')
                    
                    # 检查是否被拦截
                    if 'captcha' in html.lower() or 'access denied' in html.lower():
                        if debug:
                            print(f'warmup WARNING: possible captcha at {url}')
                        success = False
                    else:
                        if debug:
                            cookies = [c.name for c in self.cookie_jar]
                            print(f'warmup OK: {url} bytes={len(html)} cookies={cookies}')
                
                referer = url  # 下一个请求的 referer
                time.sleep(random.uniform(1.0, 2.5))  # 模拟阅读时间
                
            except Exception as e:
                if debug:
                    print(f'warmup FAILED: {url} error={e}')
                success = False
        
        if success:
            self.last_warmup = now
            if debug:
                print(f'warmup completed, got {len(list(self.cookie_jar))} cookies')
        
        return success
    
    def fetch(self, url, timeout=20, max_redirects=5, debug=False):
        """
        抓取页面，使用已有的 Cookie
        """
        # 检查是否需要预热
        if time.time() - self.last_warmup > self.warmup_interval:
            self.warmup(timeout, debug)
        
        headers = self.get_headers(referer='https://www.ozon.ru/')
        current = url
        visited = set()
        
        for _ in range(max_redirects + 1):
            if current in visited:
                raise RuntimeError(f'redirect loop: {current}')
            visited.add(current)
            
            req = urllib.request.Request(current, headers=headers)
            try:
                with self.opener.open(req, timeout=timeout) as resp:
                    content = resp.read()
                    encoding = resp.headers.get('Content-Encoding', '')
                    if 'gzip' in encoding:
                        content = gzip.decompress(content)
                    return content.decode('utf-8', errors='ignore')
            except urllib.error.HTTPError as e:
                if e.code in (301, 302, 303, 307, 308):
                    location = e.headers.get('Location')
                    if not location:
                        raise
                    current = urllib.parse.urljoin(current, location)
                    continue
                raise
        
        raise RuntimeError(f'too many redirects: {url}')


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


def load_proxies(path):
    """从文件加载代理列表"""
    if not path or not os.path.exists(path):
        return []
    with open(path, 'r', encoding='utf-8') as f:
        lines = [line.strip() for line in f if line.strip()]
    return [line for line in lines if not line.startswith('#')]


def main():
    parser = argparse.ArgumentParser(description='Market scrape worker for fetching product pages')
    parser.add_argument('--base-url', default=os.getenv('MARKET_BASE_URL', 'http://localhost:8080/'))
    parser.add_argument('--worker-id', default=os.getenv('WORKER_ID', f'worker-{os.getpid()}'))
    parser.add_argument('--pull-limit', type=int, default=int(os.getenv('PULL_LIMIT', '10')))
    parser.add_argument('--poll-interval', type=float, default=float(os.getenv('POLL_INTERVAL', '5')))
    parser.add_argument('--heartbeat-interval', type=float, default=float(os.getenv('HEARTBEAT_INTERVAL', '30')))
    parser.add_argument('--timeout', type=int, default=int(os.getenv('REQUEST_TIMEOUT', '20')))
    parser.add_argument('--raw-limit', type=int, default=int(os.getenv('RAW_LIMIT', '200000')))
    # 代理参数
    parser.add_argument('--proxy', default=os.getenv('HTTP_PROXY'), help='单个代理地址')
    parser.add_argument('--proxy-file', default=os.getenv('PROXY_FILE'), help='代理列表文件')
    parser.add_argument('--max-redirects', type=int, default=int(os.getenv('MAX_REDIRECTS', '5')))
    # 请求间隔
    parser.add_argument('--sleep-min', type=float, default=float(os.getenv('SLEEP_MIN', '1.0')), help='请求间隔最小秒数')
    parser.add_argument('--sleep-max', type=float, default=float(os.getenv('SLEEP_MAX', '3.0')), help='请求间隔最大秒数')
    # 预热参数
    parser.add_argument('--warmup-interval', type=int, default=int(os.getenv('WARMUP_INTERVAL', '300')), help='预热间隔秒数')
    parser.add_argument('--no-warmup', action='store_true', help='禁用预热')
    parser.add_argument('--debug', action='store_true', help='打印调试信息')
    args = parser.parse_args()

    base = args.base_url
    last_heartbeat = 0
    
    # 加载代理列表
    proxies = load_proxies(args.proxy_file)
    if args.debug:
        print(f'loaded {len(proxies)} proxies from file' if proxies else 'no proxy file, using single proxy or direct')
    
    # 初始化浏览器会话
    initial_proxy = random.choice(proxies) if proxies else args.proxy
    session = BrowserSession(proxy=initial_proxy)
    session.warmup_interval = args.warmup_interval
    
    # 启动时执行预热
    if not args.no_warmup:
        print('performing initial warmup...')
        if session.warmup(args.timeout, args.debug):
            print('warmup successful, starting worker loop')
        else:
            print('warmup failed, will retry during fetch', file=sys.stderr)

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
            pull_payload = {'worker_id': args.worker_id, 'limit': args.pull_limit}
            pull_resp = http_request('POST', build_url(base, '/market/tasks/pull'), pull_payload, args.timeout)
            tasks = pull_resp.get('tasks') or []
        except Exception as e:
            print(f'pull failed: {e}', file=sys.stderr)
            time.sleep(args.poll_interval)
            continue

        if not tasks:
            time.sleep(args.poll_interval)
            continue

        for task in tasks:
            task_id = task.get('id')
            url = task.get('url')
            data_type = task.get('dataType') or task.get('data_type')
            try:
                # 如果有代理池，随机切换代理
                if proxies:
                    new_proxy = random.choice(proxies)
                    session.set_proxy(new_proxy)
                    if args.debug:
                        print(f'switched to proxy: {new_proxy}')
                
                if args.debug:
                    print(f'fetching {url}')
                
                html = session.fetch(url, args.timeout, args.max_redirects, args.debug)
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
                    print(f'task {task_id} SUCCESS: product_id={product_id}, title={title[:50] if title else None}..., bytes={len(html)}')
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
            
            # 请求间隔，避免被封禁
            sleep_time = random.uniform(args.sleep_min, args.sleep_max)
            if args.debug:
                print(f'sleeping {sleep_time:.2f}s before next request')
            time.sleep(sleep_time)


if __name__ == '__main__':
    main()