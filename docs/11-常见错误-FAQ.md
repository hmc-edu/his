# 11 · 常见错误 FAQ

> 杭州医学院 门诊 HIS 系统 · 实训文档

按 **错误信息关键字** 查找。如果你的错误不在这里，**先把完整堆栈贴搜索引擎**——程序员日常 80% 是搜错。

---

## 一、环境与安装

### `java: command not found`

JDK 没装好或 PATH 没生效。

- Windows：装完后**重开终端**才会读到新 PATH。
- Mac/Linux：检查 `~/.bashrc` 或 `~/.zshrc` 是否加了 `export PATH=$JAVA_HOME/bin:$PATH`。

### `node: command not found`

Node.js 没装好。重装并重开终端。

### `pnpm: command not found`

```bash
npm install -g pnpm
```

如果 npm 报权限错（Linux/Mac）：

```bash
sudo npm install -g pnpm
```

### Windows PowerShell 报 "因为在此系统上禁止运行脚本"

执行：

```powershell
Set-ExecutionPolicy -Scope CurrentUser RemoteSigned
```

### Mac `brew` 命令很慢

切到清华源：

```bash
git -C "$(brew --repo)" remote set-url origin https://mirrors.tuna.tsinghua.edu.cn/git/homebrew/brew.git
brew update
```

---

## 二、启动后端

### `Web server failed to start. Port 8080 was already in use`

- 找出占用进程并杀掉：

```bash
# Linux/Mac
lsof -i :8080
kill -9 <PID>

# Windows
netstat -ano | findstr 8080
taskkill /PID <PID> /F
```

- 或改 `application.yaml` 里的 `server.port` 为别的端口（比如 18080），同时改前端 `vite.config.js` 的 proxy.target。

### `Communications link failure` / `Could not connect to MySQL`

- MySQL 服务没启动。
  - Windows：任务管理器 → 服务 → 找 `MySQL80` → 启动
  - Mac：`brew services start mysql@8.0`
  - Linux：`sudo systemctl start mysql`
- 端口被防火墙挡住。MySQL 默认 3306。
- 不在本机：把 `application-dev.yaml` 里的 `localhost` 换成 IP。

### `Access denied for user 'root'@'localhost'`

MySQL 密码不对。两种解法二选一：

```sql
-- 方案 A：把 root 密码改成 root
ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';
FLUSH PRIVILEGES;
```

```yaml
# 方案 B：改项目配置匹配你的密码
# backend/src/main/resources/application-dev.yaml
spring:
  datasource:
    password: 你的真实密码
```

### `Unknown database 'his'`

没建库：

```sql
CREATE DATABASE his DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### `Table 'his.sys_user' doesn't exist`

没跑 schema.sql：

```bash
mysql -uroot -proot his < backend/src/main/resources/db/schema.sql
mysql -uroot -proot his < backend/src/main/resources/db/data.sql
```

### `Public Key Retrieval is not allowed`

JDBC URL 缺少 `allowPublicKeyRetrieval=true`，参考项目里 `application-dev.yaml`，里面已经带上了。

### `The server time zone value '...' is unrecognized`

JDBC URL 缺 `serverTimezone=GMT%2B8`。项目里已配，**自定义 URL 时别忘了带**。

### `Could not GET 'https://services.gradle.org/...'`

Gradle wrapper 还在用官方源。本项目已改成腾讯云镜像（`backend/gradle/wrapper/gradle-wrapper.properties`），如果你上次拉代码很久了，先 `git pull` 一下。

### `Invalid bound statement (not found): xxx`

MyBatis 找不到 XML 里的 SQL。检查：

1. XML 文件路径是否在 `src/main/resources/mappers/` 下
2. `<mapper namespace="com.hmc.his.repository.XxxRepository">` 的 namespace 必须等于 Repository 接口的全限定名
3. `<select id="...">` 的 id 必须等于 Repository 接口的方法名

### Lombok 注解不生效，IDE 全是红线

- 装 Lombok 插件：`Settings → Plugins → 搜索 Lombok`
- 开启注解处理：`Settings → Build → Compiler → Annotation Processors → Enable`

### IDEA 找不到 main 方法的运行图标

- 等 Gradle 索引完（右下角进度条跑完）
- `File → Invalidate Caches → Invalidate and Restart`

---

## 三、启动前端

### `vite: command not found`

```bash
cd frontend
pnpm install      # 重装一遍
```

### `pnpm install` 卡在 `Progress: resolved 0`

npm 源走默认（npmjs.org）了。检查 `frontend/.npmrc` 是否存在并包含：

