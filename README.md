<p align="center">
  <img src="doc/ylmao/logo.png" alt="YLmao" width="180">
</p>

<p align="center">
  <strong>简体中文</strong> · <a href="README.en.md">English</a>
</p>

<h1 align="center">YLmao-Admin-Layui 后台脚手架</h1>

<p align="center">
  基于 Spring Boot 4 的同域管理后台起步工程<br>
  Thymeleaf + Layui / Pear Admin 页面，Sa-Token 权限，MyBatis-Plus 数据访问
</p>

<p align="center">
  <code>com.ylmao</code> · <code>admin-layui</code> · Java 25 · 可 fork 后按业务改包名与品牌
</p>

---

## 简介

**YLmao-Admin-Layui** 提供登录鉴权、菜单权限、用户/角色/部门/岗位、字典、通知公告、操作日志、定时任务、文件管理、代码生成（ZIP）、开发环境接口文档（springdoc）等常见后台能力，适合作为 **CMS / 业务系统管理端** 的基础仓库继续扩展。

- 后端默认包名：`com.ylmao.admin`，启动类 `AdminApp`
- 页面与静态资源：`src/main/resources/templates`、`static`
- 数据库种子：`doc/admin-layui.sql`
- 开发约定：[`doc/接口文档与开发说明.md`](doc/接口文档与开发说明.md)

## 致谢

本项目的界面与交互建立在优秀开源前端之上，在此特别说明并感谢：

<table>
  <colgroup>
    <col style="width: 8.5em">
    <col>
  </colgroup>
  <thead>
    <tr><th>项目</th><th>说明</th></tr>
  </thead>
  <tbody>
    <tr>
      <td style="white-space: nowrap"><a href="https://layui.dev/"><strong>Layui</strong></a></td>
      <td>前端 UI 组件与文档规范；改页面时请优先对照 Layui 官方 API 与示例。</td>
    </tr>
    <tr>
      <td style="white-space: nowrap"><a href="https://gitee.com/pear-admin/Pear-Admin-Layui"><strong>Pear Admin</strong></a></td>
      <td>后台布局、Pear 组件与交互范式；本仓库在 <code>static/component/pear</code> 中集成相关资源，页面风格与 Layui 官方约定冲突时 <strong>以 Layui 为准</strong>。</td>
    </tr>
  </tbody>
</table>

后端为自研脚手架；Logo 与品牌标识见 `doc/ylmao/`。

## 主要功能

- 登录 / 退出、图形验证码、登录失败锁定与 IP 限流（`security.*` 可配置）
- RBAC：用户、角色、菜单权限、数据范围（部门树）
- 部门、岗位、字典类型与字典数据
- 通知公告、用户收件箱与头部消息
- 操作日志、在线用户、访问控制（黑白名单）
- 系统配置分组、本地上传与预览
- 内置定时任务扫描与执行日志
- 代码生成（表 → 后端 + 页面 + 权限 SQL）
- 开发环境 Swagger UI（生产默认关闭）

## 技术栈

| 类型 | 技术 |
| --- | --- |
| 后端 | Spring Boot **4.1.0**、Java **25** |
| 权限 | Sa-Token **1.45.0** + Redis 会话 |
| ORM | MyBatis-Plus **3.5.16**、MySQL 8 |
| 模板 | Thymeleaf（页面）、Freemarker（代码生成模板） |
| 接口文档 | springdoc-openapi **3.1.0**（dev 开 / prod 关） |
| 前端 | **Layui 2.13.9**、**Pear Admin**（同域静态资源） |
| 其它 | Hutool、captcha-core、EasyExcel、HikariCP |

## 环境要求

- JDK **25+**
- **MySQL 8.0+**
- **Redis 6.0+**（必需：登录态、验证码、配置/字典缓存、限流、分布式锁等）
- Maven 3.9+ 或项目自带 `mvnw` / `mvnw.cmd`

## 快速开始

### 1. 获取代码

```bash
git clone https://gitee.com/ylmao/admin-layui.git
cd admin-layui
```

### 2. 初始化数据库

创建数据库（名称自定，与配置一致即可），导入种子：

```bash
mysql -u <user> -p <database> < doc/admin-layui.sql
```

`sys_config` 中含系统名称、Logo（`/static/admin/images/ylmao/logo.png`）、`security.*` 等；缺项可能导致启动校验失败。

