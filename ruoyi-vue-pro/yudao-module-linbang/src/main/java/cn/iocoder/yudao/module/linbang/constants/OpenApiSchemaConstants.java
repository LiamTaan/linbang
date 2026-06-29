package cn.iocoder.yudao.module.linbang.constants;

/**
 * LinBang OpenAPI 字段说明常量。
 *
 * <p>用于统一状态/枚举字段的 Swagger 描述，避免各个 VO 手写后逐渐漂移，
 * 也方便同步到 Apifox 后保持一致的可读性。</p>
 */
public interface OpenApiSchemaConstants {

    String PRICING_MODE =
            "计价方式，传 lb_pricing_mode 字典的 dict.value，例如 FIXED_PRICE 一口价、CONTRACT 承包、OUTSOURCING 外发、HOURLY 计时、BY_UNIT 按单位。";

    String PRICING_MODE_SET =
            "支持计价方式列表，传 lb_pricing_mode 字典的 dict.value 数组，例如 [FIXED_PRICE,HOURLY]。";

    String AGREEMENT_TYPE =
            "协议类型：TRADE_GUARANTEE 通用交易保障协议、PROJECT_ESCROW 工程托管协议。";

    String SENSITIVE_PROCESS_STRATEGY =
            "敏感处理策略：BLOCK 直接拦截、REPLACE 替换后放行、REVIEW 转人工审核、ALLOW_LOG 仅记录日志。";

    String SENSITIVE_CONTENT_TYPE =
            "内容类型：TEXT 文本、IMAGE 图片 OCR、QRCODE 二维码。";

    String CITY_LEVEL =
            "城市等级：TIER_1 一线城市、TIER_2 二线城市、TIER_3 三线及以下城市；可按平台规则继续扩展。";

    String CREDIT_TRIGGER_TYPE =
            "触发类型：AUTO 系统自动触发、MANUAL 人工调整。";

    String CREDIT_BIZ_TYPE =
            "业务类型：ORDER/ORDER_INFO 订单、ORDER_UNIT/UNIT 订单单元、ORDER_ABNORMAL/ABNORMAL 异常单、"
                    + "COMPLAINT 投诉、APPEAL 申诉、WITHDRAW/WITHDRAW_APPLY 提现、USER 用户。";

    String WALLET_FLOW_TYPE =
            "流水类型：IN 入账、OUT 出账、FREEZE 冻结、UNFREEZE 解冻。";

    String WALLET_FLOW_BIZ_TYPE =
            "钱包业务类型，记录该笔流水来源业务，例如 WITHDRAW_APPLY 提现申请、WITHDRAW 提现结果；"
                    + "其他取值按钱包业务字典扩展。";

    String WALLET_BILL_TYPE =
            "钱包账单类型：ORDER 订单托管/支付、SETTLEMENT 结算解锁、WITHDRAW 提现、REFUND 退款、COMMISSION 推广收益、ADJUST 调整。";

    String ORDER_SPLIT_STATUS =
            "拆单状态：UNSPLIT 不拆单、SPLIT 已拆单。";

    String ORDER_SPLIT_MODE =
            "拆分方式：DIRECT 直接单、BY_PROGRESS 按进度拆分、BY_PROCESS 按工序拆分、BY_CONTENT 按内容拆分、BY_PERSON 按多人拆分。";

    String RULE_MATCH_MODE =
            "规则命中模式：ANY 任一条件命中即可、ALL 全部已配置条件同时命中。";

    String ORDER_STATUS =
            "订单状态：PENDING_PAY 待支付、PENDING_ACCEPT 待接单、ACCEPTED 已接单、SERVING 服务中、"
                    + "PENDING_CONFIRM 待确认、AFTER_SALE 售后中、FINISHED 已完成、REFUNDED 已退款、CLOSED 已关闭。";

    String ORDER_BUSINESS_CATEGORY =
            "订单业务分类：WAIT_ACCEPT 待接、IN_SERVICE 服务中、FINISHED 已完成、AFTER_SALE 售后、WAIT_REVIEW 待评价、"
                    + "WAIT_PAY 待付款、REFUNDED 已退款。待评价为派生分类，表示已完成且当前用户未提交评价。";

    String ORDER_UNIT_STATUS =
            "单元状态：PENDING_CREATE 待生成、PENDING_ACCEPT 待接单、ACCEPTED 已接单、SERVING 服务中、"
                    + "PENDING_CONFIRM 待验收、FINISHED 已完成、APPEALING 申诉中、REFUNDED 已退款、CLOSED 已关闭。";

    String ORDER_VERIFY_STATUS =
            "核销状态：NOT_REQUIRED 无需核销、PENDING 待核销、VERIFIED 已核销、EXPIRED 已失效。";

