import requests

# 通过网关登录
r = requests.post('http://localhost:8080/api/auth/login', json={'username':'admin','password':'123'})
print('网关登录:', r.status_code)
if r.status_code == 200:
    data = r.json()
    token = data['data']['token']
    h = {'Authorization': 'Bearer ' + token}
    
    # 通过网关访问用户管理接口
    result = requests.get('http://localhost:8080/api/system/user/page', params={'pageNum':1, 'pageSize':10}, headers=h)
    print('网关用户列表:', result.status_code)
    if result.status_code == 500:
        print('错误:', result.text)
    elif result.status_code == 200:
        print('成功:', result.json()['data']['total'], '条记录')
