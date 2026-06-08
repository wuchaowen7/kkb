import bcrypt
import subprocess

password = 'admin123'.encode('utf-8')
hashed = bcrypt.hashpw(password, bcrypt.gensalt())
print('Generated hash:', hashed.decode())

result = subprocess.run(
    ['mysql', '-u', 'root', '-p123456', '-e', f"USE smart_inventory; UPDATE sys_user SET password = '{hashed.decode()}' WHERE username = 'admin';"],
    capture_output=True, text=True
)
print('MySQL stdout:', result.stdout)
print('MySQL stderr:', result.stderr)
print('Return code:', result.returncode)
