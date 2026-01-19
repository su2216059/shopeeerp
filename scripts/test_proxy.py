"""
测试俄罗斯代理是否能访问 Ozon

用法：
    python scripts/test_proxy.py
    python scripts/test_proxy.py --proxy http://ip:port
    python scripts/test_proxy.py --proxy-file proxies.txt
"""
import argparse
import sys
import urllib.request
import urllib.error


# 一些免费俄罗斯代理（可能已失效，仅供测试）
# 格式: ip:port
FREE_PROXIES = [
    # 从免费代理网站获取后填入这里
    # "185.15.172.212:3128",
    # "46.8.221.158:8080",
]


def test_proxy(proxy, timeout=15):
    """测试代理是否能访问 Ozon"""
    proxy_handler = urllib.request.ProxyHandler({
        'http': f'http://{proxy}',
        'https': f'http://{proxy}',
    })
    opener = urllib.request.build_opener(proxy_handler)
    
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
        'Accept': 'text/html',
    }
    
    test_url = 'https://www.ozon.ru/'
    req = urllib.request.Request(test_url, headers=headers)
    
    try:
        with opener.open(req, timeout=timeout) as resp:
            content = resp.read().decode('utf-8', errors='ignore')
            
            # 检查是否被阻止
            if 'Доступ ограничен' in content or 'access denied' in content.lower():
                return False, 'blocked'
            if 'captcha' in content.lower():
                return False, 'captcha'
            if '<title>' in content.lower() and 'ozon' in content.lower():
                return True, 'success'
            return False, 'unknown response'
    except urllib.error.HTTPError as e:
        return False, f'HTTP {e.code}'
    except urllib.error.URLError as e:
        return False, f'URL Error: {e.reason}'
    except Exception as e:
        return False, str(e)


def load_proxies_from_file(path):
    """从文件加载代理列表"""
    with open(path, 'r') as f:
        lines = [line.strip() for line in f if line.strip()]
    return [line for line in lines if not line.startswith('#')]


def main():
    parser = argparse.ArgumentParser(description='Test Russian proxies for Ozon access')
    parser.add_argument('--proxy', help='单个代理地址 (ip:port)')
    parser.add_argument('--proxy-file', help='代理列表文件')
    parser.add_argument('--timeout', type=int, default=15, help='超时时间（秒）')
    args = parser.parse_args()
    
    proxies = []
    
    if args.proxy:
        proxies = [args.proxy]
    elif args.proxy_file:
        proxies = load_proxies_from_file(args.proxy_file)
    else:
        proxies = FREE_PROXIES
    
    if not proxies:
        print('没有代理可测试！')
        print('请使用 --proxy 或 --proxy-file 参数，或在脚本中填入 FREE_PROXIES')
        print()
        print('获取免费俄罗斯代理：')
        print('  1. https://free-proxy-list.net/ (筛选 Russia)')
        print('  2. https://hidemy.name/en/proxy-list/?country=RU')
        print()
        print('然后运行: python scripts/test_proxy.py --proxy ip:port')
        sys.exit(1)
    
    print(f'测试 {len(proxies)} 个代理...\n')
    
    working = []
    for proxy in proxies:
        print(f'测试: {proxy}...', end=' ', flush=True)
        success, msg = test_proxy(proxy, args.timeout)
        if success:
            print(f'✓ 成功!')
            working.append(proxy)
        else:
            print(f'✗ 失败 ({msg})')
    
    print()
    if working:
        print(f'=== 可用代理 ({len(working)}) ===')
        for p in working:
            print(f'  {p}')
        print()
        print('使用方法：')
        print(f'  python scripts/ozon_url_discovery_browser.py --proxy http://{working[0]} --debug --once')
    else:
        print('没有找到可用的代理 :(')
        print('建议购买付费俄罗斯代理服务')


if __name__ == '__main__':
    main()