```
registry=https://registry.npmmirror.com/
```

如果还是慢，强制：

```bash
pnpm install --registry=https://registry.npmmirror.com
```

### `EADDRINUSE: address already in use :::5173`

5173 被占用。改 `frontend/vite.config.js` 里的 `server.port`。

### 浏览器打开 5173，页面空白 + Console 报 `Failed to fetch dynamically imported module`

通常是热更新失败，按 Ctrl+Shift+R 强制刷新。如果还不行：

```bash
# 清缓存重启
rm -rf node_modules/.vite
pnpm dev
```

### `Cannot find module '@/...'`

`@` 别名没识别。检查 `vite.config.js` 是否有：

```js
resolve: {
  alias: { '@': fileURLToPath(new URL('./src', import.meta.url)) }
}
```

### `[plugin:vite:vue] ...`

Vue 文件语法错误。看具体行号修复。

### Element Plus 组件不显示

`main.js` 里是否：

```js
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'   // ← CSS 必须导入
app.use(ElementPlus)
```

---

## 四、运行时 / 业务

### 登录后立刻跳回登录页

- token 没存进 localStorage：F12 → Application → Local Storage → 看有没有 `his_auth`
- token 没带到请求头：F12 → Network → 看 Authorization 字段是否有值
- 后端把 token 否了：看后端日志有没有 `NotLoginException`

### 接口 200 但前端没拿到数据

`api/request.js` 的响应拦截器只在 `code==0` 时返回 `data`。如果你在调用方写：

```js
const res = await listPatients()
console.log(res)        // 这里就是 data，不需要 res.data
```

如果你的后端没用 `R<T>` 包，或者直接返回了 `data`，前端拿到的会是 `undefined`。请遵守项目约定。

### `pull access denied` for ghcr.io

GHCR 镜像还是私有。两个方案：

