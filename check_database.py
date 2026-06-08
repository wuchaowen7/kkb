import mysql.connector

def check_database():
    try:
        # 连接数据库
        conn = mysql.connector.connect(
            host='localhost',
            user='root',
            password='123456',
            database='smart_inventory'
        )
        
        cursor = conn.cursor()
        
        # 检查用户表
        print("=== Checking sys_user table ===")
        cursor.execute("SELECT COUNT(*) FROM sys_user")
        result = cursor.fetchone()
        print("sys_user count:", result[0])
        
        # 检查商品表
        print("\n=== Checking base_product table ===")
        cursor.execute("SELECT COUNT(*) FROM base_product")
        result = cursor.fetchone()
        print("base_product count:", result[0])
        
        # 检查库存表
        print("\n=== Checking stock_inventory table ===")
        cursor.execute("SELECT COUNT(*) FROM stock_inventory")
        result = cursor.fetchone()
        print("stock_inventory count:", result[0])
        
        # 测试分页查询
        print("\n=== Testing pagination query ===")
        cursor.execute("SELECT * FROM sys_user LIMIT 0, 10")
        rows = cursor.fetchall()
        print("Pagination query result count:", len(rows))
        
        conn.close()
        print("\nDatabase connection and tables are OK!")
        
    except Exception as e:
        print("Database error:", str(e))

if __name__ == '__main__':
    check_database()