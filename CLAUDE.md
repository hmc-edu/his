# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目简介

杭州医学院门诊 HIS 系统 — 信息系统类课程实训项目。覆盖门诊业务主线：登录 → 挂号 → 就诊（电子病历 + 处方）→ 收费 → 发药。

## 技术栈

- 后端：Spring Boot 3.1.9 + MyBatis 3.0 + MySQL 8 + Sa-Token + SpringDoc OpenAPI
- 前端：Vue 3 + Vite + Element Plus + Pinia + Vue Router + Axios
- 运行环境：JDK 17、Node 18+、MySQL 8 (Windows 宿主机)

## 常用命令

```bash
# === 启动 MySQL (Windows 宿主机，非 Docker) ===
net start mysql80

# === 后端 ===
cd backend && ./gradlew bootRun                        # 启动后端，端口 8080
./gradlew clean bootJar                                # 构建 jar
./gradlew test                                         # 运行全部单元测试
./gradlew test --tests "com.hmc.his.controller.AuthControllerTest"  # 运行单个测试类

# === 前端 ===
cd frontend && pnpm install && pnpm dev                # 开发服务器 (http://localhost:5173)
pnpm build                                             # 生产构建到 dist/

# === Docker Compose (仅当需要用容器时) ===
docker compose up --build                              # 一键拉起完整系统
docker compose down -v && docker compose up --build    # 重置数据库后启动
```

MySQL 连接信息（本地开发）：`root/root@localhost:3306/his`，数据库初始化脚本在 `backend/src/main/resources/db/schema.sql` 和 `data.sql`。

## 后端分层架构

```
Controller (@RestController) → Service (interface) → ServiceImpl (@Service) → Repository (@Mapper) → Model
```

- **Controller** — HTTP 接口层。接收/返回 JSON，`@Valid` 参数校验，`@SaCheckRole` 角色校验，调用 Service 后包成 `R<T>` 统一响应。
- **Service** — 业务逻辑层。状态机、金额计算、跨 Repository 协作。Service 接口与实现分离（方便 Mock 测试）。
- **Repository** — MyBatis Mapper 接口。纯 SQL，无业务逻辑。简单 SQL 用 `@Select/@Insert/@Update` 注解，复杂动态 SQL 写在 `resources/mappers/*.xml`。
- **Model** — 数据库实体类（PO），字段对应表列，驼峰与下划线由 MyBatis `map-underscore-to-camel-case` 自动转换。
- **DTO** — `dto/` 包下，前后端接口传输对象，与 Model 分离（如 `LoginReq` 只含 username/password，不含 created_at 等）。

## 统一响应格式

所有接口返回 `R<T>`：`{ "code": 0, "msg": "ok", "data": {...} }`。业务异常时 code 为非 0。
- 成功：`R.ok(data)`
- 失败：`R.fail(code, message)` 或直接 `throw new BusinessException("msg")`，由 `GlobalExceptionHandler` 转换为 R 响应。
- 分页：`PageRes<T>` 包含 `total` 和 `records`。

前端 `api/request.js` 拦截响应：`code===0` 时只把 `data` 返回给调用方；`code===401` 自动跳登录；其他异常弹 toast。

## 鉴权 (Sa-Token)

- `SaTokenConfig` 注册拦截器作用于 `/api/**`，`/api/auth/login` 和 Swagger 路径白名单。
- 登录时 `AuthService` 调用 `StpUtil.login(userId)` 生成 token。
- 前端请求拦截器自动注入 `Authorization` 头；响应 401 时清空 localStorage 并跳 `/login`。
- 角色由 `StpInterfaceImpl` 从 `sys_user.role` 单字段返回，用于 `@SaCheckRole`。
- **当前系统有四个角色**：ADMIN、RECEPTION、DOCTOR、PHARMACIST。DataInitializer 在 sys_user 为空时自动插入默认账号。

## 关键设计约束

- **处方金额服务端计算** — `VisitServiceImpl#prescribe` 取 `drug.price × qty` 算金额，前端不传金额字段。
- **状态机收口在 Service 层** — `registration.status` 的所有切换只在 service 层完成，前端不传 status。状态流转：WAITING → VISITING → DONE / CANCELLED。
- **账单随处方自动生成** — `BillService.createFromPrescription` 在处方创建后同步生成 bill + bill_item。

## 数据库

- 字符集：utf8mb4，排序规则：utf8mb4_unicode_ci
- `schema.sql` 和 `data.sql` 在 `backend/src/main/resources/db/`，Docker 首次启动自动执行。
- 核心表（按业务流程）：`sys_user` → `patient` → `registration`(挂号) → `visit`(就诊) → `prescription`(处方) → `prescription_item` → `bill`(账单) → `bill_item`
- 辅助表：`department`、`doctor`、`drug`

## 测试

- JUnit 5 + Mockito + MockMvc (`standaloneSetup`，不启动完整 Spring 上下文)。
- `HisApplicationTests` 是占位测试，不依赖数据库。
- 单元测试命名：`*Test.java`，对应 main 中的类。

## 前端结构

```
src/
├── api/           # axios 接口封装（auth.js, patient.js, registration.js, visit.js, bill.js 等）
├── router/        # 路由定义 + 登录守卫 + meta.roles 角色过滤
├── stores/        # Pinia 状态（user.js 管理 token 和用户信息，持久化到 localStorage）
├── layouts/       # DefaultLayout.vue — 左栏菜单 + 顶栏 + 内容区
├── views/         # 页面组件，按模块分目录
│   ├── login/     # 登录
│   ├── dashboard/ # 工作台
│   ├── reception/ # 挂号台（患者管理、挂号、挂号查询、收费台）
│   ├── doctor/    # 医生工作站（待诊队列、就诊）
│   ├── pharmacy/  # 药房（发药）
│   ├── base/      # 基础信息（科室、医生、药品）
│   └── system/    # 系统管理（用户）
└── main.js        # 入口：注册 Pinia / Router / Element Plus (中文) / 全部图标
```

Vite 开发服务器配置了 `/api` 代理到 `http://localhost:8080`。路由使用 hash 模式 (`createWebHashHistory`)。
