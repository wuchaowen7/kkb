import requests
import json

def test_simple_queries():
    print("=== Testing Simple Queries ===\n")
    
    # 先获取token
    login_data = {'username': 'admin', 'password': 'admin123'}
    try:
        response = requests.post('http://localhost:8081/api/auth/login', json=login_data)
        print("Login response:", response.status_code)
        if response.status_code == 200:
            token = response.json().get('data', {}).get('accessToken') or response.json().get('data', {}).get('token')
            headers = {'Authorization': 'Bearer %s' % token}
            print("Token obtained successfully\n")
            
            # 测试不同类型的查询
            test_urls = [
                # server-system 模块
                ('GET', 'http://localhost:8081/api/system/role/list', {}, 'Role list'),
                ('GET', 'http://localhost:8081/api/system/menu/tree', {}, 'Menu tree'),
                ('GET', 'http://localhost:8081/api/system/user/list', {}, 'User list (non-paginated)'),
                
                # server-base 模块
                ('GET', 'http://localhost:8082/api/base/categories/tree', {}, 'Category tree'),
                ('GET', 'http://localhost:8082/api/base/warehouses/list', {}, 'Warehouse list'),
                ('GET', 'http://localhost:8082/api/base/suppliers/list', {}, 'Supplier list'),
                ('GET', 'http://localhost:8082/api/base/products/list', {}, 'Product list (non-paginated)'),
            ]
            
            for method, url, params, desc in test_urls:
                print(f"\nTesting: {desc}")
                print(f"URL: {url}")
                try:
                    if method == 'GET':
                        response = requests.get(url, headers=headers, params=params)
                    print(f"Status: {response.status_code}")
                    if response.status_code == 200:
                        data = response.json()
                        print(f"Response code: {data.get('code')}")
                        if 'data' in data:
                            if isinstance(data['data'], list):
                                print(f"Data count: {len(data['data'])}")
                            else:
                                print(f"Data type: {type(data['data'])}")
                    else:
                        print(f"Response: {response.text[:300]}")
                except Exception as e:
                    print(f"Error: {e}")
                    
    except Exception as e:
        print(f"Login failed: {e}")

if __name__ == '__main__':
    test_simple_queries()