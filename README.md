# 杭州医学院门诊 HIS 系统

[![CI](https://github.com/hmc-edu/his/actions/workflows/ci.yml/badge.svg)](https://github.com/hmc-edu/his/actions/workflows/ci.yml)
[![Release](https://github.com/hmc-edu/his/actions/workflows/release.yml/badge.svg)](https://github.com/hmc-edu/his/actions/workflows/release.yml)

杭州医学院 信息系统类课程实训项目。覆盖门诊业务主线闭环：**登录 → 挂号 → 就诊（电子病历 + 处方）**。

## 技术栈

- 后端：Spring Boot 3.1.9 + MyBatis 3.0 + MySQL 8 + Sa-Token + SpringDoc OpenAPI
- 前端：Vue 3 + Vite + Element Plus + Pinia + Vue Router + Axios
- 运行环境：JDK 17、Node 18+、MySQL 8

> 🇨🇳 **校园网/国内网络环境用户请先看 [docs/05-国内网络优化.md](./docs/05-国内网络优化.md)**：项目内 gradle / npm / maven 已默认走国内镜像，但 Docker Hub 与 GHCR 还需要在本机做一次配置，否则首次拉镜像会很慢。

## 三种运行方式

### A. Docker Compose 一键启动（推荐）

只要装了 Docker，一条命令即可拉起完整系统（MySQL + 后端 + 前端）：

```bash
docker compose up --build
```

启动完成后浏览器访问：

- 前端：<http://localhost:8000>
- 后端 API 文档：<http://localhost:8080/swagger-ui.html>

默认账号：`admin / reception / doctor`，密码均为 `123456`。

> 第一次启动需要拉取 MySQL/Node/JDK 镜像并构建，约 5–10 分钟。
> 重置数据：`docker compose down -v && docker compose up --build`

### B. 直接拉取镜像（跳过编译）

每次推送 `vX.Y.Z` 标签时，CI 把后端/前端镜像推到 GitHub Container Registry。直接使用项目根目录的 `docker-compose.cn.yml` 即可：

```bash
docker compose -f docker-compose.cn.yml up
```

> **使用前提：管理员先把 GHCR 镜像设为 Public**（GitHub org 包默认私有）：
> <https://github.com/orgs/hmc-edu/packages/container/his-backend/settings>
> <https://github.com/orgs/hmc-edu/packages/container/his-frontend/settings>
> Danger Zone → Change package visibility → Public。一次性，30 秒。
>
> 国内网络若觉得 ghcr.io 慢，可以同步推到阿里云 ACR，改 `image:` 即可。详见 [docs/05-国内网络优化.md](./docs/05-国内网络优化.md)。

### C. 本地开发模式

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

实训文档位于 [`docs/`](./docs)：

- [01-环境准备](./docs/01-环境准备.md)
- [02-启动指南](./docs/02-启动指南.md)
- [03-业务流程](./docs/03-业务流程.md)
- [04-扩展练习](./docs/04-扩展练习.md)
- [05-国内网络优化](./docs/05-国内网络优化.md) 🇨🇳

## 目录结构

```
his/
├── backend/                Spring Boot 后端
├── frontend/               Vue 3 前端
├── docs/                   实训文档与扩展练习
├── docker-compose.yml      容器编排
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

## CI/CD

- **`.github/workflows/ci.yml`**：每次 `push` / `pull_request` 到 `main` 时，分别构建后端 jar 与前端 dist，运行单元测试，并把产物作为 14 天保留期的 Artifact 上传到 Actions 页。
- **`.github/workflows/release.yml`**：当推送 `vX.Y.Z` 标签时（如 `git tag v0.1.0 && git push --tags`），CI 会：
  1. 构建后端 jar、前端 dist，附到 GitHub Release
  2. 构建后端/前端 Docker 镜像并推送到 `ghcr.io/hmc-edu/his-backend`、`ghcr.io/hmc-edu/his-frontend`

## 实训扩展方向

主线之外，预留扩展点：门诊收费、药房发药、检查/检验申请、报表统计、完整 RBAC 权限模型、医生排班与号源、单据打印、AOP 审计日志等。详见 [`docs/04-扩展练习.md`](./docs/04-扩展练习.md)。

---

© 杭州医学院 · Hangzhou Medical College
