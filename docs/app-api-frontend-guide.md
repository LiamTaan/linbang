# 邻里互助 App 前端接口文档说明

本文件给 App / 小程序前端联调用，作为独立入口说明。

## 1. 推荐文档入口

- Swagger UI：`http://127.0.0.1:48080/swagger-ui/index.html?urls.primaryName=linbang-app`
- OpenAPI JSON：`http://127.0.0.1:48080/v3/api-docs/linbang-app`

说明：

- `linbang-app` 只包含邻里互助用户端接口，不再混入一整站的后台、基础设施、演示模块接口。
- 管理端接口请查看 `linbang-admin` 分组。

## 2. 接口前缀与鉴权

- App 端统一前缀：`/app-api/*`
- 管理端统一前缀：`/admin-api/*`
- 登录后请求头：
  - `Authorization: Bearer {accessToken}`
  - `tenant-id: 1`

## 3. 通用返回结构

普通接口：

```json
{
  "code": 0,
  "data": {},
  "msg": ""
}
```

分页接口：

```json
{
  "code": 0,
  "data": {
    "list": [],
    "total": 0
  },
  "msg": ""
}
```

## 4. 认证接入说明

### 4.1 短信登录与短信注册

- App 端当前不提供独立 `register` 接口。
- 登录页和注册页统一调用 `POST /app-api/member/auth/login`。
- 入参固定为：
  - `mobile`
  - `code`
- 语义规则：
  - 手机号已注册：校验验证码成功后直接登录。
  - 手机号未注册：校验验证码成功后自动注册并直接登录。
- `POST /app-api/member/auth/send-sms-code` 为登录/注册共用验证码发送接口。

### 4.2 原生 App 第三方登录流程

- 第一步：调用 `GET /app-api/member/auth/social-auth-redirect` 获取授权跳转地址。
- 第二步：App 拉起第三方授权，拿到 `code` 与 `state`。
- 第三步：调用 `POST /app-api/member/auth/social-login`。
- 返回规则：
  - 已绑定手机号：直接返回正式登录态。
  - 未绑定手机号：返回 `bindRequired = true`，且 `userId`、`accessToken`、`refreshToken`、`expiresTime` 允许为空。
- 第四步：若 `bindRequired = true`，前端继续调用 `POST /app-api/member/auth/social-bind-mobile`，传入手机号和短信验证码。
- 绑定规则：
  - 手机号未注册：自动创建正式账号并绑定。
  - 手机号已注册：绑定到既有正式账号。
- 绑定成功后返回正式登录态。

### 4.3 第三方平台类型 `socialType`

- `32`：微信开放平台授权
- `40`：支付宝授权

## 5. 前端必须统一理解的状态值

### 5.1 实名认证状态 `realNameStatus`

- `PENDING`：待审核
- `APPROVED`：已通过
- `REJECTED`：已驳回

### 5.2 服务商入驻状态 `entryStatus`

- `PENDING`：待审核
- `FIRST_APPROVED`：初审通过
- `APPROVED`：终审通过
- `REJECTED`：已驳回

### 5.3 主订单状态 `order.status`

- `PENDING_PAY`：待支付
- `PENDING_ACCEPT`：待接单
- `ACCEPTED`：已接单
- `SERVING`：服务中
- `PENDING_CONFIRM`：待确认
- `AFTER_SALE`：售后中
- `FINISHED`：已完成
- `REFUNDED`：已退款
- `CLOSED`：已关闭

### 5.4 订单单元状态 `orderUnit.status`

- `PENDING_CREATE`：待生成
- `PENDING_ACCEPT`：待接单
- `ACCEPTED`：已接单
- `SERVING`：服务中
- `PENDING_CONFIRM`：待验收
- `FINISHED`：已完成
- `APPEALING`：申诉中
- `REFUNDED`：已退款
- `CLOSED`：已关闭

### 5.5 拆单状态 `splitStatus`

- `UNSPLIT`：不拆单
- `SPLIT`：已拆单

### 5.6 支付状态 `payStatus`

- `WAITING`：待支付
- `SUCCESS`：支付成功
- `FAILED`：支付失败
- `CLOSED`：已关闭

说明：

- 支付单明细中的 `status` 可能是支付模块内部数值状态，前端展示优先使用 `statusName`。

### 5.7 退款审核状态 `auditStatus`

- `PENDING`：待审核
- `APPROVED`：已通过
- `REJECTED`：已驳回

### 5.8 提现状态 `withdraw.status`

- `PENDING`：待审核
- `APPROVED`：审核通过
- `REJECTED`：审核驳回
- `SUCCESS`：打款成功
- `FAILED`：打款失败

### 5.9 投诉状态 `complaint.status`

- `PENDING`：待受理
- `PROCESSING`：处理中
- `FINISHED`：已完结
- `REJECTED`：已驳回

### 5.10 申诉状态 `appeal.status`

- `PENDING`：待审核
- `PROCESSING`：处理中
- `APPROVED`：通过
- `REJECTED`：驳回
- `FINISHED`：已完结

### 5.11 开关类状态

- `merchantStatus`：`ENABLE` 启用，`DISABLE` 停用
- `acceptStatus`：`ENABLE` 可接单，`DISABLE` 暂停接单
- `servicePoint.status`：`ENABLE` 启用，`DISABLE` 停用

### 5.12 当前角色编码 `currentRoleCode`

- `USER`：普通用户
- `MERCHANT`：服务商
- `PROMOTER`：推广员
- `PARTNER`：区域合作商

### 5.13 推广佣金状态 `commission.status`

- `PENDING`：待结算
- `SETTLED`：已结算
- `INVALID`：已失效

### 5.14 信用等级 `creditLevel`

- 当前按业务字典展示，常见值示例：`NORMAL`、`GOOD`、`EXCELLENT`
- 页面文案以前后端统一字典配置为准，不要自行硬编码另一套等级名

## 6. 联调建议

- 先只看 `linbang-app` 分组，不要从全量 Swagger 猜接口。
- 认证页面联调时，注册页也按 `/app-api/member/auth/login` 接，不要另找独立注册接口。
- 第三方登录页面必须先判断 `bindRequired`，再决定是进入业务首页还是进入绑定手机号页。
- 页面文案请以前后端统一状态值为准，不要自己重新发明另一套状态名。
- 字段说明不清时，优先以 `spec/business-rules.yaml` 和 `spec/api-contract-core.yaml` 为准。
