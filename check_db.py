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
        cursor.execute("SELECT id, menu_name, icon FROM sys_menu LIMIT 10")
        results = cursor.fetchall()
        print("菜单数据：")
        for row in results:
            print(f"ID: {row[0]}, 菜单名称: {row[1]}, 图标: {row[2]}")
finally:
    conn.close()