# 邻里互助 Docker 宝塔部署说明

本文档对应服务器：

- 服务器 IP：`43.139.250.177`
- 对外访问地址：`http://43.139.250.177:8012`

当前推荐方案不是宝塔传统 `Java项目 + 静态站点`，而是：

- 你在本地先打包
- 服务器只上传打包产物和 Docker 编排文件
- 宝塔左侧 `Docker` 面板导入编排启动
- 不在服务器上构建前后端镜像，直接挂载本地打包产物

## 1. 这套 Docker 方案包含什么

本次已补齐以下文件：

- [docker-compose.yml](D:\user_wuyou\local_life_helper\deploy\linbang-docker\deploy\docker\docker-compose.yml)
- [nginx/default.conf](D:\user_wuyou\local_life_helper\deploy\linbang-docker\deploy\docker\nginx\default.conf)
- [.env.example](D:\user_wuyou\local_life_helper\deploy\linbang-docker\deploy\docker\.env.example)

编排启动后会有 4 个容器：

- `linbang-mysql`
- `linbang-redis`
- `linbang-backend`
- `linbang-nginx`

访问入口统一走：

- 管理端：`http://43.139.250.177:8012`

容器内部流量关系：

- Nginx 暴露 `8012`
- 前端静态页由 `nginx` 容器直接提供
- `/admin-api`、`/app-api`、`/infra/ws` 由 `nginx` 转发到 `backend`
- `backend` 连接 `mysql` 和 `redis`

## 2. 宝塔面板前置要求

先确认宝塔里这些都装好了：

- Docker
- Docker Compose 插件

如果宝塔 Docker 面板里有 `编排`、`Compose`、`项目编排` 之类入口，就可以直接用。

## 3. 第一步：本地先打包

### 3.1 后端打包

在本地执行：

```powershell
cd D:\user_wuyou\local_life_helper\ruoyi-vue-pro
mvn clean package -pl yudao-server -am -DskipTests
```

产物：

- `ruoyi-vue-pro/yudao-server/target/yudao-server.jar`

### 3.2 前端打包

先确认 [`.env.bt`](D:\user_wuyou\local_life_helper\yudao-ui-admin-vue3\.env.bt:6) 中的 `VITE_BASE_URL` 已经改成：

```env
VITE_BASE_URL='http://43.139.250.177:8012'
```

然后在本地执行：

```powershell
cd D:\user_wuyou\local_life_helper\yudao-ui-admin-vue3
pnpm install
pnpm build:bt
```

产物：

- `yudao-ui-admin-vue3/dist-bt`

## 4. 第二步：准备服务器部署目录

建议在服务器上准备目录，例如：

```bash
/www/wwwroot/linbang-docker
```

推荐整理成这个结构：

```text
/www/wwwroot/linbang-docker/
  deploy/docker/
  ruoyi-vue-pro/yudao-server/target/yudao-server.jar
  ruoyi-vue-pro/sql/mysql/
  yudao-ui-admin-vue3/dist-bt/
```

把下面这些内容上传到服务器：

- `deploy/docker/docker-compose.yml`
- `deploy/docker/.env.example`
- `deploy/docker/nginx/default.conf`
- `ruoyi-vue-pro/yudao-server/target/yudao-server.jar`
- `ruoyi-vue-pro/sql/mysql/建表.sql`
- `ruoyi-vue-pro/sql/mysql/初始化.sql`
- 整个 `yudao-ui-admin-vue3/dist-bt`

说明：

- `Dockerfile` 现在不是启动必须项了
- 宝塔只需要拉基础镜像 `mysql`、`redis`、`eclipse-temurin`、`nginx`
- 前后端产物直接从宿主机目录挂进去，不再走 `docker build`

当前宝塔 Docker 编排容易把 `compose` 文件复制到面板内部目录再执行，所以这份交付里的
`docker-compose.yml` 已经改成了绝对路径版本，统一指向：

- `/www/wwwroot/linbang-docker`

本地我已经帮你整理好了一个可直接上传的单根目录：

- [deploy/linbang-docker](D:\user_wuyou\local_life_helper\deploy\linbang-docker:1)

你现在不需要自己手拼目录，直接把这个目录整体上传到服务器，并落到：

- `/www/wwwroot/linbang-docker/`

实际导入编排时，使用：

```bash
/www/wwwroot/linbang-docker/deploy/docker/docker-compose.yml
```

## 5. 第三步：创建环境变量文件

在服务器上复制一份：

```bash
cp /www/wwwroot/linbang-docker/deploy/docker/.env.example /www/wwwroot/linbang-docker/deploy/docker/.env
```

然后至少改这些值：

