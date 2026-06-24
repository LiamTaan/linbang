# 邻里互助项目接口结构规范

## 1. 目标

本文件不是最终 OpenAPI 全量文档，而是接口结构骨架。后续所有真实接口设计，都必须遵守本文件。

## 2. 基础规则

### 2.1 前缀规则

- 用户端接口前缀：`/app-api`
- 管理端接口前缀：`/admin-api`

### 2.2 风格规则

统一按芋道 `ruoyi-vue-pro` 风格实现，参考 `System` 模块：

- Controller 命名
- BO / VO 分层
- 列表分页结构
- 导出接口规范

### 2.3 响应规则

- 普通接口：`CommonResult<T>`
- 分页接口：框架统一分页对象
- 禁止自造另一套响应包装

## 3. 用户端接口骨架

### 3.1 用户与认证

- `POST /app-api/member/auth/login`
- `GET /app-api/member/auth/social-auth-redirect`
- `POST /app-api/member/auth/social-login`
- `POST /app-api/member/auth/social-bind-mobile`
- `POST /app-api/member/auth/logout`
- `POST /app-api/member/auth/send-sms-code`
- `POST /app-api/member/auth/refresh-token`
- `GET /app-api/member/user/profile`
- `PUT /app-api/member/user/update-profile`
- `POST /app-api/member/real-name/create`
- `GET /app-api/member/real-name/get`
- `POST /app-api/member/qualification/create`
- `GET /app-api/member/qualification/page`
- `POST /app-api/member/role-apply/create`
- `GET /app-api/member/role-apply/page`
- `GET /app-api/member/role-apply/get`

认证补充说明：

- `POST /app-api/member/auth/login` 同时承担短信登录与短信注册；若手机号未注册，则验证码校验成功后自动完成注册并返回正式登录态。
- `POST /app-api/member/auth/send-sms-code` 为短信登录与短信注册共用验证码发送接口。
- App 原生第三方登录当前只支持：
  - `type = 32`：微信开放平台授权
  - `type = 40`：支付宝授权
- `POST /app-api/member/auth/social-login` 在第三方账号未绑定手机号时，返回 `bindRequired = true`，且 `userId`、`accessToken`、`refreshToken`、`expiresTime` 可以为空。
- `POST /app-api/member/auth/social-bind-mobile` 在校验短信验证码成功后完成绑定；若手机号未注册则自动创建账号，若已注册则绑定到既有账号，并返回正式登录态。

### 3.2 地址

- `GET /app-api/member/address/page`
- `GET /app-api/member/address/get`
- `POST /app-api/member/address/create`
- `PUT /app-api/member/address/update`
- `DELETE /app-api/member/address/delete`

### 3.3 服务商与入驻

- `POST /app-api/merchant/entry/create`
- `GET /app-api/merchant/entry/get`
- `GET /app-api/merchant/info/profile`
- `PUT /app-api/merchant/info/update-profile`
- `GET /app-api/merchant/info/accept-status/get`
- `PUT /app-api/merchant/info/accept-status/update`
- `GET /app-api/merchant/service-category/list`
- `PUT /app-api/merchant/service-category/selected/update`
- `GET /app-api/merchant/service-point/page`
- `GET /app-api/merchant/service-point/get`
- `POST /app-api/merchant/service-point/create`
- `PUT /app-api/merchant/service-point/update`
- `PUT /app-api/merchant/service-point/status/update`
- `DELETE /app-api/merchant/service-point/delete`

### 3.4 订单

- `POST /app-api/order/info/create`
- `GET /app-api/order/info/page`
- `GET /app-api/order/info/get`
- `POST /app-api/order/info/cancel`
- `GET /app-api/order/unit/page`
- `GET /app-api/order/unit/get`
- `POST /app-api/order/unit/start-service`
- `POST /app-api/order/unit/confirm`
- `POST /app-api/order/accept/create`
- `POST /app-api/order/unit/upload-delivery-proof`

### 3.5 支付与钱包

