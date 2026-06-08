# 智能库存管理系统

一个基于微服务架构的企业级智能库存管理平台，提供完整的库存管理、智能分析、报表统计等功能。

## 项目介绍

本系统采用前后端分离的微服务架构，整合了 AI 预测能力，为企业提供智能化的库存管理解决方案。

### 核心特性

- 🚀 **微服务架构**：Spring Cloud Gateway + 多服务模块
- 🤖 **AI 智能预测**：销量预测、智能补货建议
- 📊 **数据可视化**：ECharts 图表展示
- 🔐 **权限管理**：基于 RBAC 的权限控制
- 📈 **实时监控**：库存预警、临期提醒
- 📝 **报表导出**：Excel 导出功能

## 技术架构

### 前端技术栈
- **框架**: Vue 3 (组合式API)
- **语言**: TypeScript
- **构建工具**: Vite 6.5.0
- **UI 组件库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router
- **图表库**: ECharts
- **HTTP 客户端**: Axios

### 后端技术栈
- **框架**: Spring Boot 3.2.5
- **语言**: Java 17
- **数据库**: MySQL 8.0
- **ORM**: MyBatis Plus 3.5.6
- **认证**: JWT + Spring Security
- **网关**: Spring Cloud Gateway
- **导出**: EasyExcel

### AI 服务
- **框架**: Flask
- **语言**: Python 3.9
- **预测算法**: LSTM/ARIMA/统计学方法

## 微服务架构

| 服务 | 端口 | 功能描述 |
|------|------|----------|
| server-gateway | 8080 | API 网关、路由转发、CORS |
| server-system | 8081 | 用户、角色、权限、菜单管理 |
| server-base | 8082 | 商品、分类、仓库、供应商管理 |
| server-stock | 8083 | 入库、出库、调拨、盘点、退库 |
| server-intelligence | 8084 | 预警、销量预测、智能分析、补货 |
| server-report | 8085 | 数据大屏、进销存报表、库存明细 |
| ai-service | 5000 | AI 预测服务 |
| frontend | 5173 | 前端开发服务器 |

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
- 销量预测：基于 AI 的销量预测
- 智能补货：智能补货建议
- 智能分析：多维度数据分析

### 报表中心
- 数据大屏：实时数据可视化
- 进销存报表：出入库统计
- 库存明细：详细库存信息（支持 Excel 导出）

### 系统管理
- 用户管理：用户账号管理
- 角色管理：角色权限配置
- 菜单管理：系统菜单配置
- 操作日志：操作记录查询
- 系统配置：系统参数设置
- 数据备份：数据备份与恢复

## 快速开始

### 环境要求

- **JDK**: 17+
- **Node.js**: 18+
- **Python**: 3.9+
- **MySQL**: 8.0+
- **Maven**: 3.8+

### 1. 克隆项目

```bash
git clone <repository-url>
cd dd
```

### 2. 数据库初始化

```bash
# 启动 MySQL 容器（使用 Docker）
cd docker
docker-compose up -d mysql

# 或使用本地 MySQL，执行初始化脚本
mysql -u root -p < docker/mysql/init/init.sql
```

### 3. 启动后端服务

```bash
# 编译所有服务
cd server
mvn clean package -DskipTests

# 启动所有服务（Windows）
cd ..
start-all.bat

# 或手动启动各个服务
cd server/server-gateway && mvn spring-boot:run
cd server/server-system && mvn spring-boot:run
cd server/server-base && mvn spring-boot:run
cd server/server-stock && mvn spring-boot:run
cd server/server-intelligence && mvn spring-boot:run
cd server/server-report && mvn spring-boot:run
```

### 4. 启动 AI 服务

```bash
cd ai-service
pip install -r requirements.txt
python app.py
```

### 5. 启动前端服务

```bash
cd web
npm install
npm run dev
```

### 6. 访问系统

- 前端地址: http://localhost:5173
- 网关地址: http://localhost:8080
- AI 服务: http://localhost:5000