    String ORDER_VERIFY_METHOD =
            "核销方式：CODE 手动输入核销码、SCAN 扫码核销、ADMIN 后台核销。";

    String AUDIT_STATUS =
            "审核状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回。";

    String QUALIFICATION_TYPE =
            "资质类型，传 lb_qualification_type 字典的 dict.value，例如 BUSINESS_LICENSE 营业执照、"
                    + "ELECTRICIAN 电工证、WELDER 焊工证、HVAC_TECHNICIAN 空调制冷证。";

    String WITHDRAW_STATUS =
            "提现状态：PENDING 待审核、APPROVED 审核通过待打款、REJECTED 审核驳回、SUCCESS 打款成功、FAILED 打款失败。";

    String COMPLAINT_STATUS =
            "投诉状态：PENDING 待受理、PROCESSING 处理中、FINISHED 已完结、REJECTED 已驳回。";

    String APPEAL_STATUS =
            "申诉状态：PENDING 待审核、PROCESSING 处理中、APPROVED 通过、REJECTED 驳回、FINISHED 已完结。";

    String ROLE_APPLY_AUDIT_STATUS =
            "身份申请审核状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回。";

    String ROLE_APPLY_ROLE_CODE =
            "身份申请角色：PROMOTER 推广员、PARTNER 代理/区域合作商、PLATFORM_OPERATOR 平台运营。";

    String MERCHANT_ENTRY_AUDIT_STATUS =
            "入驻审核动作：FIRST_APPROVED 初审通过、APPROVED 终审通过、REJECTED 驳回。";

    String MERCHANT_ENTRY_STATUS =
            "入驻状态：PENDING 待审核、FIRST_APPROVED 初审通过、APPROVED 终审通过、REJECTED 已驳回。";

    String PRICE_REPORT_AUDIT_STATUS =
            "价格申报审核结果：APPROVED 通过、REJECTED 驳回。";

    String ENABLE_DISABLE_STATUS =
            "状态：ENABLE 启用、DISABLE 停用。";

    String REVIEW_STATUS =
            "评价状态：ENABLE 正常展示、DISABLE 隐藏/停用。";

    String MATCH_RECORD_STATUS =
            "匹配记录状态：PUSHED 已推送、ACCEPTED 已接单；其他状态按抢单链路扩展，例如 EXPIRED 已过期。";

    String RISK_RULE_VALUE_TYPE =
            "风控规则值类型：INTEGER 整数、DECIMAL 小数、FEN 金额分。";

    String SENSITIVE_WORD_TYPE =
            "词库类型：ILLEGAL 违法违规、RISK 交易风险、CONTACT 联系方式。";

    String SENSITIVE_MATCH_TYPE =
            "匹配方式：CONTAINS 包含匹配；其他取值按敏感词引擎扩展。";

    String SENSITIVE_BLOCK_LEVEL =
            "拦截级别：BLOCK 直接拦截、REVIEW 转人工复核。";

    String USER_STATUS =
            "用户状态：ENABLE 启用、DISABLE 停用。";

    String MERCHANT_STATUS =
            "服务商状态：ENABLE 启用、DISABLE 停用。";

    String MERCHANT_ACCEPT_STATUS =
            "接单状态：ENABLE 可接单、DISABLE 暂停接单。";

    String SERVICE_POINT_STATUS =
            "服务点状态：ENABLE 启用、DISABLE 停用。";

    String WALLET_STATUS =
            "钱包状态：ENABLE 启用、DISABLE 停用。";

    String BANK_CARD_STATUS =
            "银行卡状态：ENABLE 启用、DISABLE 停用。";

    String CREDIT_RULE_STATUS =
            "信用规则状态：ENABLE 启用、DISABLE 停用。";

    String MESSAGE_TEMPLATE_TYPE =
            "模板类型：BIZ 业务消息模板、MARKETING 营销消息模板。";

    String MESSAGE_CHANNEL_TYPE =
            "渠道类型：APP_POPUP 站内消息/小程序弹窗、WECHAT_MP_TEMPLATE 公众号/小程序模板消息、SMS 短信、APP_VOICE App 语音朗读。";

    String MESSAGE_CATEGORY =
            "消息分类：SYSTEM 系统类、FINANCE 金额类、ORDER 订单类、COMPLIANCE 合规类、DISPUTE 纠纷类、MARKETING 营销类、"
                    + "MEETING_NOTICE 会议通知、SUPERIOR_INSTRUCTION 上级指令。";

    String MESSAGE_SCENE_STATUS =
            "场景状态：ENABLE 启用、DISABLE 停用。";

