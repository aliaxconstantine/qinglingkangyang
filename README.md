# 秦岭康养数据可视化平台

<div align="center">

面向秦岭地区的康养服务、景点与地方特产数据采集、管理和可视化平台。

[![Vue](https://img.shields.io/badge/Vue-3.5-42b883?logo=vuedotjs&logoColor=white)](https://vuejs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](qlky-admin/LICENSE)

</div>

## ✨ 功能亮点

- **数据大屏**：展示康养服务、景点评分、评论热度、地方特产分布与天气信息。
- **特产地图**：根据产地与关键词将特产归属到县区；支持鼠标拖拽平移、滚轮缩放。
- **爬虫任务**：支持景点、特产、康养服务数据的脚本任务配置与执行。
- **数据管理**：统一管理爬虫字段、采集记录、任务状态与图表数据。
- **实时呈现**：特产详情自动轮播，图表直接使用数据库聚合后的最新数据。

## 🧱 技术栈

| 模块 | 技术 |
| --- | --- |
| 前端 | Vue 3、TypeScript、Vite、Element Plus、ECharts、DataV |
| 后端 | Java 17、Spring Boot 3、MyBatis-Plus、WebSocket |
| 数据库 | MySQL 8 |
| 数据采集 | Python 脚本、Excel/XML 数据源 |

## 📁 项目结构

```text
.
├── qlky-admin/           # Vue 3 管理端与数据大屏
├── qlky-api/             # Spring Boot 后端接口与任务调度
│   ├── sql/               # MySQL 表结构与任务初始化脚本
│   └── src/main/resources/ # 后端配置与 Mapper
├── file/                 # 景点、特产、康养服务采集脚本与示例数据
└── README.md
```

## 🚀 快速开始

### 1. 环境要求

- Node.js `>= 16`
- JDK `17`
- Maven `3.8+`
- MySQL `8.0+`
- Python `3.9+`（仅运行采集脚本时需要）

### 2. 初始化数据库

在 MySQL 中执行以下 SQL，先创建表结构，再初始化爬虫任务与字段映射：

```powershell
mysql -uroot -p < qlky-api/sql/qlky-schema-mysql.sql
mysql -uroot -p qlky < qlky-api/sql/qlky-seed-crawler-tasks.sql
```

> `qlky-seed-crawler-tasks.sql` 可重复执行。不要导入运行时备份数据；项目已提供标准 MySQL 初始化脚本。

### 3. 配置后端

复制配置模板，并以环境变量提供数据库账号：

```powershell
Copy-Item qlky-api/src/main/resources/private.yml.example qlky-api/src/main/resources/private.yml
$env:QLKY_DB_USERNAME = 'root'
$env:QLKY_DB_PASSWORD = '你的 MySQL 密码'
```

如数据库地址不是默认值，可额外设置 `QLKY_DB_URL`。`private.yml` 已被 Git 忽略，不会提交本地凭据。

### 4. 启动后端

```powershell
cd qlky-api
mvn spring-boot:run
```

后端默认监听 `http://localhost:9997`。

### 5. 启动前端

新开一个终端窗口：

```powershell
cd qlky-admin
npm install
npm run dev
```

前端开发服务器默认地址为 `http://localhost:8888`，开发环境已配置接口地址 `http://localhost:9997`。

## 📊 大屏与数据采集

1. 进入管理端的数据大屏，使用顶部按钮在康养服务、特产、天气数据间切换。
2. 在“爬虫任务”中执行初始化任务，或运行 `file/jd`、`file/tc`、`file/ky` 下的采集脚本。
3. 采集脚本提交的数据会写入 `crawler_data`，大屏图表通过 `/pageData` 接口读取最新记录。

## 🛠 常用命令

```powershell
# 前端生产构建
cd qlky-admin
npm run build

# 后端测试
cd qlky-api
mvn test

# 推送当前仓库
git push -u origin main
```

## 🔐 配置说明

- `qlky-admin/.env.development`：前端开发接口地址。
- `qlky-api/src/main/resources/private.yml`：本地数据库连接配置，不提交到仓库。
- `qlky-api/sql/qlky-schema-mysql.sql`：数据库表结构。
- `qlky-api/sql/qlky-seed-crawler-tasks.sql`：任务、爬虫与字段初始化数据。

## 📄 License

前端基础框架采用 [MIT License](qlky-admin/LICENSE)。
