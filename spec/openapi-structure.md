# 邻里互助用户中心与资金域 OpenAPI 结构

## App API

- `/app-api/order/*`
  - 订单分页支持业务分类：待接、服务中、已完成、售后、待评价、待付款、已退款
  - 订单详情返回统一时间线：订单、单元、支付、退款、投诉、申诉、操作日志
  - 订单修改
  - 订单取消
  - 单元核销码查看
  - 单元核销
  - 核销日志查询
- `/app-api/wallet/*`
  - 钱包首页 / 我的账户总览
  - 钱包流水分页/详情
  - 推广收益摘要增强字段
  - 银行卡分页/详情/新增/删除/设默认
  - 提现申请/分页/详情
- `/app-api/member/role-apply/*`
  - 身份申请分页 / 详情
  - 代理、推广员、平台运营三类身份申请
- `/app-api/member/qualification/*`
  - 资质详情
  - 到期提醒与认证进度查询
- `/app-api/reminder/*`
  - 生日提醒、自定义事件提醒 CRUD
  - 提醒列表
  - 提醒历史
  - 证件到期提醒统一聚合查询
- `/app-api/promote/*`
  - 推广中心
  - 两级团队统计与收益摘要
- `/app-api/reward-order/*`
  - 我发布的悬赏列表
  - 我参与的悬赏列表
  - 悬赏详情
  - 悬赏创建
  - 参与悬赏
  - 参与记录分页
  - 本轮最小闭环复用晒单悬赏申请与参与记录表
- `/app-api/pay/order/*`
  - 创建支付单
  - 查询支付单
  - 支付回调通知
- `/app-api/pay/refund/*`
  - 创建退款申请
  - 查询退款分页
  - 查询退款详情
  - 退款回调通知
- `/app-api/order/*`
  - 订单详情内返回托管凭证、退款记录、结算解锁信息
- `/app-api/platform-config/*`
  - App 设置
  - 税务提醒
  - 个体执照代办入口

## Admin API

- `/admin-api/order/info/*`
  - 订单分页、详情、编辑、删除
  - 详情展示单元核销信息与统一时间线
- `/admin-api/order/unit/*`
  - 单元分页、详情、解锁
  - 详情展示核销字段与单元时间线
- `/admin-api/member/role-apply/*`
  - 身份申请分页、详情、审核
  - 差异化展示代理、推广员、平台运营申请字段
- `/admin-api/wallet/account/*`
- `/admin-api/wallet/flow/*`
- `/admin-api/wallet/withdraw/*`
- `/admin-api/wallet/bank-card/*`
- `/admin-api/wallet/divide-rule/*`
- `/admin-api/wallet/commission-order/*`
- `/admin-api/wallet/escrow-proof/*`
- `/admin-api/pay/refund-context/*`

## 文档要求

- 所有金额字段统一单位为元，若为支付模块原始字段需注明“分”。
- 所有状态字段必须描述枚举值语义。
- 所有关联 ID 需注明指向对象。
- 所有核销字段必须明确：核销作用域、核销状态、核销码是否可见、核销时间、核销人含义。
- 所有身份申请字段必须明确角色适用范围，不能只给字段名不说明适用角色。
- 所有提醒字段必须明确：提醒类型、重复规则、下一次提醒时间、最近一次触发时间、是否系统生成、是否允许编辑。
- “我的悬赏”接口必须在文档中明确：本轮交付复用晒单悬赏申请主表，并新增参与记录表补齐“我参与的悬赏”和参与记录查询。
- 钱包、提现、退款、分账、托管凭证必须提供列表和详情字段说明。
