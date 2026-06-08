import os

class Config:
    DEBUG = os.getenv('DEBUG', 'false').lower() == 'true'
    PORT = int(os.getenv('PORT', 5000))
    # 主后端 API 地址，用于拉取历史销售数据
    BACKEND_URL = os.getenv('BACKEND_URL', 'http://localhost:8080/api')
    MODEL_DIR = os.path.join(os.path.dirname(__file__), 'models', 'saved')
