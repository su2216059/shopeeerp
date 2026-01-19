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


def http_request(method, url, payload=None, timeout=20):
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


class NoRedirectHandler(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        return None


USER_AGENTS = [
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
    'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
    'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
]


TRACKING_PARAMS = {
    '_fr',
    '__rr',
    'abt_att',
    'utm_source',
    'utm_medium',
    'utm_campaign',
    'utm_content',
    'utm_term',
    'gclid',
    'yclid',
    'ysclid',
}


def sanitize_seed_url(url):
    parsed = urllib.parse.urlsplit(url)
    if not parsed.query:
        return url
    query = urllib.parse.parse_qsl(parsed.query, keep_blank_values=True)
    filtered = [(k, v) for k, v in query if k not in TRACKING_PARAMS]
    new_query = urllib.parse.urlencode(filtered)
    return urllib.parse.urlunsplit((parsed.scheme, parsed.netloc, parsed.path, new_query, parsed.fragment))


def fetch_url(url, timeout=20, max_redirects=5, proxy=None):
    handlers = [NoRedirectHandler(), urllib.request.HTTPCookieProcessor()]
    if proxy:
        handlers.append(urllib.request.ProxyHandler({'http': proxy, 'https': proxy}))
    opener = urllib.request.build_opener(*handlers)
    headers = {
        'User-Agent': random.choice(USER_AGENTS),
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
        'Accept-Language': 'ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7',
        'Cache-Control': 'no-cache',
        'Referer': 'https://www.ozon.ru/',
    }
    current = url
    visited = set()
    for _ in range(max_redirects + 1):
        if current in visited:
            raise RuntimeError(f'redirect loop: {current}')
        visited.add(current)
        req = urllib.request.Request(current, headers=headers)
        try:
            with opener.open(req, timeout=timeout) as resp:
                return resp.read().decode('utf-8', errors='ignore')
        except urllib.error.HTTPError as e:
            if e.code in (301, 302, 303, 307, 308):
                location = e.headers.get('Location')
                if not location:
                    raise
                current = urllib.parse.urljoin(current, location)
                continue
            raise
    raise RuntimeError(f'too many redirects: {url}')


def normalize_product_url(url):
    if url.startswith('//'):
        return 'https:' + url
    if url.startswith('/'):
        return 'https://www.ozon.ru' + url
    return url


def extract_product_urls(html):
    urls = set()
    patterns = [
        r'href="(https?://[^"\s]+/product/[^"\s]+)"',
        r'href="(/product/[^"\s]+)"',
        r'"link"\s*:\s*"(https?://[^"\s]+/product/[^"\s]+)"',
    ]
    for pattern in patterns:
        for match in re.findall(pattern, html):
            url = normalize_product_url(match)
            if '/product/' in url:
                urls.add(url.split('?')[0])
    return urls


def load_seeds(path):
    with open(path, 'r', encoding='utf-8') as f:
        lines = [line.strip() for line in f if line.strip()]
    return [line for line in lines if not line.startswith('#')]


def load_cache(path):
    if not os.path.exists(path):
        return set()
    try:
        with open(path, 'r', encoding='utf-8') as f:
            data = json.load(f)
        return set(data if isinstance(data, list) else [])
    except Exception:
        return set()


def load_proxies(path):
    if not path or not os.path.exists(path):
        return []
    with open(path, 'r', encoding='utf-8') as f:
        lines = [line.strip() for line in f if line.strip()]
    return [line for line in lines if not line.startswith('#')]


def save_cache(path, urls):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(sorted(urls), f)


def enqueue_tasks(base_url, urls, data_type, platform, market, timeout, batch_size=100):
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


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--base-url', default=os.getenv('MARKET_BASE_URL', 'http://localhost:8080'))
    parser.add_argument('--seed-file', default=os.getenv('SEED_FILE', 'scripts/ozon_seed_urls.txt'))
    parser.add_argument('--cache-file', default=os.getenv('CACHE_FILE', 'scripts/cache/ozon_product_urls.json'))
    parser.add_argument('--interval', type=int, default=int(os.getenv('POLL_INTERVAL', '900')))
    parser.add_argument('--timeout', type=int, default=int(os.getenv('REQUEST_TIMEOUT', '20')))
    parser.add_argument('--max-redirects', type=int, default=int(os.getenv('MAX_REDIRECTS', '5')))
    parser.add_argument('--data-type', default=os.getenv('DATA_TYPE', 'category_page'))
    parser.add_argument('--platform', default=os.getenv('PLATFORM', 'ozon'))
    parser.add_argument('--market', default=os.getenv('MARKET', 'RU'))
    parser.add_argument('--proxy', default=os.getenv('HTTP_PROXY'))
    parser.add_argument('--proxy-file', default=os.getenv('PROXY_FILE'))
    parser.add_argument('--sleep-min', type=float, default=float(os.getenv('SLEEP_MIN', '0.5')))
    parser.add_argument('--sleep-max', type=float, default=float(os.getenv('SLEEP_MAX', '1.5')))
    parser.add_argument('--debug', action='store_true', help='print fetch and parse details')
    args = parser.parse_args()

    if not os.path.exists(args.seed_file):
        print(f'seed file not found: {args.seed_file}', file=sys.stderr)
        sys.exit(1)

    while True:
        seeds = load_seeds(args.seed_file)
        cache = load_cache(args.cache_file)
        proxies = load_proxies(args.proxy_file)
        discovered = set()

        for seed_url in seeds:
            sanitized = sanitize_seed_url(seed_url)
            if args.debug and sanitized != seed_url:
                print(f'sanitized: {seed_url} -> {sanitized}')
            try:
                proxy = random.choice(proxies) if proxies else args.proxy
                html = fetch_url(sanitized, args.timeout, args.max_redirects, proxy)
                urls = extract_product_urls(html)
                discovered.update(urls)
                if args.debug:
                    print(f'fetch ok: {seed_url} bytes={len(html)} urls={len(urls)}')
                    if 'captcha' in html.lower() or 'access denied' in html.lower():
                        print('warning: possible block or captcha page')
            except Exception as e:
                print(f'fetch failed: {seed_url} {e}', file=sys.stderr)
            time.sleep(random.uniform(args.sleep_min, args.sleep_max))

        new_urls = discovered - cache
        if new_urls:
            count = enqueue_tasks(args.base_url, new_urls, args.data_type, args.platform, args.market, args.timeout)
            print(f'enqueued {count} new urls')
            cache.update(new_urls)
            save_cache(args.cache_file, cache)
        else:
            print('no new urls')

        time.sleep(max(args.interval, 1))


if __name__ == '__main__':
    main()
