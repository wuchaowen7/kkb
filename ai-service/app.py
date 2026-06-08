from flask import Flask, jsonify
from config import Config
from api.predict_routes import create_predict_blueprint

def create_app():
    app = Flask(__name__)
    app.config.from_object(Config)

    # 注册 API 蓝图，使用工厂函数进行依赖注入
    app.register_blueprint(create_predict_blueprint(), url_prefix='/api/v1')

    @app.route('/health')
    def health():
        return jsonify({'status': 'ok', 'service': 'ai-prediction'})

    return app

if __name__ == '__main__':
    app = create_app()
    app.run(host='0.0.0.0', port=Config.PORT, debug=Config.DEBUG)