- `POST /app-api/pay/order/create`
- `GET /app-api/pay/order/get`
- `POST /app-api/pay/refund/create`
- `GET /app-api/wallet/account/get`
- `GET /app-api/wallet/flow/page`
- `GET /app-api/wallet/flow/get`
- `POST /app-api/wallet/withdraw/create`
- `GET /app-api/wallet/withdraw/page`
- `GET /app-api/wallet/withdraw/get`
- `GET /app-api/wallet/bank-card/page`
- `GET /app-api/wallet/bank-card/get`
- `POST /app-api/wallet/bank-card/create`
- `PUT /app-api/wallet/bank-card/update`
- `PUT /app-api/wallet/bank-card/default`
- `DELETE /app-api/wallet/bank-card/delete`

### 3.6 投诉、申诉、评价

- `POST /app-api/review/comment/create`
- `GET /app-api/review/comment/page`
- `GET /app-api/review/comment/get`
- `POST /app-api/review/complaint/create`
- `GET /app-api/review/complaint/page`
- `GET /app-api/review/complaint/get`
- `POST /app-api/review/appeal/create`
- `GET /app-api/review/appeal/page`
- `GET /app-api/review/appeal/get`
- `GET /app-api/review/credit/get`
- `GET /app-api/review/credit-record/page`
- `GET /app-api/review/credit-record/get`

### 3.7 推广

- `GET /app-api/promote/center/get`
- `GET /app-api/promote/commission/page`
- `GET /app-api/promote/invite-code/get`
- `POST /app-api/promote/invite-code/bind`

### 3.8 区域合作商与工作台

- `GET /app-api/partner/workbench/get`
- `GET /app-api/dashboard/overview`
- `GET /app-api/dashboard/order-stat`
- `GET /app-api/dashboard/finance-stat`
- `GET /app-api/dashboard/user-stat`

### 3.9 消息与反馈

- `GET /app-api/message/record/page`
- `GET /app-api/message/record/get`
- `POST /app-api/help/feedback/create`
- `GET /app-api/help/feedback/page`
- `GET /app-api/help/feedback/get`

### 3.10 系统与平台配置

- `GET /app-api/platform-config/app-settings`
- `GET /app-api/platform-config/agreement`

## 4. 管理端接口骨架

### 4.1 用户中心

- `GET /admin-api/member/user/page`
- `GET /admin-api/member/user/get`
- `PUT /admin-api/member/user/update-status`
- `GET /admin-api/member/real-name/page`
- `GET /admin-api/member/real-name/get`
- `POST /admin-api/member/real-name/audit`
- `GET /admin-api/member/qualification/page`
- `GET /admin-api/member/qualification/get`
- `POST /admin-api/member/qualification/audit`
- `GET /admin-api/member/role-apply/page`
- `GET /admin-api/member/role-apply/get`
- `POST /admin-api/member/role-apply/audit`

### 4.2 服务商中心

- `GET /admin-api/merchant/info/page`
- `GET /admin-api/merchant/info/get`
- `GET /admin-api/merchant/entry/page`
- `POST /admin-api/merchant/entry/audit`
- `GET /admin-api/merchant/service-category/tree`
- `POST /admin-api/merchant/service-category/create`
- `PUT /admin-api/merchant/service-category/update`
- `DELETE /admin-api/merchant/service-category/delete`
- `GET /admin-api/merchant/service-point/page`
- `GET /admin-api/merchant/service-point/get`
- `POST /admin-api/merchant/service-point/create`
- `PUT /admin-api/merchant/service-point/update`
- `DELETE /admin-api/merchant/service-point/delete`
- `GET /admin-api/merchant/service-point/export-excel`

### 4.3 订单中心

- `GET /admin-api/order/info/page`
- `GET /admin-api/order/info/get`
- `GET /admin-api/order/unit/page`
- `GET /admin-api/order/unit/get`
- `POST /admin-api/order/info/mark-abnormal`
- `POST /admin-api/order/unit/unlock`
- `GET /admin-api/order/accept-record/page`
- `GET /admin-api/order/match-record/page`

### 4.4 资金中心

