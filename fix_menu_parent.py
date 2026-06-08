import pymysql

# 连接数据库
conn = pymysql.connect(
    host='localhost',
    user='root',
    password='123456',
    database='smart_inventory',
    charset='utf8mb4'
)

try:
    with conn.cursor() as cursor:
        # 修复智能模块的子菜单（应该指向ID 13）
        cursor.execute("UPDATE sys_menu SET parent_id = 13 WHERE id IN (14, 15, 16, 17)")
        
        # 修复报表的子菜单（应该指向ID 18）
        cursor.execute("UPDATE sys_menu SET parent_id = 18 WHERE id IN (19, 20, 21)")
        
        # 修复系统管理的子菜单（应该指向ID 22）
        cursor.execute("UPDATE sys_menu SET parent_id = 22 WHERE id IN (23, 24, 25, 26, 27, 28)")
        
        conn.commit()
        print("菜单父ID已修复成功!")
        
        # 验证修复结果
        cursor.execute("SELECT id, parent_id, menu_name FROM sys_menu ORDER BY id")
        results = cursor.fetchall()
        print("\n修复后的菜单数据：")
        for row in results:
            indent = "  " * (row[1] > 0)
            print(f"{indent}ID: {row[0]}, 父ID: {row[1]}, 名称: {row[2]}")
            
finally:
    conn.close()