```env
PUBLIC_IP=43.139.250.177
PUBLIC_PORT=8012
PUBLIC_BASE_URL=http://43.139.250.177:8012
ADMIN_UI_URL=http://43.139.250.177:8012
ADMIN_UI_HOST=43.139.250.177:8012

MYSQL_ROOT_PASSWORD=你自己的强密码
MYSQL_DATABASE=linbang
MYSQL_USER=linbang
MYSQL_PASSWORD=你自己的数据库密码

REDIS_PASSWORD=
REDIS_DATABASE=11

SERVER_PORT=48080
SPRING_PROFILES_ACTIVE=prod
JAVA_OPTS=-Xms512m -Xmx1024m -Djava.security.egd=file:/dev/./urandom

AMAP_WEB_KEY=你的高德 Web 服务 Key
```

如果你暂时还没有微信公众号/小程序配置，也没关系，当前交付包已经给了可启动的占位值。
后面正式接微信登录或微信支付时，再把下面这些环境变量替换成真实值：

```env
WX_MP_APP_ID=
WX_MP_SECRET=
WX_MINIAPP_APPID=
WX_MINIAPP_SECRET=
```

说明：

- 现在默认是 `http://43.139.250.177:8012`
- 等你后面有域名和 SSL，再把 `PUBLIC_BASE_URL` 改成 `https://你的域名`

## 6. 第四步：用宝塔 Docker 面板导入编排

在宝塔 `Docker` 面板里：

1. 找到 `Compose` 或 `项目编排`
2. 选择 `导入 compose`
3. 选择文件：
   - `/www/wwwroot/linbang-docker/deploy/docker/docker-compose.yml`
4. 工作目录选择：
   - `/www/wwwroot/linbang-docker/deploy/docker`
5. 确保读取的是同目录下的 `.env`
6. 点击创建并启动

说明：

- 即使宝塔把 compose 拷贝到 `/www/server/panel/data/...` 再执行，也不会影响启动
- 因为现在已经不依赖 `build.context`
- 只要你上传目录最终就是 `/www/wwwroot/linbang-docker/`，挂载路径就能命中

如果宝塔要求手动填命令，本质上就是执行：

```bash
cd /www/wwwroot/linbang-docker/deploy/docker
mkdir -p /www/wwwroot/linbang-docker/data/mysql
mkdir -p /www/wwwroot/linbang-docker/data/redis
docker compose up -d
```

## 7. 首次启动会发生什么

首次启动时会自动完成这些事：

1. MySQL 容器创建 `linbang` 库
2. 自动执行 UTF-8 SQL 文件：
   - `ruoyi-vue-pro/sql/mysql/建表.sql`
   - `ruoyi-vue-pro/sql/mysql/初始化.sql`
3. Redis 启动
4. 后端容器直接挂载你本地打好的 `yudao-server.jar`
5. Nginx 直接挂载你本地打好的 `dist-bt`
6. Nginx 对外暴露 `8012`

## 8. 访问与验收

启动后优先检查：

### 7.1 打开页面

浏览器访问：

