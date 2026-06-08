import subprocess
import os

os.chdir(r'd:\youxi\AI_project\dd')

# 使用utf8mb4字符集执行SQL脚本
result = subprocess.run(
    ['mysql', '--defaults-extra-file=.my.cnf', '-e', 'SET NAMES utf8mb4; SOURCE docker/mysql/init/init.sql;'],
    capture_output=True,
    text=True,
    encoding='utf-8'
)

if result.returncode == 0:
    print("Database initialized successfully!")
else:
    print(f"Error: {result.stderr}")