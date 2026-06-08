import requests
import json

BASE_URL = "http://localhost:8080/api"

def get_token():
    login_data = {'username': 'admin', 'password': 'admin123'}
    response = requests.post('%s/auth/login' % BASE_URL, json=login_data)
    if response.status_code == 200:
        data = response.json()
        return data.get('data', {}).get('accessToken') or data.get('data', {}).get('token')
    return None

def test_page_api(token):
    headers = {'Authorization': 'Bearer %s' % token}
    
    # 测试分页API
    print("=== Testing Page APIs ===")
    
    page_apis = [
        {"name": "User Page", "url": "/system/user/page", "params": {"pageNum": 1, "pageSize": 10}},
        {"name": "Product Page", "url": "/base/products/page", "params": {"pageNum": 1, "pageSize": 10}},
        {"name": "Inventory Page", "url": "/stock/inventory/page", "params": {"pageNum": 1, "pageSize": 10}},
        {"name": "Inbound Page", "url": "/stock/inbound/page", "params": {"pageNum": 1, "pageSize": 10}},
        {"name": "Outbound Page", "url": "/stock/outbound/page", "params": {"pageNum": 1, "pageSize": 10}},
    ]
    
    for api in page_apis:
        try:
            print("\n--- Testing %s ---" % api["name"])
            print("URL: %s" % api["url"])
            print("Params: %s" % api["params"])
            
            response = requests.get('%s%s' % (BASE_URL, api["url"]), headers=headers, params=api["params"])
            
            print("Status Code: %d" % response.status_code)
            print("Headers: %s" % response.headers)
            
            try:
                content = response.json()
                print("Response (JSON): %s" % json.dumps(content, ensure_ascii=False, indent=2))
            except:
                print("Response (Text): %s" % response.text[:500])
                
        except Exception as e:
            print("Exception: %s" % str(e))

def main():
    token = get_token()
    if not token:
        print("Failed to get token")
        return
    
    print("Token obtained successfully")
    test_page_api(token)

if __name__ == '__main__':
    main()