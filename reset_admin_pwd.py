import requests
import hashlib

# 直接访问数据库可能更简单，但我们尝试用另一种方式
# 先检查数据库中的用户
print("需要重置admin密码，请手动在数据库中执行：")
print("UPDATE sys_user SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq' WHERE username = 'admin';")
print("\n上面是 BCrypt 加密后的 'admin123'")
