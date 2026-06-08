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
        cursor.execute("SELECT id, parent_id, menu_name FROM sys_menu ORDER BY id")
        results = cursor.fetchall()
        print("菜单数据（ID, 父ID, 名称）：")
        for row in results:
            indent = "  " * (row[1] > 0)
            print(f"{indent}ID: {row[0]}, 父ID: {row[1]}, 名称: {row[2]}")
finally:
    conn.close()