### 3. 修改连接配置

按本机环境编辑（**勿将真实密码提交到公开仓库**）：

| 文件 | 用途 |
| --- | --- |
| `src/main/resources/application-dev.yml` | 开发：MySQL、Redis、端口（默认 **8085**） |
| `src/main/resources/application-prod.yml` | 生产：MySQL、Redis、端口（默认 **8081**） |

业务时区由 `application.yml` 的 `app.timezone`（默认 `Asia/Shanghai`）统一；改后需重启。

### 4. 启动

默认 **dev** profile：

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

浏览器访问：**http://localhost:8085/login**（端口以 `application-dev.yml` 为准）。

种子中管理员账号以 `doc/admin-layui.sql` 内 `sys_user` 为准（部署后请尽快修改密码）。

## 打包与运行

```bash
.\mvnw.cmd clean package -DskipTests   # Windows
./mvnw clean package -DskipTests     # Linux / macOS
```

产物：

```text
target/admin-layui-0.0.1-SNAPSHOT.jar
```

```bash
java -jar target/admin-layui-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## 部署要点

- **多实例**：各节点必须连接 **同一 Redis**，在线用户、强退、验证码与限流才一致。
- **Nginx**：示例见 [`doc/nginx-admin.example.conf`](doc/nginx-admin.example.conf)；须转发 `X-Forwarded-For` 以便应用识别客户端 IP。
- **上线检查**：JDK 25、库表种子、`security.*` 七项、Redis/MySQL 连通、上传目录可写、`/login` 验证码与管理员登录冒烟。

更细的清单与 CORS、静态资源说明见下文「常见问题」与 [`doc/接口文档与开发说明.md`](doc/接口文档与开发说明.md)。

## 常用地址（dev 示例）

| 路径 | 说明 |
| --- | --- |
| `/login` | 登录页 |
| `/admin/index` | 登录后后台主页 |
| `/apidoc/listView` | 接口文档（需权限；内嵌 Swagger UI） |
| `/swagger-ui/index.html` | Swagger UI（需已登录） |
| `/captcha/captchaImage` | 验证码 |

## 目录结构

```text
.
├── doc/
│   ├── admin-layui.sql              # 数据库全量种子
│   ├── nginx-admin.example.conf     # Nginx 反代示例
│   ├── 接口文档与开发说明.md
│   └── ylmao/                       # Logo、favicon 源文件
├── src/main/java/com/ylmao/admin/   # 后端源码（AdminApp 入口）
├── src/main/resources/
│   ├── templates/                   # Thymeleaf 页面
│   ├── static/                      # Layui、Pear、业务静态资源
│   ├── mapper/                      # MyBatis XML
│   └── application*.yml
├── mvnw / mvnw.cmd
└── pom.xml
```

## 开发说明

- 模块样板：**用户 `user`**（主）、**角色 `role`**、**岗位 `post`**（含 OpenAPI 注解示例）。
- 前端：先查 [Layui 文档](https://layui.dev/)，再对照本项目 `templates/system/*.html`。
- 后端：Controller 用 DTO/VO；列表 `PageQuery` + `R.page`；URL 不在方法路径重复模块名。
- 在线 API：开发工具 → 接口文档；或阅读 [`doc/接口文档与开发说明.md`](doc/接口文档与开发说明.md)。

## 常见问题

**Redis 连接失败**  
检查 Redis 是否启动、`spring.data.redis` 配置、防火墙；多实例是否共用同一 Redis。

**数据库连接失败**  
检查库名、账号密码、MySQL 是否允许连接、当前激活的 profile。

**启动报「安全配置缺失 security.*」**  
执行或合并 `doc/admin-layui.sql` 中 `security` 分组配置，或对照文档补全七项。

**登录报 Sa-Token / Jackson 找不到 `com.md.admin.*`**  
Redis 中残留旧会话；开发环境可清空当前 database 后重新登录（包名迁移后常见）。

**未登录跳转登录**  
Sa-Token 会话过期或未带 `saToken` Cookie，重新登录即可。

**静态资源 404**  
确认 `spring.mvc.static-path-pattern: /static/**` 未改，且资源已打入 Jar。

---

<p align="center">
  <sub>YLmao-Admin-Layui · 感谢 Layui 与 Pear Admin 社区</sub>
</p>
