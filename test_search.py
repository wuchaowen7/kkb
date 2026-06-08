import requests

# 使用正确密码登录
r = requests.post('http://localhost:8081/api/auth/login', json={'username':'admin','password':'123'})
data = r.json()
token = data['data']['token']
h = {'Authorization': 'Bearer ' + token}

print('=== 测试搜索功能 ===')

# 搜索 'wc'
result = requests.get('http://localhost:8081/api/system/user/page', params={'pageNum':1, 'pageSize':10, 'keyword':'wc'}, headers=h)
print('搜索 wc:', result.status_code)
if result.status_code == 200:
    print('结果数量:', result.json()['data']['total'])
    for user in result.json()['data']['list']:
        print(f"  - {user['username']}: {user['realName']}")

# 搜索 'zhang'
result = requests.get('http://localhost:8081/api/system/user/page', params={'pageNum':1, 'pageSize':10, 'keyword':'zhang'}, headers=h)
print('\n搜索 zhang:', result.status_code)
if result.status_code == 200:
    print('结果数量:', result.json()['data']['total'])
    for user in result.json()['data']['list']:
        print(f"  - {user['username']}: {user['realName']}")
