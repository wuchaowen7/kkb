import requests

# 登录获取token
r = requests.post('http://localhost:8081/api/auth/login', json={'username':'admin','password':'admin123'})
print('登录响应:', r.status_code, r.text)
token = r.json()['data']['token']
h = {'Authorization': 'Bearer ' + token}

print('\n=== 测试搜索功能 ===')
result = requests.get('http://localhost:8081/api/system/user/page', params={'pageNum':1, 'pageSize':10, 'keyword':'admin'}, headers=h)
print('搜索 admin:', result.json()['data']['total'], '条')

result = requests.get('http://localhost:8081/api/system/user/page', params={'pageNum':1, 'pageSize':10, 'keyword':'zhang'}, headers=h)
print('搜索 zhang:', result.json()['data']['total'], '条')

print('\n=== 测试重置密码 ===')
result = requests.put('http://localhost:8081/api/system/user/2/resetPwd', json={'oldPassword':'wrong', 'newPassword':'newpwd123'}, headers=h)
print('错误原密码:', result.json())

result = requests.put('http://localhost:8081/api/system/user/2/resetPwd', json={'oldPassword':'123456', 'newPassword':'newpwd123'}, headers=h)
print('正确原密码:', result.json())
