import requests

# 登录获取token
r = requests.post('http://localhost:8081/api/auth/login', json={'username':'admin','password':'admin123'})
print('登录响应:', r.status_code, r.text)

if r.status_code == 200:
    data = r.json()
    if data['code'] == 200:
        token = data['data']['token']
        h = {'Authorization': 'Bearer ' + token}
        
        # 测试用户管理接口
        print('\n=== 测试用户管理接口 ===')
        result = requests.get('http://localhost:8081/api/system/user/page', params={'pageNum':1, 'pageSize':10}, headers=h)
        print('用户列表:', result.status_code)
        if result.status_code == 500:
            print('错误详情:', result.text)