1. 让管理员把包改 Public：见 [02-启动指南 §B](./02-启动指南.md#b-直接拉取镜像最快不编译) 的提示
2. `docker login ghcr.io -u <github用户名>`，密码用 PAT

### Docker compose 起来后立刻 exit

看日志：`docker compose logs backend`。常见：

- MySQL 还没 ready，后端连失败 → 等几秒重试或加 `restart: unless-stopped`
- 端口冲突 → 改 `docker-compose.yml` 里的端口映射

### 容器里能连数据库，本机不能

容器网络是独立的。`docker-compose.yml` 里 `backend` 通过服务名 `mysql` 连接（`jdbc:mysql://mysql:3306/his`），本机连接是 `localhost:3306`。两套环境配置分开。

### 改了代码没生效

- **本地开发模式**：后端要重启（IDEA 红色方块停止 → 绿色三角再起）；前端 vite 通常自动热更
- **Docker 模式**：要 rebuild，`docker compose up --build`

### 已经 down -v 了，DataInitializer 不写默认账号

- 检查后端日志，应该有一行 `初始化默认账号：admin / reception / doctor`
- 没有的话，可能 sys_user 表已经有数据。`SELECT * FROM sys_user;` 看一下，必要时清空：

```sql
TRUNCATE TABLE sys_user;
TRUNCATE TABLE doctor;   -- 也清掉，让 DataInitializer 重跑
```

然后**重启**后端。

---

## 五、Git / GitHub

### `Permission denied (publickey)`

SSH 密钥没配。见 [01-环境准备 §4](./01-环境准备.md#4-git)。

### `error: failed to push some refs`

远程比本地新，先拉：

```bash
git pull --rebase origin main
git push
```

### push 时一直让输密码

切到 SSH 协议（推荐）：

```bash
git remote set-url origin git@github.com:hmc-edu/his.git
```

或用 PAT：GitHub → Settings → Developer settings → Personal access tokens → 生成 → 当密码用。

### Merge conflict 没解决直接 commit

Git 会拒绝。打开有冲突的文件，找 `<<<<<<<` `=======` `>>>>>>>` 标记，手工保留正确内容并删标记，然后：

```bash
git add <文件>
git commit
```

### 误删文件想恢复

```bash
git checkout HEAD -- path/to/file       # 从最近 commit 恢复
git log --all -- path/to/file           # 找文件曾在哪个 commit 里
git checkout <commit-hash> -- path/to/file
```

### 误提交了大文件 / 密钥

立即：

```bash
# 把文件从历史中清掉（重写历史，慎用）
git filter-repo --invert-paths --path 'path/to/secret'
# 然后强制推送
git push --force
```

并**立刻把泄露的密钥废弃换新**。

---

## 六、Docker / 网络

### 拉镜像超慢

- 配 Docker Hub 镜像加速：见 [05-国内网络优化 §1](./05-国内网络优化.md#1-docker-hub-加速影响所有-docker-pull)
- 校园网/家庭网都试过还是慢：换个网络（手机热点 / 流量）

### `manifest unknown` / `pull access denied`

- ghcr.io 包是私有：让管理员设 Public
- 或登录：`docker login ghcr.io`

### `denied: 这镜像不在白名单`

DaoCloud `m.daocloud.io` 限定白名单，自定义仓库要申请。详见 [05-国内网络优化 §2](./05-国内网络优化.md#2-ghcr-镜像访问仅-拉预编译镜像-场景需要)。

### Docker Desktop 启动失败 (Windows)

- 确认 Hyper-V / WSL2 已开启
- 确认 BIOS 里虚拟化 (VT-x / AMD-V) 开启
- Win 家庭版需要先装 WSL2

### `error getting credentials - err: exit status 1, out: ``

Docker 配置里指定了凭证 helper（`docker-credential-pass` / `docker-credential-secretservice` / `docker-credential-desktop`），但程序没装。

修复：清掉凭证 helper 配置。

```bash
# 备份当前配置
cp ~/.docker/config.json ~/.docker/config.json.bak 2>/dev/null

# 重置成空配置（无 helper）
mkdir -p ~/.docker
echo '{}' > ~/.docker/config.json

# 重试
docker compose down -v
docker compose up --build
```

如果不希望全清，只删 `credsStore` 字段也可以：

```bash
# 用任意编辑器把 ~/.docker/config.json 里的 "credsStore": "..." 那行删掉
nano ~/.docker/config.json
```

### docker.io 拉镜像超慢或失败

国内学生用项目自带的 daocloud 镜像版本：

```bash
docker compose -f docker-compose.cn-build.yml up --build
```

这个 compose 让所有 base image 走 `m.daocloud.io/docker.io/library/...`，绕开 Docker Hub 限速。

---

## 七、SQL 与数据库

### 中文显示为 `?` / `???` / 乱码（高频问题）

中文乱码可能在四个环节出错，逐一排查：

#### 1. 先定位是"存进去时坏了"还是"取出来时坏了"

直接进 MySQL 看原始数据：

```bash
mysql -uroot -proot --default-character-set=utf8mb4 his
```

```sql
SELECT id, name, gender, address FROM patient LIMIT 5;
```

- 如果这里就是乱码 → **数据本身写坏了**（数据库 / data.sql / JDBC 连接编码问题）
- 如果这里是正确的中文 → **后端→前端这一段编码问题**（罕见，Spring Boot 默认 UTF-8）

#### 2. 检查数据库与表的字符集

```sql
SELECT @@character_set_database, @@collation_database;
-- 期望：utf8mb4 / utf8mb4_unicode_ci

SHOW CREATE TABLE patient \G
-- 期望：DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
```

如果数据库不是 utf8mb4：

```sql
ALTER DATABASE his CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

如果某张表不是：

```sql
ALTER TABLE patient CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 3. 检查连接编码（可能是真正的元凶）

```sql
SHOW VARIABLES LIKE 'character_set%';
```

期望输出全都是 `utf8mb4`：

```
character_set_client      | utf8mb4
character_set_connection  | utf8mb4
character_set_database    | utf8mb4
character_set_results     | utf8mb4
character_set_server      | utf8mb4
```

如果 `character_set_client` 或 `character_set_results` 不是 utf8mb4，**当前会话写入的中文都会被错误编码**。强制设置：

```sql
SET NAMES utf8mb4;
```

> 项目内 `data.sql` 和 `schema.sql` 顶部都已经加了 `SET NAMES utf8mb4;`，确保导入时不丢字。

#### 4. 检查 MySQL 服务器默认配置（一劳永逸）

打开 MySQL 配置文件（位置因系统而异）：
- **Windows**：`C:\ProgramData\MySQL\MySQL Server 8.0\my.ini`
- **macOS Homebrew**：`/usr/local/etc/my.cnf` 或 `/opt/homebrew/etc/my.cnf`
- **Linux**：`/etc/mysql/my.cnf` 或 `/etc/my.cnf`

确认有以下段：

```ini
[mysqld]
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci

[client]
default-character-set=utf8mb4

[mysql]
default-character-set=utf8mb4
```

改完**重启 MySQL 服务**。

#### 5. 已经"中招"的数据怎么办

如果 `patient` 表里已经存了乱码数据，最干净的办法是重建：

```bash
# 1. 拉最新代码（已修好 data.sql 和 JDBC URL）
git pull origin main

# 2. 重建数据库
mysql -uroot -proot -e "DROP DATABASE IF EXISTS his; CREATE DATABASE his DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 3. 强制用 utf8mb4 重新导入
mysql -uroot -proot --default-character-set=utf8mb4 his < backend/src/main/resources/db/schema.sql
mysql -uroot -proot --default-character-set=utf8mb4 his < backend/src/main/resources/db/data.sql

# 4. 重启后端
# IDEA 里点停止 → 启动；DataInitializer 会重新创建用户与医生记录
```

Docker 模式：

```bash
git pull origin main
docker compose down -v          # ⚠️ 删 volume，清掉旧的乱码数据
docker compose up --build
```

#### 6. 验证修复

启动完后：

```bash
mysql -uroot -proot --default-character-set=utf8mb4 his \
  -e "SELECT name, gender, address FROM patient LIMIT 3"
```

应输出：

```
+--------+--------+--------------------------------+
| name   | gender | address                        |
+--------+--------+--------------------------------+
| 张三   | 男     | 北京市东城区东长安街1号        |
| 李四   | 女     | 北京市西城区西长安街2号        |
| 王小明 | 男     | 北京市海淀区中关村大街3号      |
+--------+--------+--------------------------------+
```

前端登录后看患者列表，名字与地址都应正确显示。

#### 7. 常见情境对照

| 现象 | 通常原因 |
| --- | --- |
| 所有中文都是 `???` | JDBC URL 没带 `characterEncoding=UTF-8` |
| 中文显示成 `ä¸­æ–‡` 类奇怪字符 | 写入时是 utf8、读取时按 latin1 解码 |
| 字符串里中文 OK，但 emoji 显示成 `??` | JDBC 用了 `characterEncoding=utf8`（实为 utf8mb3，3 字节）。改为 `UTF-8` |
| Windows cmd 里 `mysql>` 选数据看着是乱码，但前端正常 | 是 cmd 终端编码问题，不是数据问题 |
| 前端建的患者乱码、`data.sql` 导入的正常 | JDBC URL 编码不对 |
| `data.sql` 导入的乱码、前端建的正常 | 客户端导入时没用 `--default-character-set=utf8mb4` |

### `Duplicate entry '...' for key`

唯一索引冲突（如 `patient.id_card` 重复）。检查数据。

### `Cannot delete or update a parent row: a foreign key constraint fails`

外键约束（本项目未用，但学生扩展时可能加）。先删子表数据再删父表，或加 `ON DELETE CASCADE`。

### 改了 schema.sql 但 docker-compose 不生效

`schema.sql` 只在第一次创建 volume 时执行。要重新初始化：

```bash
docker compose down -v       # 注意 -v 会删 volume
docker compose up --build
```

---

## 八、性能与优化（高级）

### 列表页慢

- 看 SQL：是否全表扫（`EXPLAIN SELECT ...`）
- 加合适的索引（参见 [09-数据库设计 §6](./09-数据库设计.md#6-索引设计)）

### 后端启动 30s+

正常情况下应该 5–10s。慢的常见原因：

- Gradle 还在下依赖（首次启动正常）
- 内存不足，JVM 触发 GC：调高 `-Xmx`
- MySQL 连接池配置太大初始化慢：`spring.datasource.hikari.maximum-pool-size=5`

### 前端 build 后体积大

```bash
pnpm build
ls -lh dist/assets/        # 看哪些 chunk 大
```

vite 默认会 code-split，主包不应超过 500KB。如果某个第三方库特别大：考虑按需引入。

---

## 九、还是没解决怎么办

按照下面步骤问问题，能大幅提升被回答的概率：

1. **完整错误堆栈**——复制粘贴，不要截图
2. **复现步骤**——你做了 1234 步发生了什么
3. **环境信息**——OS / JDK / Node / MySQL 版本
4. **已经试过什么**——避免别人重复给你已经试过的方案

把这四件事写清楚后：

- 校内：贴课程群、找助教
- 校外：StackOverflow、GitHub Issues、思否、掘金
- AI：贴给 ChatGPT / 通义千问 / 文心一言，**带上完整错误**

---

## 下一步

- 编码规范：[07-后端开发指南](./07-后端开发指南.md) / [08-前端开发指南](./08-前端开发指南.md)
- 实战：[13-实战-添加检查申请模块](./13-实战-添加检查申请模块.md)