- [http://43.139.250.177:8012](http://43.139.250.177:8012)

### 7.2 后端健康检查

浏览器访问：

- [http://43.139.250.177:8012/actuator/health](http://43.139.250.177:8012/actuator/health)

如果你没开放 `48080` 公网端口，也没关系，因为健康检查走的是 `nginx` 转发入口。

### 7.3 Swagger

- [http://43.139.250.177:8012/swagger-ui](http://43.139.250.177:8012/swagger-ui)

### 7.4 登录接口

登录页能正常调用：

- `POST /admin-api/system/auth/login`

## 9. 线上 SQL 变更执行

首次部署时，`建表.sql` 和 `初始化.sql` 会由 MySQL 容器自动导入；但后续线上新增字段、修复数据、补充配置等增量 SQL，不会自动重复执行，需要你手工执行。

强烈建议：

- 不要直接在宝塔终端粘贴大段含中文/JSON 的 SQL 去执行
- 不要把增量变更直接塞回 `建表.sql` 或 `初始化.sql` 再指望线上自动生效
- 每次线上 SQL 变更都使用单独的 UTF-8 SQL 文件

### 9.1 准备增量 SQL 文件

建议在服务器上额外放一个目录，例如：

```bash
mkdir -p /www/wwwroot/linbang-docker/sql-patch
```

每次变更新建一个独立 SQL 文件，文件名尽量使用 ASCII，例如：

```text
/www/wwwroot/linbang-docker/sql-patch/20260629_add_sms_config.sql
```

要求：

- 文件编码必须是 `UTF-8`
- 文件里可以保留可读中文，禁止转成乱码或问号
- 一个变更一个文件，方便回溯

### 9.2 先备份数据库

执行增量 SQL 前，先导出当前库：

```bash
mkdir -p /www/wwwroot/linbang-docker/sql-backup

docker exec linbang-mysql sh -lc 'mysqldump -uroot -p"$MYSQL_ROOT_PASSWORD" --default-character-set=utf8mb4 linbang' > /www/wwwroot/linbang-docker/sql-backup/linbang_$(date +%F_%H%M%S).sql
```

说明：

- 当前编排里的 MySQL 容器名是 `linbang-mysql`
- 数据库名默认是 `linbang`
- 这里使用容器环境变量里的 `MYSQL_ROOT_PASSWORD`，不需要把密码明文再写一遍

### 9.3 执行增量 SQL

把 SQL 文件上传到服务器后，执行：

```bash
docker exec -i linbang-mysql sh -lc 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" --default-character-set=utf8mb4 linbang' < /www/wwwroot/linbang-docker/sql-patch/20260629_add_sms_config.sql
```

说明：

- 这是推荐做法：宿主机 UTF-8 文件 -> 容器内 `mysql` 执行
- 不要改成 `echo "中文 SQL" | docker exec ... mysql`
- 如果 SQL 文件里有中文文案、JSON、配置值，这种方式最稳

### 9.4 执行后验证

至少补一轮验证：

```bash
docker exec -it linbang-mysql sh -lc 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" linbang'
```

进入 MySQL 后，执行你本次变更对应的验证 SQL，例如：

```sql
SHOW TABLES;
DESC your_table;
SELECT COUNT(*) FROM your_table;
```

如果本次变更涉及中文数据、配置文案、消息模板、协议内容，至少抽查：

- 一条旧记录是否正常
- 一条本次新增/更新记录是否正常
- 没有出现 `????`、`�`、明显乱码片段

### 9.5 什么时候需要重启后端

分两类看：

1. 纯数据修复、纯配置补录、纯历史数据更新：
   不一定需要重启后端，执行 SQL 后验证接口即可
2. SQL 变更配套了后端代码变更：
   需要重新打 `jar`、上传新 `jar`、再重启后端容器

如果你已经重新上传了新 `jar`，重启后端可执行：

```bash
cd /www/wwwroot/linbang-docker/deploy/docker
docker compose restart backend
```

如果你还改了 `docker-compose.yml` 或 `.env`，建议执行：

```bash
cd /www/wwwroot/linbang-docker/deploy/docker
docker compose up -d
```

### 9.6 首次初始化 SQL 与增量 SQL 的边界

请明确区分：

- `ruoyi-vue-pro/sql/mysql/建表.sql`
- `ruoyi-vue-pro/sql/mysql/初始化.sql`

它们只用于空库首次初始化。

线上已运行环境后续再改这两个文件：

- 不会自动把变更补到现有数据库
- 只能影响下一次“全新空库初始化”

所以线上正式变更请统一走：

- 独立增量 SQL 文件
- 先备份，再执行，再验证

## 10. 常见问题

### 8.1 页面打开了，但接口 404 或 502

优先检查：

- `linbang-backend` 容器是否启动成功
- `linbang-nginx` 是否正常运行
- `.env` 里的 `PUBLIC_BASE_URL` 是否写错

### 8.2 后端启动失败

优先检查：

- MySQL 密码是否和 `.env` 一致
- SQL 初始化是否成功
- 容器日志里是否报数据库连接失败
- `yudao-server.jar` 是否确实存在于 `/www/wwwroot/linbang-docker/ruoyi-vue-pro/yudao-server/target/`

### 8.3 中文乱码

这次编排已经走 UTF-8 SQL 文件挂载导入，不是命令行内联 SQL，风险比手工导入低很多。

如果仍然乱码，检查：

- MySQL 是否用了 `utf8mb4`
- 初始化是不是首次创建库时执行的

### 8.4 WebSocket 不通

这套 Nginx 配置已经包含：

- `/infra/ws`

如果 IM 相关能力异常，先看 `linbang-nginx` 配置是否被你手工改坏。

## 11. 生产建议

当前这套先适合你快速跑通环境，后面建议继续做两件事：

1. 换成域名 + HTTPS
2. 把 MySQL/Redis 公网端口限制到内网或直接取消暴露

尤其是：

- `3306`
- `6379`
- `48080`

这版编排已经默认不对公网暴露这 3 个端口。

## 12. 你现在最短操作路径

你可以直接照这个顺序做：

1. 本地执行后端 `mvn clean package -pl yudao-server -am -DskipTests`
2. 本地执行前端 `pnpm build:bt`
3. 上传整个 [deploy/linbang-docker](D:\user_wuyou\local_life_helper\deploy\linbang-docker:1) 到 `/www/wwwroot/linbang-docker/`
4. 复制 `deploy/docker/.env.example` 为 `deploy/docker/.env`
5. 改密码和 `AMAP_WEB_KEY`
6. 先创建数据目录：

```bash
mkdir -p /www/wwwroot/linbang-docker/data/mysql
mkdir -p /www/wwwroot/linbang-docker/data/redis
```

7. 在宝塔 Docker 面板导入 `deploy/docker/docker-compose.yml`
8. 点击启动
8. 打开 [http://43.139.250.177:8012](http://43.139.250.177:8012)

如果你愿意，我下一步可以继续帮你把这套 compose 再收紧一版，做成“生产更稳”的版本：

- 支持 HTTPS
- 支持宝塔一键更新镜像
