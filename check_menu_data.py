import pymysql

# 数据库配置
db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': '123456',
    'database': 'smart_inventory',
    'charset': 'utf8mb4'
}

try:
    connection = pymysql.connect(**db_config)
    cursor = connection.cursor(pymysql.cursors.DictCursor)
    
    # 查询所有菜单
    cursor.execute("SELECT id, parent_id, menu_name, path, icon, sort FROM sys_menu ORDER BY sort, id")
    menus = cursor.fetchall()
    
    print("所有菜单数据:")
    print("-" * 80)
    for menu in menus:
        print(f"ID: {menu['id']:2d} | ParentID: {menu['parent_id']:2d} | 名称: {menu['menu_name']:10} | 路径: {menu['path']:20} | 图标: {menu['icon']}")
    
    # 检查子菜单
    print("\n子菜单列表 (parent_id != 0):")
    print("-" * 80)
    child_menus = [m for m in menus if m['parent_id'] != 0]
    for menu in child_menus:
        print(f"ID: {menu['id']:2d} | ParentID: {menu['parent_id']:2d} | 名称: {menu['menu_name']}")

except Exception as e:
    print(f"错误: {e}")
finally:
    if connection:
        connection.close()