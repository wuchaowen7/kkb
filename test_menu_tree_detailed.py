import requests
import json

login_data = {
    'username': 'admin',
    'password': 'admin123'
}

try:
    login_response = requests.post('http://localhost:8081/api/auth/login', json=login_data)
    print(f"登录状态码: {login_response.status_code}")
    
    if login_response.status_code == 200:
        data = login_response.json()
        token = data.get('data', {}).get('accessToken') or data.get('data', {}).get('token')
        print(f"获取到Token: {token[:20]}...")
        
        if token:
            headers = {'Authorization': f'Bearer {token}'}
            menu_response = requests.get('http://localhost:8081/api/system/menu/tree', headers=headers)
            print(f"\n菜单API状态码: {menu_response.status_code}")
            
            # 格式化输出
            menu_data = menu_response.json()
            print("\n菜单响应(JSON格式化):")
            print(json.dumps(menu_data, indent=2, ensure_ascii=False))
            
            # 检查是否有children字段
            print("\n检查每个菜单项是否有children字段:")
            for item in menu_data.get('data', []):
                has_children = 'children' in item
                children_count = len(item.get('children', [])) if has_children else 0
                print(f"菜单: {item.get('menuName')} - 有children字段: {has_children}, 子菜单数量: {children_count}")
        
except Exception as e:
    print(f"错误: {e}")