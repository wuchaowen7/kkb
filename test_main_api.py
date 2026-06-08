import requests
import json

def test_login():
    print("测试登录API...")
    login_data = {'username': 'admin', 'password': 'admin123'}
    response = requests.post('http://localhost:8080/api/auth/login', json=login_data)
    print(f"登录状态码: {response.status_code}")
    if response.status_code == 200:
        data = response.json()
        token = data.get('data', {}).get('accessToken') or data.get('data', {}).get('token')
        print(f"获取到Token: {token[:20]}...")
        return token
    return None

def test_menu_api(token):
    print("\n测试菜单API...")
    headers = {'Authorization': f'Bearer {token}'}
    response = requests.get('http://localhost:8080/api/system/menu/tree', headers=headers)
    print(f"菜单API状态码: {response.status_code}")
    if response.status_code == 200:
        data = response.json()
        menus = data.get('data', [])
        print(f"获取到 {len(menus)} 个顶级菜单")
        for menu in menus[:3]:  # 只显示前3个
            children_count = len(menu.get('children', []))
            print(f"  - {menu.get('menuName')}: {children_count} 个子菜单")

def test_alerts_api(token):
    print("\n测试预警API...")
    headers = {'Authorization': f'Bearer {token}'}
    response = requests.get('http://localhost:8080/api/intelligence/alerts', headers=headers)
    print(f"预警API状态码: {response.status_code}")
    if response.status_code == 200:
        data = response.json()
        alerts = data.get('data', [])
        print(f"获取到 {len(alerts)} 条预警记录")

if __name__ == '__main__':
    print("=" * 50)
    print("测试智能库存管理系统API")
    print("=" * 50)
    
    token = test_login()
    if token:
        test_menu_api(token)
        test_alerts_api(token)
    
    print("\n" + "=" * 50)