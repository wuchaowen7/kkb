import subprocess
import os

def test_direct_db_connection():
    print("=== Testing MySQL Connection ===")
    
    # 使用mysql命令行工具测试
    cmd = [
        'mysql', '-h', 'localhost', '-u', 'root', '-p123456', 
        '-D', 'smart_inventory', '-e', 'SELECT COUNT(*) FROM sys_user;'
    ]
    
    try:
        result = subprocess.run(cmd, capture_output=True, text=True, timeout=30)
        print("Exit code:", result.returncode)
        print("STDOUT:", result.stdout)
        if result.stderr:
            print("STDERR:", result.stderr)
    except Exception as e:
        print("Error:", str(e))

def test_direct_api_call():
    print("\n=== Testing Direct API Calls ===")
    
    import requests
    
    # 先获取token
    login_data = {'username': 'admin', 'password': 'admin123'}
    try:
        response = requests.post('http://localhost:8081/api/auth/login', json=login_data)
        print("Direct login to server-system (8081):")
        print("Status:", response.status_code)
        print("Response:", response.text[:300])
        
        if response.status_code == 200:
            token = response.json().get('data', {}).get('accessToken') or response.json().get('data', {}).get('token')
            headers = {'Authorization': 'Bearer %s' % token}
            
            # 直接调用server-system的用户分页接口
            print("\nDirect call to user page API:")
            response = requests.get('http://localhost:8081/api/system/user/page?pageNum=1&pageSize=10', headers=headers)
            print("Status:", response.status_code)
            print("Response:", response.text[:500])
            
    except Exception as e:
        print("Error:", str(e))

if __name__ == '__main__':
    test_direct_db_connection()
    test_direct_api_call()