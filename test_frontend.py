import requests

# 通过前端开发服务器访问（端口5173）
print('=== 通过前端开发服务器访问 ===')

# 登录
r = requests.post('http://localhost:5173/api/auth/login', json={'username':'admin','password':'123'})
print('登录:', r.status_code)
if r.status_code == 200:
    data = r.json()
    print('登录响应:', data)
    token = data['data']['token']
    h = {'Authorization': 'Bearer ' + token}
    
    # 访问用户列表
    result = requests.get('http://localhost:5173/api/system/user/page', params={'pageNum':1, 'pageSize':10}, headers=h)
    print('\n用户列表:', result.status_code)
    if result.status_code == 500:
        print('错误详情:', result.text[:2000])
    elif result.status_code == 200:
        print('成功:', result.json()['data']['total'], '条')