- `GET /admin-api/pay/order/page`
- `GET /admin-api/pay/order/get`
- `GET /admin-api/pay/refund/page`
- `GET /admin-api/pay/refund-context/get`
- `POST /admin-api/pay/refund/audit`
- `GET /admin-api/wallet/account/get`
- `GET /admin-api/wallet/account/page`
- `GET /admin-api/wallet/flow/get`
- `GET /admin-api/wallet/flow/page`
- `GET /admin-api/wallet/withdraw/page`
- `GET /admin-api/wallet/withdraw/get`
- `GET /admin-api/wallet/bank-card/get`
- `POST /admin-api/wallet/withdraw/audit`
- `GET /admin-api/wallet/bank-card/page`
- `GET /admin-api/wallet/divide-rule/get`
- `GET /admin-api/wallet/divide-rule/page`
- `POST /admin-api/wallet/divide-rule/create`
- `PUT /admin-api/wallet/divide-rule/update`

### 4.5 风控中心

- `GET /admin-api/risk/rule/get`
- `GET /admin-api/risk/rule/page`
- `POST /admin-api/risk/rule/create`
- `PUT /admin-api/risk/rule/update`
- `DELETE /admin-api/risk/rule/delete`
- `GET /admin-api/risk/sensitive-word/page`
- `GET /admin-api/risk/sensitive-word/get`
- `POST /admin-api/risk/sensitive-word/create`
- `PUT /admin-api/risk/sensitive-word/update`
- `DELETE /admin-api/risk/sensitive-word/delete`
- `GET /admin-api/risk/event/get`
- `GET /admin-api/risk/event/page`
- `GET /admin-api/risk/blacklist/get`
- `GET /admin-api/risk/blacklist/page`

### 4.6 信用与售后

- `GET /admin-api/review/complaint/page`
- `GET /admin-api/review/complaint/get`
- `POST /admin-api/review/complaint/process`
- `GET /admin-api/review/appeal/page`
- `GET /admin-api/review/appeal/get`
- `POST /admin-api/review/appeal/audit`
- `GET /admin-api/review/comment/page`
- `GET /admin-api/review/comment/get`
- `GET /admin-api/review/credit-record/page`
- `GET /admin-api/review/credit-record/get`
- `GET /admin-api/review/credit-record/export-excel`
- `GET /admin-api/review/credit-rule/get`
- `PUT /admin-api/review/credit-rule/update`

### 4.7 推广与合作

- `GET /admin-api/promote/user/page`
- `GET /admin-api/promote/user/get`
- `GET /admin-api/promote/commission/page`
- `GET /admin-api/promote/commission/get`
- `GET /admin-api/partner/info/page`
- `GET /admin-api/partner/info/get`
- `GET /admin-api/partner/price-report/page`
- `GET /admin-api/partner/price-report/get`
- `POST /admin-api/partner/price-report/audit`

### 4.8 消息中心

- `GET /admin-api/message/record/page`
- `GET /admin-api/message/record/get`
- `GET /admin-api/message/template/page`
- `GET /admin-api/message/template/get`
- `POST /admin-api/message/template/create`
- `PUT /admin-api/message/template/update`
- `GET /admin-api/message/push-task/page`
- `GET /admin-api/message/push-task/get`

### 4.9 数据中心

- `GET /admin-api/dashboard/overview`
- `GET /admin-api/dashboard/order-stat`
- `GET /admin-api/dashboard/finance-stat`
- `GET /admin-api/dashboard/user-stat`

### 4.10 平台配置

- `GET /admin-api/platform-config/setting/page`
- `POST /admin-api/platform-config/setting/create`
- `PUT /admin-api/platform-config/setting/update`
- `POST /admin-api/security/dynamic-key/verify`
- `GET /admin-api/help/feedback/page`

## 5. 列表页标准

每个管理端列表页接口至少保证：

- 分页
- 查询条件
- 状态字段
- 创建时间
- 操作所需的主键和状态字段

禁止：

- 列表页缺状态字段
- 列表页缺主键
- 列表页与详情页字段名不一致

## 6. 审核类接口标准

审核类接口必须统一包含：

- `id`
- `auditStatus`
- `auditRemark`

驳回时必须支持：

- `rejectReason`

## 7. 详情页标准

详情页接口必须满足：

- 字段口径与列表页一致
- 能覆盖页面展示所需全部主区块
- 提供状态、日志、附件、关联记录

## 8. 后续扩展要求

当开始实际编码前，应基于本骨架继续补成：

- `openapi.yaml` 或
- 各模块接口清单文档

但无论如何，都不得脱离本结构另起一套接口风格。
