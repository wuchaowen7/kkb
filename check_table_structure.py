import subprocess
import os

def check_table_structure():
    print("=== Checking Database Table Structure ===\n")
    
    # 查询sys_user表结构
    cmd_user = [
        'mysql', '-h', 'localhost', '-u', 'root', '-p123456', 
        '-D', 'smart_inventory', '-e', 'DESCRIBE sys_user;'
    ]
    
    # 查询base_product表结构
    cmd_product = [
        'mysql', '-h', 'localhost', '-u', 'root', '-p123456', 
        '-D', 'smart_inventory', '-e', 'DESCRIBE base_product;'
    ]
    
    # 查询sys_role表结构（作为对比）
    cmd_role = [
        'mysql', '-h', 'localhost', '-u', 'root', '-p123456', 
        '-D', 'smart_inventory', '-e', 'DESCRIBE sys_role;'
    ]
    
    print("=== sys_user table ===")
    try:
        result = subprocess.run(cmd_user, capture_output=True, text=True, timeout=30)
        print(result.stdout)
        if result.stderr:
            print("STDERR:", result.stderr)
    except Exception as e:
        print("Error:", str(e))
    
    print("\n=== base_product table ===")
    try:
        result = subprocess.run(cmd_product, capture_output=True, text=True, timeout=30)
        print(result.stdout)
        if result.stderr:
            print("STDERR:", result.stderr)
    except Exception as e:
        print("Error:", str(e))
    
    print("\n=== sys_role table ===")
    try:
        result = subprocess.run(cmd_role, capture_output=True, text=True, timeout=30)
        print(result.stdout)
        if result.stderr:
            print("STDERR:", result.stderr)
    except Exception as e:
        print("Error:", str(e))

if __name__ == '__main__':
    check_table_structure()