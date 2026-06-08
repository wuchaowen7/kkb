import requests

def test_service(name, url):
    try:
        response = requests.get(url, timeout=3)
        status = response.status_code
        print(f"{name}: {'✅ 正常' if status == 200 else f'⚠️ 状态码{status}'}")
        return status == 200
    except Exception as e:
        print(f"{name}: ❌ 失败 - {str(e)[:50]}")
        return False

print("检查各服务状态:")
print("-" * 50)
test_service("System服务 (8081)", "http://localhost:8081/api/auth/captcha")
test_service("Gateway服务 (8080)", "http://localhost:8080/api/auth/captcha")
test_service("Base服务 (8082)", "http://localhost:8082/api/base/category/tree")
test_service("Stock服务 (8083)", "http://localhost:8083/api/stock/inbound/page")
test_service("Intelligence服务 (8084)", "http://localhost:8084/api/intelligence/alerts")
test_service("Report服务 (8085)", "http://localhost:8085/api/report/dashboard/summary")