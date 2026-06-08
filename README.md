# 智能库存管理系统 - 前端

基于 Vue 3 + TypeScript + Element Plus 构建的智能库存管理系统前端应用。

## 项目介绍

本系统是一个企业级的智能库存管理平台，提供完整的库存管理、智能分析、报表统计等功能。

## 技术栈

- **框架**: Vue 3 (组合式API)
- **语言**: TypeScript
- **构建工具**: Vite 6.5.0
- **UI 组件库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router
- **图表库**: ECharts
- **HTTP 客户端**: Axios

## 功能模块

### 基础数据管理
- 商品管理：商品信息的增删改查
- 分类管理：商品分类树结构管理
- 仓库管理：仓库信息管理
- 供应商管理：供应商信息管理

### 库存管理
- 库存查询：实时库存状态查询
- 入库管理：采购入库、退货入库
- 出库管理：销售出库、调拨出库
- 库存调拨：仓库间库存转移
- 库存盘点：定期库存盘点
- 退库管理：销售退货处理

### 智能分析
- 预警列表：库存预警、临期预警
- 销量预测：基于AI的销量预测
- 智能补货：智能补货建议
- 智能分析：多维度数据分析

### 报表中心
- 数据大屏：实时数据可视化
- 进销存报表：出入库统计
- 库存明细：详细库存信息

### 系统管理
- 用户管理：用户账号管理
- 角色管理：角色权限配置
- 菜单管理：系统菜单配置
- 操作日志：操作记录查询
- 系统配置：系统参数设置
- 数据备份：数据备份与恢复

## 快速开始

### 环境要求
- Node.js >= 18.0.0
- npm >= 9.0.0

### 安装依赖

```bash
npm install
```

### 开发模式

```bash
npm run dev
```

启动后访问: http://localhost:5173

### 构建生产版本

```bash
npm run build
```

### 预览生产版本

```bash
npm run preview
```

### 代码检查

```bash
npm run lint
```

## 目录结构

```
src/
├── api/                # API 接口定义
│   ├── modules.ts      # 各模块API方法
│   └── request.ts      # 请求封装
├── assets/             # 静态资源
│   ├── base.css        # 基础样式
│   ├── main.css        # 主样式
│   └── logo.svg        # 项目Logo
├── components/         # 公共组件
│   └── stock/          # 库存相关组件
├── router/             # 路由配置
│   └── index.ts        # 路由定义
├── stores/             # Pinia 状态管理
│   ├── user.ts         # 用户状态
│   ├── permission.ts   # 权限状态
│   └── counter.ts      # 示例状态
├── utils/              # 工具函数
│   └── icon.ts         # 图标工具
├── views/              # 页面视图
│   ├── base/           # 基础数据管理
│   ├── dashboard/      # 数据大屏
│   ├── intelligence/   # 智能分析
│   ├── layout/         # 布局组件
│   ├── login/          # 登录页面
│   ├── report/         # 报表中心
│   ├── stock/          # 库存管理
│   └── system/         # 系统管理
├── App.vue             # 根组件
└── main.ts             # 入口文件
```

## 配置说明

### 开发环境
开发环境配置文件: `.env.development`
```env
VITE_APP_API_BASE_URL=http://localhost:8080/api
```

### 生产环境
生产环境配置文件: `.env.production`
```env
VITE_APP_API_BASE_URL=http://your-server/api
```

## 开发规范

1. **组件命名**: 使用 PascalCase 命名组件文件
2. **变量命名**: 使用 camelCase 命名变量和函数
3. **文件结构**: 按功能模块组织文件
4. **注释规范**: 为关键代码添加注释说明
5. **类型定义**: 使用 TypeScript 严格模式

## 浏览器支持

- Chrome (推荐)
- Edge
- Firefox
- Safari

## 许可证

MIT License
