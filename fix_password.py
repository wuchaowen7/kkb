import subprocess
import bcrypt

# 生成BCrypt密码
password = b"admin123"
hashed = bcrypt.hashpw(password, bcrypt.gensalt())
print(f"Generated BCrypt hash: {hashed.decode()}")

# 更新数据库密码
cmd = f"mysql -h localhost -u root -p123456 -D smart_inventory -e \"UPDATE sys_user SET password = '{hashed.decode()}' WHERE username = 'admin';\""
result = subprocess.run(cmd, shell=True, capture_output=True, text=True)
print("Exit code:", result.returncode)
if result.stdout:
    print("STDOUT:", result.stdout)
if result.stderr:
    print("STDERR:", result.stderr)

# 测试登录
import requests
response = requests.post('http://localhost:8081/api/auth/login', json={'username': 'admin', 'password': 'admin123'})
print(f"\nLogin test: {response.status_code}")
print(response.text)