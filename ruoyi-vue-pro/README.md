# 邻里互助后端 / LinBang Server

这是邻里互助项目的后端工程，基于 `ruoyi-vue-pro` 落地，当前用于承接邻里互助平台的真实业务接口与后台能力。

## 项目定位

邻里互助是一个本地生活 / 邻里互助 / 交易撮合平台，核心特点是：

- 强交易
- 强审核
- 强资金
- 强风控

当前后端以单体模式运行，管理端与用户端接口统一由本工程提供。

## 本地环境

- MySQL：`127.0.0.1:3306`
- 数据库：`linbang`
- 用户名：`root`
- 密码：`XXX`
- Redis：`127.0.0.1:6379`
- Redis 密码：无

## 启动配置

当前默认使用本地配置文件：

- [yudao-server/src/main/resources/application.yaml](./yudao-server/src/main/resources/application.yaml)
- [yudao-server/src/main/resources/application-local.yaml](./yudao-server/src/main/resources/application-local.yaml)

本地启动端口：

- 后端服务：`http://127.0.0.1:48080`
- Swagger：`http://127.0.0.1:48080/swagger-ui`

## 当前业务模块

邻里互助核心业务代码位于：

- `yudao-module-linbang`

当前已经落地的主要域包括：

- `member`：用户、地址、实名、资质
- `merchant`：服务商入驻、类目、服务点、审核
- `order`：主订单、拆分单元、抢单、异常单、解锁
- `wallet`：钱包、流水、提现、退款、银行卡
- `review`：投诉、申诉、评价
- `risk`：风控规则、敏感词、信用分规则

## 接口分层

接口按平台分离：

- 管理端：`/admin-api/*`
- 用户端：`/app-api/*`

后端实现遵循芋道标准分层：

- Controller
- Service
- Convert
- DataObject
- Mapper
- VO / BO

## 数据初始化

空库初始化统一走仓库入口文件：

1. [sql/mysql/建表.sql](./sql/mysql/建表.sql)
2. [sql/mysql/初始化.sql](./sql/mysql/初始化.sql)

说明：

- 以上两份文件就是当前仓库唯一有效的空库初始化入口
- 不再维护额外的 `DB/mysql` 平行目录
- 建表阶段已合并支付模块 `pay_*` 表结构
- 初始化阶段会清理与当前系统无关的框架默认菜单

## 租户说明

当前项目按单平台模式运行，不启用业务多租户。

- 后端配置：`yudao.tenant.enable=false`
- 前端管理端不展示租户字段
- 前端保留 `tenantId=1` 作为兼容兜底，不在页面展示

## 开发约束

进入本仓库后，优先阅读以下文件：

1. [AGENTS.md](../AGENTS.md)
2. [spec/business-rules.yaml](../spec/business-rules.yaml)
3. [spec/openapi-structure.md](../spec/openapi-structure.md)
4. [spec/api-contract-core.yaml](../spec/api-contract-core.yaml)

如规则、原型、历史文档冲突，以以上文件为准。
