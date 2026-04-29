# 门诊 HIS 教学演示系统

[![CI](https://github.com/hmc-edu/his/actions/workflows/ci.yml/badge.svg)](https://github.com/hmc-edu/his/actions/workflows/ci.yml)
[![Release](https://github.com/hmc-edu/his/actions/workflows/release.yml/badge.svg)](https://github.com/hmc-edu/his/actions/workflows/release.yml)

一个面向教学的门诊医院信息系统（HIS）演示项目，覆盖最小可运行的业务闭环：**登录 → 挂号 → 就诊（病历 + 处方）**。

## 技术栈

- 后端：Spring Boot 3.1.9 + MyBatis 3.0 + MySQL 8 + Sa-Token + SpringDoc OpenAPI
- 前端：Vue 3 + Vite + Element Plus + Pinia + Vue Router + Axios
- JDK 17，Node 18+

## 三种使用方式

### A. 一键演示（推荐学生看效果）

只要装了 Docker，一条命令即可拉起完整系统（MySQL + 后端 + 前端）：

```bash
docker compose up --build
```

启动完成后浏览器访问：

- 前端：<http://localhost:8000>
- 后端 API 文档：<http://localhost:8080/swagger-ui.html>

默认账号：`admin / reception / doctor`，密码均为 `123456`。

> 第一次启动需要拉取 mysql/node/openjdk 镜像并构建，大约 5–10 分钟。
> 重置数据：`docker compose down -v && docker compose up --build`

### B. 直接拉镜像（完全跳过编译）

每打一个 `vX.Y.Z` 标签时，CI 会自动把后端/前端镜像推到 GitHub Container Registry：

```bash
docker pull ghcr.io/hmc-edu/his-backend:latest
docker pull ghcr.io/hmc-edu/his-frontend:latest
```

替换 `docker-compose.yml` 中的 `build:` 为 `image:` 即可直接跑。

> **首次发布需要把镜像设为 Public**（GHCR 默认私有）：管理员进入
> <https://github.com/orgs/hmc-edu/packages/container/his-backend/settings>
> 与 <https://github.com/orgs/hmc-edu/packages/container/his-frontend/settings>，
> 在 "Danger Zone" 把 visibility 改为 Public。否则学生需要先 `docker login ghcr.io`。

### C. 本地开发模式（学生改代码用）

```bash
# 1. 数据库
mysql -uroot -p
> CREATE DATABASE his DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
> USE his;
> SOURCE backend/src/main/resources/db/schema.sql;
> SOURCE backend/src/main/resources/db/data.sql;

# 2. 后端
cd backend && ./gradlew bootRun

# 3. 前端
cd frontend && pnpm install && pnpm dev
# 访问 http://localhost:5173
```

详细文档见 [`docs/`](./docs)：

- [01-环境准备](./docs/01-环境准备.md)
- [02-启动指南](./docs/02-启动指南.md)
- [03-业务流程](./docs/03-业务流程.md)
- [04-扩展练习](./docs/04-扩展练习.md)

## 目录结构

```
his/
├── backend/                Spring Boot 后端
├── frontend/               Vue 3 前端
├── docs/                   教学文档与扩展练习
├── docker-compose.yml      一键演示编排
└── .github/workflows/      CI（自动构建）+ Release（打 tag 自动出镜像）
```

## 角色与默认账号

| 用户名 | 密码 | 角色 | 可访问模块 |
| --- | --- | --- | --- |
| admin | 123456 | ADMIN | 全部 |
| reception | 123456 | RECEPTION | 挂号台 |
| doctor | 123456 | DOCTOR | 医生工作站 |

## 主线流程

```
RECEPTION 登录 → 患者建档 → 挂号 → 生成挂号单
   ↓ 状态: WAITING
DOCTOR 登录 → 待诊队列 → 开始接诊 → 写病历 → 开处方 → 完成就诊
   ↓ 状态: VISITING → DONE
```

## CI/CD 说明

- **`.github/workflows/ci.yml`**：每次 `push` / `pull_request` 到 `main` 时，分别构建后端 jar 与前端 dist，跑单元测试，并把产物作为 14 天保留期的 Artifact 上传到 Actions 页。
- **`.github/workflows/release.yml`**：当推送 `vX.Y.Z` 标签时（如 `git tag v0.1.0 && git push --tags`），CI 会：
  1. 构建后端 jar、前端 dist，打到 GitHub Release 附件
  2. 构建后端/前端 Docker 镜像并推送到 `ghcr.io/hmc-edu/his-backend`、`ghcr.io/hmc-edu/his-frontend`

## 给学生的扩展空间

主线之外，留有充足扩展点：门诊收费、药房发药、检验申请、报表统计、完整 RBAC、排班号源、AOP 审计日志等。详见 `docs/04-扩展练习.md`。
