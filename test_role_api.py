import requests

# 使用正确密码登录
r = requests.post('http://localhost:8081/api/auth/login', json={'username':'admin','password':'123'})
print('登录响应:', r.status_code, r.text)

if r.status_code == 200:
    data = r.json()
    if data['code'] == 200:
        token = data['data']['token']
        h = {'Authorization': 'Bearer ' + token}
        
        print('\n=== 测试角色管理接口 ===')
        
        # 1. 获取角色列表
        print('\n1. 获取角色列表:')
        result = requests.get('http://localhost:8081/api/system/role/list', headers=h)
        print(f'状态码：{result.status_code}')
        if result.status_code == 200:
            roles = result.json()
            print(f'响应：{roles}')
            if roles.get('code') == 200 and roles.get('data'):
                print(f'角色数量：{len(roles["data"])}')
                for role in roles['data']:
                    print(f"  - {role['roleName']} ({role['roleCode']})")
        
        # 2. 获取单个角色详情
        print('\n2. 获取角色详情 (ID=1):')
        result = requests.get('http://localhost:8081/api/system/role/1', headers=h)
        print(f'状态码：{result.status_code}')
        if result.status_code == 200:
            print(f'响应：{result.json()}')
        
        # 3. 获取角色的菜单权限
        print('\n3. 获取角色菜单权限 (ID=1):')
        result = requests.get('http://localhost:8081/api/system/role/1/menus', headers=h)
        print(f'状态码：{result.status_code}')
        if result.status_code == 200:
            menus = result.json()
            print(f'响应：{menus}')
            if menus.get('code') == 200 and menus.get('data'):
                print(f'菜单数量：{len(menus["data"])}')
        
        # 4. 新增角色
        print('\n4. 新增测试角色:')
        new_role = {
            'roleName': '测试角色',
            'roleCode': 'TEST_ROLE',
            'description': '这是一个测试角色',
            'status': 1
        }
        result = requests.post('http://localhost:8081/api/system/role', json=new_role, headers=h)
        print(f'状态码：{result.status_code}')
        if result.status_code == 200:
            print(f'响应：{result.json()}')
            print('✓ 新增成功')
        
        # 5. 编辑角色
        print('\n5. 编辑测试角色:')
        update_role = {
            'roleName': '测试角色 - 已修改',
            'roleCode': 'TEST_ROLE',
            'description': '描述已修改',
            'status': 1
        }
        result = requests.put('http://localhost:8081/api/system/role/6', json=update_role, headers=h)
        print(f'状态码：{result.status_code}')
        if result.status_code == 200:
            print(f'响应：{result.json()}')
            print('✓ 修改成功')
        
        # 6. 分配菜单权限
        print('\n6. 分配菜单权限 (ID=6):')
        menu_ids = [1, 2, 3, 4, 5]  # 分配部分菜单
        result = requests.put('http://localhost:8081/api/system/role/6/menus', json=menu_ids, headers=h)
        print(f'状态码：{result.status_code}')
        if result.status_code == 200:
            print(f'响应：{result.json()}')
            print('✓ 权限分配成功')
        
        # 7. 删除角色
        print('\n7. 删除测试角色 (ID=6):')
        result = requests.delete('http://localhost:8081/api/system/role/6', headers=h)
        print(f'状态码：{result.status_code}')
        if result.status_code == 200:
            print(f'响应：{result.json()}')
            print('✓ 删除成功')
        
        # 8. 验证删除后的角色列表
        print('\n8. 验证删除后的角色列表:')
        result = requests.get('http://localhost:8081/api/system/role/list', headers=h)
        print(f'状态码：{result.status_code}')
        if result.status_code == 200:
            roles = result.json()
            if roles.get('code') == 200 and roles.get('data'):
                print(f'当前角色数量：{len(roles["data"])}')
                for role in roles['data']:
                    print(f"  - {role['roleName']} ({role['roleCode']})")
        
        print('\n=== 测试完成 ===')
    else:
        print('登录失败：', data)
else:
    print('HTTP 请求失败:', r.status_code, r.text)
