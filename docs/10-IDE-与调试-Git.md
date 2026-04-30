# 10 · IDE / 调试 / Git 协作

> 杭州医学院 门诊 HIS 系统 · 实训文档

---

## 第一部分：IntelliJ IDEA（后端）

### 1.1 打开项目

`File → Open` → 选择 `his/backend` 目录（注意：选 `backend`，不是项目根目录）。

IDEA 会识别 `build.gradle` 并自动下载依赖。第一次需要 3–5 分钟，期间右下角有进度条。等到 "External libraries" 全部加载完才能正常工作。

### 1.2 必装插件

`File → Settings → Plugins`，搜索并安装：

| 插件 | 用途 | 必装？ |
| --- | --- | --- |
| **Lombok** | 让 `@Data` `@Slf4j` 等注解生效 | ✅ 必装 |
| **MyBatis Log** | 把 SQL 日志里的 `?` 自动填充成实际参数 | 强烈推荐 |
| **GitToolBox** | Git 状态增强 | 推荐 |
| **Translation** | 翻译注释 | 可选 |
| **Chinese (Simplified) Language Pack** | 中文界面 | 可选 |

> 安装 Lombok 后，**确认开启注解处理器**：
> `Settings → Build, Execution, Deployment → Compiler → Annotation Processors` → 勾 "Enable annotation processing"。
> 否则 `@Data` 不会生成 getter/setter，全局红线。

### 1.3 配置 JDK

`File → Project Structure → Project`：

- Project SDK：选 `temurin-17` 或任何 17.x
- Project language level：`17`

如果下拉框里没有 JDK 17，点 "Add SDK → Download JDK" 让 IDEA 自动下载。

### 1.4 启动后端的几种方式

#### 方式 1：右键 main 方法 → Run

打开 `HisApplication.java`，左边 main 方法旁有绿色三角，点击 → `Run 'HisApplication'`。

这是最常用的方式，比命令行快。

#### 方式 2：Gradle 面板

右侧栏 `Gradle` 图标 → `his` → `Tasks → application → bootRun` → 双击。

#### 方式 3：命令行

```bash
./gradlew bootRun
```

