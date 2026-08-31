<p align="center">
  <img src="doc/ylmao/logo.png" alt="YLmao" width="180">
</p>

<p align="center">
  <a href="README.md">简体中文</a> · <strong>English</strong>
</p>

<h1 align="center">YLmao-Admin-Layui Admin Scaffold</h1>

<p align="center">
  Same-origin admin starter on Spring Boot 4<br>
  Thymeleaf + Layui / Pear Admin UI, Sa-Token auth, MyBatis-Plus data access
</p>

<p align="center">
  <code>com.ylmao</code> · <code>admin-layui</code> · Java 25 · Fork and rename packages/branding for your product
</p>

---

## Overview

**YLmao-Admin-Layui** ships common admin features—login, RBAC, org (dept/post), dict, notices, operation logs, scheduled jobs, file upload, code generation (ZIP), and springdoc API docs in dev—so you can extend it as a **CMS or business admin** baseline.

- Default package: `com.ylmao.admin`, entry class `AdminApp`
- Pages & static assets: `src/main/resources/templates`, `static`
- DB seed: `doc/admin-layui.sql`
- API & dev notes: [`doc/接口文档与开发说明.md`](doc/接口文档与开发说明.md) (Chinese)

## Acknowledgements

<table>
  <colgroup>
    <col style="width: 8.5em">
    <col>
  </colgroup>
  <thead>
    <tr><th>Project</th><th>Role</th></tr>
  </thead>
  <tbody>
    <tr>
      <td style="white-space: nowrap"><a href="https://layui.dev/"><strong>Layui</strong></a></td>
      <td>UI components and official docs—prefer Layui APIs when editing pages.</td>
    </tr>
    <tr>
      <td style="white-space: nowrap"><a href="https://gitee.com/pear-admin/Pear-Admin-Layui"><strong>Pear Admin</strong></a></td>
      <td>Layout and Pear widgets under <code>static/component/pear</code>; if Pear conflicts with Layui, <strong>follow Layui</strong>.</td>
    </tr>
  </tbody>
</table>

Backend scaffold is original; brand assets live in `doc/ylmao/`.

## Features

- Login / logout, captcha, account lock & IP rate limits (`security.*`)
- RBAC: users, roles, menu permissions, dept tree
- Dept, post, dict type & dict data
- Notices, user inbox, header messages
- Operation logs, online users, access control (allow/deny lists)
- Grouped system config, local upload & preview
- Built-in job scheduler & job logs
- Codegen: table → backend + pages + permission SQL
- Swagger UI in dev (disabled in prod by default)

## Stack

| Area | Technology |
| --- | --- |
| Backend | Spring Boot **4.1.0**, Java **25** |
| Auth | Sa-Token **1.45.0** + Redis sessions |
| ORM | MyBatis-Plus **3.5.16**, MySQL 8 |
| Templates | Thymeleaf (UI), Freemarker (codegen) |
| API docs | springdoc-openapi **3.1.0** (dev on / prod off) |
| Frontend | **Layui 2.13.9**, **Pear Admin** (same-origin static) |
| Other | Hutool, captcha-core, EasyExcel, HikariCP |

## Requirements

- JDK **25+**
- **MySQL 8.0+**
- **Redis 6.0+** (required for sessions, captcha, caches, rate limits, locks)
- Maven 3.9+ or bundled `mvnw` / `mvnw.cmd`

## Quick start

### 1. Clone

```bash
git clone https://gitee.com/ylmao/admin-layui.git
cd admin-layui
```

### 2. Database

Create a database and import the seed:

```bash
mysql -u <user> -p <database> < doc/admin-layui.sql
```

`sys_config` includes site name, logo (`/static/admin/images/ylmao/logo.png`), and `security.*`; missing items can fail startup checks.

### 3. Configure connections

Edit for your environment (**do not commit real passwords**):

| File | Purpose |
| --- | --- |
| `src/main/resources/application-dev.yml` | Dev: MySQL, Redis, port (**8085** default) |
| `src/main/resources/application-prod.yml` | Prod: MySQL, Redis, port (**8081** default) |

Timezone: `app.timezone` in `application.yml` (default `Asia/Shanghai`); restart after changes.

### 4. Run

Default profile **dev**:

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

Open **http://localhost:8085/login** (port from `application-dev.yml`).

Admin account is defined in `doc/admin-layui.sql` (`sys_user`); change the password after deploy.

## Package & run

```bash
.\mvnw.cmd clean package -DskipTests   # Windows
./mvnw clean package -DskipTests       # Linux / macOS
```

Artifact:

```text
target/admin-layui-0.0.1-SNAPSHOT.jar
```

```bash
java -jar target/admin-layui-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## Deployment notes

- **Multiple instances**: share one **Redis** for online users, kick-out, captcha, and rate limits.
- **Nginx**: see [`doc/nginx-admin.example.conf`](doc/nginx-admin.example.conf); forward `X-Forwarded-For` for client IP.
- **Smoke test**: JDK 25, seed applied, `security.*` (7 keys), Redis/MySQL up, upload dir writable, `/login` works.

See FAQ below and [`doc/接口文档与开发说明.md`](doc/接口文档与开发说明.md).

## URLs (dev)

| Path | Description |
| --- | --- |
| `/login` | Login page |
| `/admin/index` | Admin home after login |
| `/apidoc/listView` | API docs menu (permission; embedded Swagger) |
| `/swagger-ui/index.html` | Swagger UI (login required) |
| `/captcha/captchaImage` | Captcha image |

## Layout

```text
.
├── doc/
│   ├── admin-layui.sql
│   ├── nginx-admin.example.conf
│   ├── 接口文档与开发说明.md
│   └── ylmao/
├── src/main/java/com/ylmao/admin/
├── src/main/resources/
│   ├── templates/
│   ├── static/
│   ├── mapper/
│   └── application*.yml
├── mvnw / mvnw.cmd
└── pom.xml
```

## Development

- Reference modules: **`user`** (primary), **`role`**, **`post`** (OpenAPI sample).
- Frontend: [Layui docs](https://layui.dev/) first, then `templates/system/*.html`.
- Backend: DTO/VO, `PageQuery` + `R.page`, no repeated module name in URL paths.
- API UI: Dev tools → API docs, or the Chinese dev doc above.

## FAQ

**Redis connection failed**  
Check Redis is running, `spring.data.redis`, firewall; clusters must use the same Redis.

**Database connection failed**  
Check DB name, credentials, MySQL access, active Spring profile.

**Startup: missing `security.*` config**  
Import or merge `security` rows from `doc/admin-layui.sql`.

**Login: Jackson cannot resolve `com.md.admin.*`**  
Stale Redis sessions after a package rename; flush dev Redis DB and log in again.

**Redirect to login**  
Session expired or missing `saToken` cookie.

**Static 404**  
Keep `spring.mvc.static-path-pattern: /static/**` and ensure assets are in the JAR.

---

<p align="center">
  <sub>YLmao-Admin-Layui · Thanks to the Layui and Pear Admin communities</sub>
</p>