    String MESSAGE_SEND_STATUS =
            "发送状态：SUCCESS 发送成功、FAILED 发送失败、PENDING 待发送。";

    String MESSAGE_PUSH_TASK_STATUS =
            "推送任务状态：PENDING 待执行、PROCESSING 处理中、SUCCESS 全部发送成功、PARTIAL_FAILED 部分发送失败、FAILED 全部发送失败、CANCELLED 已取消。";

    String MESSAGE_TARGET_SCOPE =
            "推送范围：SINGLE_USER 单用户；如批量推送、多角色推送等场景可按消息任务范围字典扩展。";

    String MESSAGE_READ_STATUS =
            "已读状态：UNREAD 未读、READ 已读。";

    String MESSAGE_CAMPAIGN_SOURCE_TYPE =
            "投放来源：USER_DIRECTED 用户定向申请、ADMIN_DIRECTED 管理后台定向投放、SYSTEM_TRIGGER 系统触发、AD 广告投放。";

    String MESSAGE_CAMPAIGN_AUDIT_STATUS =
            "投放审核状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回、CANCELLED 已取消。";

    String MESSAGE_TARGET_MODE =
            "目标模式：FULL_PLATFORM 全平台、JURISDICTION 辖区、CUSTOM_FILTER 自定义筛选。";

    String RISK_EVENT_STATUS =
            "风险事件处理状态：PENDING 待处理、PROCESSING 处理中、FINISHED 已完结。";

    String RISK_EVENT_DISPOSE_STATUS =
            "风险处置状态：PENDING 待复核、PROCESSING 处理中、FINISHED 已完成、RELEASED 已解除。";

    String RISK_EVENT_DISPOSE_ACTION =
            "风险处置动作：CONFIRM_VIOLATION 确认违规、RELEASE_FALSE_POSITIVE 解除误判、FREEZE_FUNDS 冻结资金、UNFREEZE_FUNDS 解冻资金、RESTRICT_PUBLISH 限制发单、RESTRICT_ACCEPT 限制接单。";

    String RISK_LEVEL =
            "风险等级，按平台风控等级字典展示，常见值如 LOW 低风险、MEDIUM 中风险、HIGH 高风险。";

    String ORDER_ABNORMAL_HANDLE_STATUS =
            "异常单处理状态：PENDING 待处理、PROCESSING 处理中、FINISHED 已完结。";

    String ORDER_ABNORMAL_FINAL_AUDIT_STATUS =
            "异常单终审状态：PENDING 待终审、APPROVED 终审通过、REJECTED 终审驳回。";

    String BLACKLIST_TYPE =
            "黑名单类型，按平台黑名单字典展示，常见值如 RISK 风控拉黑。";

    String USER_RESTRICT_STATUS =
            "用户限制状态：ACTIVE 生效中、RELEASED 已解除、EXPIRED 已过期。";

    String PARTNER_COORDINATION_STATUS =
            "合作商协调状态：INITIATED 已发起、PROCESSING 协调中、ESCALATED 已升级平台终审、FINISHED 已结束。";

    String COMMISSION_TYPE =
            "佣金类型，按推广佣金字典展示，当前常见值如 ORDER 订单佣金。";

    String COMMISSION_STATUS =
            "佣金状态，按推广佣金状态字典展示，常见值如 PENDING 待结算、SETTLED 已结算、INVALID 已失效。";

    String HELP_FEEDBACK_TYPE =
            "反馈分类，按平台帮助反馈分类字典展示，例如 功能建议、异常反馈、投诉建议。";

    String HELP_FEEDBACK_STATUS =
            "反馈处理状态，按帮助反馈状态字典展示，常见值如 PENDING 待处理、PROCESSING 处理中、FINISHED 已完结。";

    String PARTNER_STATUS =
            "合作商状态：ENABLE 启用、DISABLE 停用。";

    String REMINDER_TYPE =
            "提醒类型：BIRTHDAY 生日提醒、QUALIFICATION_EXPIRY 证件到期提醒、CUSTOM_EVENT 自定义事件提醒。";

    String REMINDER_STATUS =
            "提醒状态：ACTIVE 生效中、TRIGGERED 已触发、DISABLED 已停用、EXPIRED 已过期。";

    String REMINDER_REPEAT_TYPE =
            "提醒重复规则：NONE 单次提醒、YEARLY 每年提醒。生日提醒默认 YEARLY。";

    String BENEFIT_TYPE =
            "权益类型：CREDIT_LEVEL 信用等级权益、POINT 积分权益、COUPON 优惠券权益、PRIORITY_DISPLAY 优先展示权益、"
                    + "PROMOTER 推广身份权益、PRIORITY_DISPATCH 优先派单权益。";
}
