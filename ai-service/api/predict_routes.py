from flask import Blueprint, request, jsonify
from services.prediction_service import PredictionService

predict_bp = Blueprint('predict', __name__)

def create_predict_blueprint(prediction_service=None):
    """工厂函数，支持依赖注入，方便测试和配置管理"""
    if prediction_service is None:
        prediction_service = PredictionService()
    
    @predict_bp.route('/predict/sales', methods=['POST'])
    def predict_sales():
        """销量预测
        Body: { "product_ids": [1,2,3], "period": 7 }
        Returns: { "predictions": { "1": [12,15,18,...], ... } }
        """
        data = request.get_json() or {}
        product_ids = data.get('product_ids', [])
        period = data.get('period', 7)

        if period not in [7, 15, 30]:
            return jsonify({'error': 'period must be 7, 15 or 30'}), 400

        predictions = {}
        for pid in product_ids:
            predictions[str(pid)] = prediction_service.predict(pid, period)

        return jsonify({'predictions': predictions})

    @predict_bp.route('/predict/replenish', methods=['POST'])
    def predict_replenish():
        """补货建议
        Body: { "product_id": 1, "current_stock": 100, "safety_stock": 50, "in_transit": 0 }
        """
        data = request.get_json() or {}
        product_id = data.get('product_id')
        current_stock = data.get('current_stock', 0)
        safety_stock = data.get('safety_stock', 0)
        in_transit = data.get('in_transit', 0)

        # 添加空值检查
        if product_id is None:
            return jsonify({'error': 'product_id is required'}), 400

        # 预测未来30天销量
        predicted_sales = sum(prediction_service.predict(product_id, 30))
        # 建议采购量 = 预测销量 - 当前库存 + 安全库存 - 在途数量
        suggested_qty = max(0, predicted_sales - current_stock + safety_stock - in_transit)

        return jsonify({
            'product_id': product_id,
            'suggested_qty': int(suggested_qty),
            'predicted_sales': int(predicted_sales),
            'current_stock': current_stock,
            'safety_stock': safety_stock,
            'in_transit': in_transit,
            'formula': f'预测销量({int(predicted_sales)}) - 当前库存({current_stock}) + 安全库存({safety_stock}) - 在途({in_transit})',
            'method': 'statistical-fallback'
        })

    @predict_bp.route('/model/retrain', methods=['POST'])
    def retrain():
        """触发模型重训练"""
        # TODO: 从主后端拉取最新数据并重新训练模型
        return jsonify({'message': '模型重训练已触发', 'status': 'started'})

    @predict_bp.route('/model/health', methods=['GET'])
    def model_health():
        return jsonify({'status': 'healthy', 'models_loaded': prediction_service.models_loaded()})
    
    return predict_bp