> **推荐的实训组合**：Docker 起 MySQL + IDEA 启后端 + 终端 `pnpm dev` 起前端。这样既能本机断点调试后端，又不用装 MySQL。具体流程见 [02-启动指南 C 节](./02-启动指南.md#c-混合模式推荐做扩展练习的同学用-)。

### 1.5 断点调试（必学）

1. 在你想暂停的代码行号旁边**点一下**——出现红色圆点就是断点
2. 右上角的"启动"按钮换成虫子图标 → 点击 (Debug 模式)
3. 当请求触发到这一行时，程序自动暂停，IDEA 跳到 Debug 面板：

| 面板 | 作用 |
| --- | --- |
| **Variables** | 看当前作用域内所有变量的值 |
| **Watches** | 加表达式（如 `r.getStatus()`），实时求值 |
| **Frames** | 调用栈：从哪里调到这里来的 |
| **Console** | 程序输出 |

调试快捷键：

| 按键 | 作用 |
| --- | --- |
| F8 | Step Over：跳到下一行 |
| F7 | Step Into：进入方法 |
| Shift+F8 | Step Out：跳出当前方法 |
| F9 | Resume：继续跑到下一个断点 |
| Ctrl+F8 | 切换断点 |
| Alt+F8 | Evaluate Expression：临时算个表达式 |

#### 实战：找一个挂号失败的 bug

1. 在 `RegistrationServiceImpl#create` 第一行打断点
2. Debug 模式启动后端
3. 前端发起挂号请求
4. 程序停在断点处，逐行 F8，看 `r.getRegFee()` 是否为 null、`registrationRepository.insert(r)` 是否抛异常
5. 找到根因后停止 Debug，修代码，再来一遍

### 1.6 看实时 SQL

`application-dev.yaml` 已配置：

```yaml
logging:
  level:
    com.hmc.his.repository: debug
```

控制台会打印所有 SQL：

```
==>  Preparing: SELECT * FROM patient WHERE id = ?
==> Parameters: 1(Long)
<==      Total: 1
```

装了 **MyBatis Log** 插件后，会多打一行可执行的 SQL（参数已填充）：

```
SELECT * FROM patient WHERE id = 1
```

直接复制到 MySQL 客户端就能跑，调试 SQL 神器。

### 1.7 IDEA 常用快捷键

| 按键 | 作用 |
| --- | --- |
| Ctrl+N | 按类名搜索 |
| Ctrl+Shift+N | 按文件名搜索 |
| Ctrl+Shift+F | 全文搜索 |
| Ctrl+B / Ctrl+左键 | 跳到定义 |
| Alt+F7 | 查找用法 |
| Ctrl+Alt+L | 格式化代码 |
| Ctrl+Alt+O | 优化 imports |
| Shift+F6 | 重命名 |
| Ctrl+/ | 注释行 |
| Ctrl+D | 复制行 |
| Ctrl+Y | 删除行 |
| Alt+Enter | 智能修复（导入、生成构造器…） |

---

## 第二部分：VS Code（前端）

### 2.1 打开项目

`File → Open Folder` → 选 `his/frontend`。

按 `` Ctrl+` ``（反引号）打开终端，运行：

```bash
pnpm install   # 第一次，约 2 分钟
pnpm dev
```

### 2.2 必装插件

| 插件 | 用途 |
| --- | --- |
| **Vue (Official)** | Vue 3 语法高亮 / 类型 / 跳转 |
| **ESLint** | 代码风格检查 |
| **Path Intellisense** | 路径补全 |
| **GitLens** | Git 增强 |
| **Chinese (Simplified)** | 中文界面 |

### 2.3 调试前端

#### 方法 1：浏览器 DevTools（推荐）

按 F12，三个面板必看：

- **Console**：报错堆栈、`console.log`
- **Network**：每个 HTTP 请求的 URL / 状态 / 请求头 / 响应体
- **Sources**：在源码里打断点（左边行号点一下）

#### 方法 2：VS Code 内嵌调试

`.vscode/launch.json`：

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "chrome",
      "request": "launch",
      "name": "Vue: chrome",
      "url": "http://localhost:5173",
      "webRoot": "${workspaceFolder}/src",
      "breakOnLoad": true
    }
  ]
}
```

按 F5 启动，可以在 VS Code 里直接打断点。

### 2.4 Vue Devtools

Chrome 商店搜 "Vue.js devtools" 装上。F12 多一个 Vue 面板：

- **Components**：组件树 + props/state 实时查看
- **Pinia**：所有 store 的实时数据
- **Routes**：当前路由 + 所有路由列表

改 store 数据可以直接在 Pinia 面板里点字段编辑——**强烈推荐**用来调试登录态、菜单权限等。

---

## 第三部分：Git 协作流程

> 本节给从来没用过 git 的同学，已经会的可跳过。

### 3.1 一次完整的协作流程

假设你是杭医学生，要给项目加一个"门诊收费"功能：

```bash
# 1. 拉最新代码（每次开始干活前都要做）
cd his
git checkout main
git pull origin main

# 2. 建一个新分支（不要直接在 main 上改）
git checkout -b feature/billing

# 3. 写代码... 改文件...

# 4. 看一下改了哪些
git status        # 列出改动文件
git diff          # 看具体改了什么内容

# 5. 暂存
git add backend/src/main/java/com/hmc/his/controller/BillController.java
git add backend/src/main/java/com/hmc/his/service/BillService.java
# 或者：git add . （添加全部，但容易误加敏感文件，慎用）

# 6. 提交
git commit -m "feat(bill): 增加门诊收费基础接口"

# 7. 推到远程（首次需要 -u 设置上游）
git push -u origin feature/billing

# 8. 浏览器打开 GitHub 仓库 → "Compare & pull request" → 填描述 → 创建 PR
# 9. 老师/同学 review → 提意见 → 你继续在本地改 → 重新 add/commit/push
# 10. 通过后合并到 main

# 11. 合并后清理本地分支
git checkout main
git pull
git branch -d feature/billing
```

### 3.2 commit 消息写法（Conventional Commits）

格式：`<类型>(<范围>): <一句话描述>`

| 类型 | 含义 |
| --- | --- |
| `feat` | 新功能 |
| `fix` | bug 修复 |
| `refactor` | 重构（不改变功能） |
| `docs` | 仅改文档 |
| `test` | 加/改测试 |
| `chore` | 构建/工具相关 |
| `style` | 格式化（不改代码逻辑） |

例：

```
feat(bill): 增加门诊收费接口
fix(visit): 修正处方金额计算遗漏小数点
docs(readme): 更新启动说明
```

### 3.3 分支命名

| 类型 | 命名 | 例子 |
| --- | --- | --- |
| 功能 | `feature/<名字>` | `feature/dispense` |
| 修复 | `fix/<名字>` | `fix/login-401` |
| 文档 | `docs/<名字>` | `docs/api-guide` |
| 重构 | `refactor/<名字>` | `refactor/visit-state-machine` |

不要用 `dev`、`my-branch`、`123` 这种无意义名字。

### 3.4 常用 Git 命令速查

```bash
# 查看
git status                        # 当前状态
git log --oneline -20             # 最近 20 条 commit
git diff                          # 工作区 vs 暂存区
git diff --staged                 # 暂存区 vs HEAD
git diff main..HEAD               # 当前分支相对 main 改了什么

# 撤销
git restore <file>                # 撤销工作区改动（危险！没法恢复）
git restore --staged <file>       # 把文件从暂存区拿回工作区
git reset HEAD~1                  # 撤销最近一次 commit（保留改动）

# 分支
git branch                        # 列出本地分支
git checkout -b <name>            # 新建并切换分支
git checkout <name>               # 切换分支
git branch -d <name>              # 删除分支（已合并）
git branch -D <name>              # 强制删除（未合并，慎用）

# 同步远程
git fetch                         # 拉远程信息但不合并
git pull                          # = fetch + merge
git push                          # 推到远程

# 合并冲突
git pull origin main              # 拉 main 时如果冲突
# 编辑器里会有 <<<<<<<  =======  >>>>>>> 标记，手工解决
git add <冲突文件>
git commit                        # 完成合并
```

### 3.5 处理冲突（Merge Conflict）

冲突长这样：

```java
public class BillController {
<<<<<<< HEAD
    private final BillService billService;
=======
    private final BillService service;       // ← 别人改的
>>>>>>> main
```

操作：

1. **手动**保留你想要的版本，删除 `<<<<<<<`、`=======`、`>>>>>>>` 标记
2. `git add <文件>`
3. `git commit`（git 会自动给 merge commit 写好消息）

### 3.6 不要做的事

- ❌ `git push --force` 到 main（会覆盖别人的提交）
- ❌ `git commit -am "fix"` （commit 消息无意义）
- ❌ 把 `node_modules/`、`build/`、`.idea/` 提交进去（已经在 `.gitignore` 里了）
- ❌ 把数据库密码、API key 写进代码再提交
- ❌ 长时间不 pull，等到要 push 时一堆冲突
- ❌ 在 `main` 分支上直接改

### 3.7 PR 描述模板

提 PR 时按这个模板写描述（README 里也提到过）：

```markdown
## 改动内容

- 新增 ... 表
- ...

## 测试方式

- [ ] 单元测试通过：`./gradlew test`
- [ ] 手工冒烟：登录 admin → 访问 /xxx → 操作 yyy → 期望结果 zzz
- [ ] 截图：

## 影响范围

- [ ] 是否改了 schema：是 / 否
- [ ] 是否改了 application.yaml：是 / 否
- [ ] 是否影响其他模块：...

## 关联 issue

Closes #N
```

---

## 第四部分：调试一个真实 bug 的完整过程

假设前端报"挂号失败：用户不存在"，但你刚刚明明建过患者。完整定位过程：

### Step 1 — 看前端 Console / Network

F12 → Network 找最后那条 POST /api/registrations。

- 请求体里 `patientId` 是什么？复制下来
- 响应体里 `code/msg` 是什么？

### Step 2 — 后端日志

后端控制台看：

```
==>  Preparing: SELECT * FROM patient WHERE id = ?
==> Parameters: 99(Long)         ← 注意这里！
<==      Total: 0                ← 一行也没查到
```

**线索**：传过去的 `patientId=99`，数据库里没有 id=99 的患者。

### Step 3 — MySQL 验证

```sql
SELECT id, name FROM patient WHERE id = 99;
-- (空)
SELECT id, name FROM patient ORDER BY id DESC LIMIT 5;
-- 6 钱多多
-- 5 赵丽
-- ...
```

确认数据库里没有 99，最大才 6。

### Step 4 — 找前端为什么传了 99

回到 Register.vue，看 `form.patient.id` 是怎么来的。

发现：用户搜索不存在的患者名字时，`patientRecords.value` 是空数组，但你脚本里有一句：

```js
form.patient = patientRecords.value[0]    // 取第一个，但空数组下就是 undefined
// 然后又：
form.patient.id = parseInt(someInput)     // 哪里手动赋了 99？
```

——找到了 bug 根源。

### Step 5 — 修复 + 验证 + 提交

```bash
git diff frontend/src/views/reception/Register.vue
git add frontend/src/views/reception/Register.vue
git commit -m "fix(reception): 选患者时校验非空，避免传入无效 patientId"
git push
```

---

## 下一步

- 完整业务一遍：[03-业务流程](./03-业务流程.md)
- 实战做新模块：[13-实战-添加检查申请模块](./13-实战-添加检查申请模块.md)
- 不会的术语：[12-术语表](./12-术语表.md)
