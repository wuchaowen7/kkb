import numpy as np
import random
import os
from datetime import datetime, timedelta
import json
from config import Config

class PredictionService:
    """
    销量预测服务
    - 数据充足时使用 LSTM/ARIMA（需训练后）
    - 当前作为骨架，使用统计学方法兜底（移动平均 + 趋势）
    """

    def __init__(self):
        self.model_loaded = False
        self.model_dir = Config.MODEL_DIR
        os.makedirs(self.model_dir, exist_ok=True)

    def models_loaded(self):
        return self.model_loaded

    def predict(self, product_id: int, period: int = 7) -> list:
        """
        预测指定商品未来 period 天的销量
        返回: [day1, day2, ..., dayN] 预测销量列表
        """
        # TODO: 实际从主后端 API 获取历史数据
        # 当前使用统计学方法生成模拟预测
        history = self._get_history(product_id)

        if len(history) >= 90:
            # 数据充足：加权移动平均 + 指数平滑
            return self._weighted_moving_average(history, period)
        elif len(history) >= 7:
            # 数据有限：简单移动平均
            return self._simple_moving_average(history, period)
        else:
            # 冷启动：基于同类商品均值或简单估算
            return self._cold_start(period)

    def _get_history(self, product_id: int) -> list:
        """
        从主后端或本地缓存获取历史日销量数据
        TODO: 实际调用后端 API: GET /api/intelligence/sales-history/{product_id}
        """
        # 模拟数据 — 生成过去90天的随机销量
        np.random.seed(product_id)
        today = datetime.now()
        history = []
        for i in range(90, 0, -1):
            date = today - timedelta(days=i)
            # 模拟周内/周末波动
            weekday = date.weekday()
            base = 50 + product_id * 3
            seasonal = 20 * np.sin(2 * np.pi * i / 7) + 10 * np.sin(2 * np.pi * i / 30)
            noise = np.random.normal(0, 10)
            daily = max(0, int(base + seasonal + noise))
            history.append(daily)
        return history

    def _weighted_moving_average(self, history: list, period: int) -> list:
        """加权移动平均法预测"""
        predictions = []
        weights = list(range(1, min(30, len(history)) + 1))
        total_weight = sum(weights)
        last_n = history[-len(weights):]

        for i in range(period):
            # 加权平均
            avg = sum(w * v for w, v in zip(weights, last_n[-len(weights):])) / total_weight
            # 加轻微趋势调整
            trend = (history[-1] - history[-8]) / 8 if len(history) >= 8 else 0
            pred = max(0, int(avg + trend * i))
            predictions.append(pred)
        return predictions

    def _simple_moving_average(self, history: list, period: int) -> list:
        """简单移动平均预测"""
        avg = int(np.mean(history[-7:]))
        return [max(0, avg + random.randint(-5, 5)) for _ in range(period)]

    def _cold_start(self, period: int) -> list:
        """冷启动：基于同类商品或保守估算"""
        base = 30
        return [max(0, base + random.randint(-5, 8)) for _ in range(period)]
