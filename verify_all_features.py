import requests
import json

BASE_URL = "http://localhost:8080/api"

def test_auth():
    print("=" * 60)
    print("[SYSTEM] Auth Module Test")
    print("=" * 60)
    
    login_data = {'username': 'admin', 'password': 'admin123'}
    try:
        response = requests.post('%s/auth/login' % BASE_URL, json=login_data)
        if response.status_code == 200:
            data = response.json()
            token = data.get('data', {}).get('accessToken') or data.get('data', {}).get('token')
            print("[OK] Login success - Token: %s..." % (token[:20] if token else ""))
            return token
        else:
            print("[FAIL] Login failed - Status: %d, Response: %s" % (response.status_code, response.text[:200]))
            return None
    except Exception as e:
        print("[FAIL] Login exception: %s" % str(e))
        return None

def test_system_api(token):
    print("\n" + "=" * 60)
    print("[SYSTEM] System Management Module Test")
    print("=" * 60)
    
    headers = {'Authorization': 'Bearer %s' % token}
    tests = [
        {"name": "Get Menu Tree", "url": "/system/menu/tree"},
        {"name": "Get User List", "url": "/system/user/page", "params": {"pageNum": 1, "pageSize": 10}},
        {"name": "Get Role List", "url": "/system/role/list"},
    ]
    
    for test in tests:
        try:
            response = requests.get('%s%s' % (BASE_URL, test["url"]), headers=headers, params=test.get("params"))
            if response.status_code == 200:
                data = response.json()
                result = data.get('data', [])
                count = len(result) if isinstance(result, list) else "OK"
                print("[OK] %s - Count: %s" % (test['name'], count))
            else:
                print("[FAIL] %s - Status: %d" % (test['name'], response.status_code))
        except Exception as e:
            print("[FAIL] %s - Exception: %s" % (test['name'], str(e)))

def test_base_api(token):
    print("\n" + "=" * 60)
    print("[SYSTEM] Base Info Module Test")
    print("=" * 60)
    
    headers = {'Authorization': 'Bearer %s' % token}
    tests = [
        {"name": "Get Product List", "url": "/base/products/page", "params": {"pageNum": 1, "pageSize": 10}},
        {"name": "Get Category Tree", "url": "/base/categories/tree"},
        {"name": "Get Warehouse List", "url": "/base/warehouses/list"},
        {"name": "Get Supplier List", "url": "/base/suppliers/list"},
    ]
    
    for test in tests:
        try:
            response = requests.get('%s%s' % (BASE_URL, test["url"]), headers=headers, params=test.get("params"))
            if response.status_code == 200:
                data = response.json()
                result = data.get('data', [])
                count = len(result) if isinstance(result, list) else (result.get('records') and len(result.get('records'))) or "OK"
                print("[OK] %s - Count: %s" % (test['name'], count))
            else:
                print("[FAIL] %s - Status: %d, Response: %s" % (test['name'], response.status_code, response.text[:100]))
        except Exception as e:
            print("[FAIL] %s - Exception: %s" % (test['name'], str(e)))

def test_stock_api(token):
    print("\n" + "=" * 60)
    print("[SYSTEM] Stock Management Module Test")
    print("=" * 60)
    
    headers = {'Authorization': 'Bearer %s' % token}
    tests = [
        {"name": "Get Inventory List", "url": "/stock/inventory/page", "params": {"pageNum": 1, "pageSize": 10}},
        {"name": "Get Inbound List", "url": "/stock/inbound/page", "params": {"pageNum": 1, "pageSize": 10}},
        {"name": "Get Outbound List", "url": "/stock/outbound/page", "params": {"pageNum": 1, "pageSize": 10}},
        {"name": "Get Transfer List", "url": "/stock/transfer/page", "params": {"pageNum": 1, "pageSize": 10}},
        {"name": "Get Check List", "url": "/stock/check/page", "params": {"pageNum": 1, "pageSize": 10}},
    ]
    
    for test in tests:
        try:
            response = requests.get('%s%s' % (BASE_URL, test["url"]), headers=headers, params=test.get("params"))
            if response.status_code == 200:
                data = response.json()
                result = data.get('data', [])
                count = len(result) if isinstance(result, list) else (result.get('records') and len(result.get('records'))) or "OK"
                print("[OK] %s - Count: %s" % (test['name'], count))
            else:
                print("[FAIL] %s - Status: %d, Response: %s" % (test['name'], response.status_code, response.text[:100]))
        except Exception as e:
            print("[FAIL] %s - Exception: %s" % (test['name'], str(e)))

def test_intelligence_api(token):
    print("\n" + "=" * 60)
    print("[SYSTEM] Intelligence Module Test")
    print("=" * 60)
    
    headers = {'Authorization': 'Bearer %s' % token}
    tests = [
        {"name": "Get Alert List", "url": "/intelligence/alerts"},
        {"name": "Get Alert Count", "url": "/intelligence/alerts/count"},
        {"name": "Get Top Selling", "url": "/intelligence/analysis/top-selling"},
        {"name": "Get Turnover", "url": "/intelligence/analysis/turnover"},
    ]
    
    for test in tests:
        try:
            response = requests.get('%s%s' % (BASE_URL, test["url"]), headers=headers)
            if response.status_code == 200:
                data = response.json()
                result = data.get('data', [])
                count = len(result) if isinstance(result, list) else "OK"
                print("[OK] %s - Count: %s" % (test['name'], count))
            else:
                print("[FAIL] %s - Status: %d, Response: %s" % (test['name'], response.status_code, response.text[:100]))
        except Exception as e:
            print("[FAIL] %s - Exception: %s" % (test['name'], str(e)))

def test_report_api(token):
    print("\n" + "=" * 60)
    print("[SYSTEM] Report Module Test")
    print("=" * 60)
    
    headers = {'Authorization': 'Bearer %s' % token}
    tests = [
        {"name": "Get Dashboard", "url": "/report/dashboard"},
        {"name": "Get In-Out-Stock", "url": "/report/in-out-stock", "params": {"startDate": "2024-01-01", "endDate": "2024-12-31"}},
        {"name": "Get Inventory Detail", "url": "/report/inventory-detail", "params": {"pageNum": 1, "pageSize": 10}},
    ]
    
    for test in tests:
        try:
            response = requests.get('%s%s' % (BASE_URL, test["url"]), headers=headers, params=test.get("params"))
            if response.status_code == 200:
                data = response.json()
                result = data.get('data', {})
                keys = len(result.keys()) if isinstance(result, dict) else len(result) if isinstance(result, list) else "OK"
                print("[OK] %s - Fields: %s" % (test['name'], keys))
            else:
                print("[FAIL] %s - Status: %d, Response: %s" % (test['name'], response.status_code, response.text[:100]))
        except Exception as e:
            print("[FAIL] %s - Exception: %s" % (test['name'], str(e)))

def main():
    print("Smart Inventory Management System - Feature Verification")
    print("=" * 60)
    
    token = test_auth()
    
    if not token:
        print("\n[FAIL] Authentication failed, cannot continue testing")
        return
    
    test_system_api(token)
    test_base_api(token)
    test_stock_api(token)
    test_intelligence_api(token)
    test_report_api(token)
    
    print("\n" + "=" * 60)
    print("Feature verification completed!")
    print("=" * 60)

if __name__ == '__main__':
    main()