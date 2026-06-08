import requests

# 先尝试登录获取token
login_data = {
    'username': 'admin',
    'password': 'admin123'
}

try:
    # 登录
    login_response = requests.post('http://localhost:8081/api/auth/login', json=login_data)
    print(f"登录状态码: {login_response.status_code}")
    
    if login_response.status_code == 200:
        data = login_response.json()
        token = data.get('data', {}).get('accessToken') or data.get('data', {}).get('token')
        print(f"获取到Token: {token[:20]}...")
        
        if token:
            # 使用token获取菜单
            headers = {'Authorization': f'Bearer {token}'}
            menu_response = requests.get('http://localhost:8081/api/system/menu/tree', headers=headers)
            print(f"\n菜单API状态码: {menu_response.status_code}")
            print(f"菜单响应: {menu_response.text[:2000]}")
        
except Exception as e:
    print(f"错误: {e}")