**默认账号**: admin / admin123

## 目录结构

```
dd/
├── ai-service/              # AI 预测服务
│   ├── api/                 # API 路由
│   ├── services/            # 预测服务
│   ├── config.py            # 配置文件
│   └── app.py               # 应用入口
├── docker/                  # Docker 配置
│   ├── mysql/               # MySQL 初始化脚本
│   ├── nginx/               # Nginx 配置
│   └── docker-compose.yml   # Docker Compose 配置
├── server/                  # 后端服务
│   ├── server-common/       # 公共模块
│   ├── server-gateway/      # 网关服务
│   ├── server-system/       # 系统管理服务
│   ├── server-base/         # 基础数据服务
│   ├── server-stock/        # 库存管理服务
│   ├── server-intelligence/ # 智能分析服务
│   └── server-report/       # 报表服务
├── web/                     # 前端项目
│   ├── src/
│   │   ├── api/             # API 接口
│   │   ├── assets/          # 静态资源
│   │   ├── components/      # 公共组件
│   │   ├── router/          # 路由配置
│   │   ├── stores/          # 状态管理
│   │   ├── utils/           # 工具函数
│   │   └── views/           # 页面视图
│   ├── package.json
│   └── vite.config.ts
├── start-all.bat            # 启动所有服务（Windows）
├── stop-all.bat             # 停止所有服务（Windows）
└── README.md                # 项目说明
```

## 配置说明

### 后端配置

各服务的配置文件位于 `server/*/src/main/resources/application.yml`

主要配置项：
- 数据库连接
- 服务端口
- JWT 密钥
- AI 服务地址

### 前端配置

开发环境配置文件: `web/.env.development`
```env
VITE_APP_API_BASE_URL=http://localhost:8080/api
```

生产环境配置文件: `web/.env.production`
```env
VITE_APP_API_BASE_URL=http://your-server/api
```

### AI 服务配置

AI 服务配置文件: `ai-service/config.py`
```python
PORT = 5000
DEBUG = True
MODEL_DIR = 'models/saved'
```

## 开发规范

### 后端开发规范
1. **包结构**: 按功能模块组织（controller、service、mapper）
2. **命名规范**: 驼峰命名法
3. **异常处理**: 统一使用 GlobalExceptionHandler
4. **日志记录**: 使用 @OperationLog 注解记录操作日志
5. **权限控制**: 使用 @RequiresPermissions 注解

### 前端开发规范
1. **组件命名**: 使用 PascalCase 命名组件文件
2. **变量命名**: 使用 camelCase 命名变量和函数
3. **文件结构**: 按功能模块组织文件
4. **注释规范**: 为关键代码添加注释说明
5. **类型定义**: 使用 TypeScript 严格模式

### Git 提交规范
```
feat: 新功能
fix: 修复 bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
test: 测试相关
chore: 构建/工具链相关
```

## 部署说明

### Docker 部署

```bash
# 构建所有服务镜像
docker-compose build

# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 生产环境部署

1. **数据库部署**: 使用 MySQL 主从复制
2. **后端部署**: 使用 Docker/K8s 部署各微服务
3. **前端部署**: 使用 Nginx 部署静态资源
4. **AI 服务**: 使用 Gunicorn + Nginx 部署
5. **负载均衡**: 使用 Nginx 进行负载均衡

## 常见问题

### 1. 端口冲突
如果端口被占用，请修改对应服务的配置文件中的端口配置。

### 2. 数据库连接失败
检查 MySQL 服务是否启动，用户名密码是否正确。

### 3. AI 服务无法访问
确保 Python 环境正确，依赖包已安装。

### 4. 前端无法访问后端
检查网关服务是否启动，CORS 配置是否正确。

## 贡献指南

欢迎提交 Issue 和 Pull Request！

## 许可证

MIT License

## 联系方式

如有问题，请提交 Issue 或联系项目维护者。