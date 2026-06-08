import requests
import json

def test_pagination_with_debug():
    print("=== Debug Pagination Issues ===\n")
    
    # 先获取token
    login_data = {'username': 'admin', 'password': 'admin123'}
    try:
        response = requests.post('http://localhost:8081/api/auth/login', json=login_data)
        print("Login response:", response.status_code)
        if response.status_code == 200:
            token = response.json().get('data', {}).get('accessToken') or response.json().get('data', {}).get('token')
            headers = {'Authorization': 'Bearer %s' % token}
            print("Token obtained successfully\n")
            
            # 测试不同参数的分页查询
            test_cases = [
                ('http://localhost:8081/api/system/user/page', {'pageNum': 1, 'pageSize': 10}),
                ('http://localhost:8081/api/system/user/page', {'page': 1, 'size': 10}),
                ('http://localhost:8081/api/system/user/page', {'pageNum': '1', 'pageSize': '10'}),
                ('http://localhost:8081/api/base/products/page', {'pageNum': 1, 'pageSize': 10}),
            ]
            
            for url, params in test_cases:
                print(f"\nTesting: {url} with params: {params}")
                try:
                    response = requests.get(url, headers=headers, params=params)
                    print(f"Status: {response.status_code}")
                    print(f"Response: {response.text[:500]}")
                except Exception as e:
                    print(f"Error: {e}")
            
            # 测试非分页查询
            print("\n=== Testing Non-Pagination Queries ===")
            non_paginated_urls = [
                'http://localhost:8081/api/system/role/list',
                'http://localhost:8081/api/base/category/tree',
            ]
            
            for url in non_paginated_urls:
                print(f"\nTesting: {url}")
                try:
                    response = requests.get(url, headers=headers)
                    print(f"Status: {response.status_code}")
                    if response.status_code == 200:
                        data = response.json().get('data')
                        print(f"Data count: {len(data) if isinstance(data, list) else 'Object'}")
                except Exception as e:
                    print(f"Error: {e}")
                    
    except Exception as e:
        print(f"Login failed: {e}")

if __name__ == '__main__':
    test_pagination_with_debug()