package cn.iocoder.yudao.module.linbang.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * LinBang 错误码枚举类
 *
 * linbang 邻里互助，使用 1-099-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== MEMBER 模块 1-099-001-000 ==========
    ErrorCode MEMBER_USER_NOT_EXISTS = new ErrorCode(1_099_001_000, "邻里用户不存在");
    ErrorCode MEMBER_USER_ADDRESS_NOT_EXISTS = new ErrorCode(1_099_001_001, "用户地址不存在");
    ErrorCode MEMBER_USER_REAL_NAME_NOT_EXISTS = new ErrorCode(1_099_001_002, "实名认证记录不存在");
    ErrorCode MEMBER_USER_SOURCE_NOT_EXISTS = new ErrorCode(1_099_001_003, "会员基础账号不存在");
    ErrorCode MEMBER_USER_DISABLED = new ErrorCode(1_099_001_004, "邻里用户已被禁用");
    ErrorCode MEMBER_USER_QUALIFICATION_NOT_EXISTS = new ErrorCode(1_099_001_005, "用户资质不存在");
    ErrorCode CERT_EXEMPTION_APPLY_NOT_EXISTS = new ErrorCode(1_099_001_006, "证件豁免申请不存在");
    ErrorCode MEMBER_ROLE_APPLY_NOT_EXISTS = new ErrorCode(1_099_001_007, "身份申请不存在");
    ErrorCode MEMBER_ROLE_APPLY_AUDIT_STATUS_INVALID = new ErrorCode(1_099_001_008, "当前身份申请状态不允许审核");
    ErrorCode MEMBER_QUALIFICATION_EXPIRED = new ErrorCode(1_099_001_009, "服务资质已过期，暂不允许接单");
    ErrorCode MEMBER_ROLE_APPLY_ROLE_CODE_INVALID = new ErrorCode(1_099_001_010, "当前申请角色不受支持");
    ErrorCode MEMBER_ROLE_APPLY_PENDING_EXISTS = new ErrorCode(1_099_001_011, "当前角色已有待审核申请，请勿重复提交");
    ErrorCode USER_REMINDER_NOT_EXISTS = new ErrorCode(1_099_001_012, "用户提醒不存在");
    ErrorCode USER_REMINDER_TYPE_INVALID = new ErrorCode(1_099_001_013, "当前提醒类型不支持创建或修改");
    ErrorCode USER_REMINDER_EVENT_TIME_INVALID = new ErrorCode(1_099_001_014, "提醒时间不合法");
    ErrorCode MEMBER_USER_MOBILE_DUPLICATED = new ErrorCode(1_099_001_015, "手机号已被注册");
    ErrorCode MEMBER_USER_USERNAME_DUPLICATED = new ErrorCode(1_099_001_016, "用户名已被占用");
    ErrorCode MEMBER_USER_PASSWORD_INVALID = new ErrorCode(1_099_001_017, "账号或密码错误");
    ErrorCode MEMBER_USER_REGISTER_AGREEMENT_REQUIRED = new ErrorCode(1_099_001_018, "请先确认注册协议");
    ErrorCode MEMBER_USER_ACCOUNT_TYPE_INVALID = new ErrorCode(1_099_001_019, "当前账户类型不受支持");
    ErrorCode MEMBER_USER_BUSINESS_LICENSE_REQUIRED = new ErrorCode(1_099_001_020, "企业注册必须上传营业执照");
    ErrorCode MEMBER_ROLE_SWITCH_NOT_ALLOWED = new ErrorCode(1_099_001_021, "当前角色尚未开通或不允许切换");

    // ========== MERCHANT 模块 1-099-002-000 ==========
    ErrorCode MERCHANT_SERVICE_CATEGORY_NOT_EXISTS = new ErrorCode(1_099_002_000, "服务类目不存在");
    ErrorCode MERCHANT_ENTRY_NOT_EXISTS = new ErrorCode(1_099_002_001, "服务商入驻申请不存在");
    ErrorCode MERCHANT_INFO_NOT_EXISTS = new ErrorCode(1_099_002_002, "服务商信息不存在");
    ErrorCode MERCHANT_AUTH_REQUIRED = new ErrorCode(1_099_002_003, "当前账号未开通服务商身份");
    ErrorCode MERCHANT_SERVICE_POINT_NOT_EXISTS = new ErrorCode(1_099_002_004, "服务点不存在");
    ErrorCode MERCHANT_PRICE_REPORT_AUDIT_STATUS_INVALID = new ErrorCode(1_099_002_005, "当前价格申报状态不允许审核");
    ErrorCode MERCHANT_SERVICE_CATEGORY_PARENT_NOT_EXISTS = new ErrorCode(1_099_002_006, "上级服务类目不存在");
    ErrorCode MERCHANT_SERVICE_CATEGORY_PARENT_INVALID = new ErrorCode(1_099_002_007, "上级服务类目设置不合法");
    ErrorCode MERCHANT_SERVICE_CATEGORY_HAS_CHILDREN = new ErrorCode(1_099_002_008, "当前服务类目下仍存在子类目，无法删除");
    ErrorCode MERCHANT_SERVICE_CATEGORY_IN_USE = new ErrorCode(1_099_002_009, "当前服务类目已关联服务商，无法删除");
    ErrorCode MERCHANT_SUB_ACCOUNT_DISABLED = new ErrorCode(1_099_002_010, "当前子账号已被禁用");
    ErrorCode MERCHANT_SUB_ACCOUNT_PERMISSION_DENIED = new ErrorCode(1_099_002_011, "当前子账号无权执行该操作");

    // ========== RISK 模块 1-099-003-000 ==========
    ErrorCode RISK_RULE_NOT_EXISTS = new ErrorCode(1_099_003_000, "风控规则不存在");
    ErrorCode SENSITIVE_WORD_NOT_EXISTS = new ErrorCode(1_099_003_001, "敏感词不存在");
    ErrorCode RISK_EVENT_NOT_EXISTS = new ErrorCode(1_099_003_002, "风险事件不存在");
    ErrorCode BLACKLIST_NOT_EXISTS = new ErrorCode(1_099_003_003, "黑名单记录不存在");
    ErrorCode USER_RESTRICT_RECORD_NOT_EXISTS = new ErrorCode(1_099_003_004, "用户限制记录不存在");
    ErrorCode USER_FROZEN_FUND_RECORD_NOT_EXISTS = new ErrorCode(1_099_003_005, "冻结资金记录不存在");
    ErrorCode USER_DEVICE_NOT_EXISTS = new ErrorCode(1_099_003_006, "用户设备记录不存在");
    ErrorCode USER_RISK_RELATION_NOT_EXISTS = new ErrorCode(1_099_003_007, "关联账号记录不存在");
    ErrorCode SENSITIVE_HIT_LOG_NOT_EXISTS = new ErrorCode(1_099_003_008, "敏感词命中记录不存在");
    ErrorCode RISK_EVENT_DISPOSE_STATUS_INVALID = new ErrorCode(1_099_003_009, "当前风险事件状态不允许处理");
    ErrorCode PUNISH_LOG_NOT_EXISTS = new ErrorCode(1_099_003_010, "处罚执行日志不存在");

    // ========== ORDER 模块 1-099-004-000 ==========
    ErrorCode ORDER_INFO_NOT_EXISTS = new ErrorCode(1_099_004_000, "订单不存在");
    ErrorCode ORDER_UNIT_NOT_EXISTS = new ErrorCode(1_099_004_001, "订单单元不存在");
    ErrorCode ORDER_ABNORMAL_NOT_EXISTS = new ErrorCode(1_099_004_002, "异常订单不存在");
    ErrorCode ORDER_ACCEPT_RECORD_NOT_EXISTS = new ErrorCode(1_099_004_003, "抢单记录不存在");
    ErrorCode ORDER_MATCH_RECORD_NOT_EXISTS = new ErrorCode(1_099_004_004, "匹配记录不存在");
    ErrorCode ORDER_CATEGORY_DISABLED = new ErrorCode(1_099_004_005, "服务类目不可用");
    ErrorCode ORDER_ADDRESS_INVALID = new ErrorCode(1_099_004_006, "订单地址不存在或无权使用");
    ErrorCode ORDER_AGREEMENT_NOT_CONFIRMED = new ErrorCode(1_099_004_007, "请先确认服务协议");
    ErrorCode ORDER_STATUS_NOT_ALLOW_ACCEPT = new ErrorCode(1_099_004_008, "当前订单状态不允许接单");
    ErrorCode ORDER_UNIT_LOCKED = new ErrorCode(1_099_004_009, "当前单元仍处于锁定状态");
    ErrorCode ORDER_UNIT_ACCEPTED_BY_OTHER = new ErrorCode(1_099_004_010, "当前单元已被其他服务商接单");
    ErrorCode ORDER_ACCESS_DENIED = new ErrorCode(1_099_004_011, "无权访问当前订单");
    ErrorCode ORDER_UNIT_ACCESS_DENIED = new ErrorCode(1_099_004_012, "无权操作当前订单单元");
    ErrorCode ORDER_PAY_ORDER_NOT_EXISTS = new ErrorCode(1_099_004_013, "订单支付单不存在");
    ErrorCode ORDER_REFUND_STATUS_NOT_ALLOWED = new ErrorCode(1_099_004_014, "当前订单状态不允许退款");
    ErrorCode ORDER_REFUND_AMOUNT_INVALID = new ErrorCode(1_099_004_015, "退款金额不合法");
    ErrorCode ORDER_REFUND_AMOUNT_EXCEED = new ErrorCode(1_099_004_016, "退款金额超过可退范围");
    ErrorCode ORDER_REFUND_NOTIFY_INVALID = new ErrorCode(1_099_004_017, "退款回调数据不合法");
    ErrorCode ORDER_PAY_STATUS_NOT_ALLOWED = new ErrorCode(1_099_004_018, "当前订单状态不允许创建或回写支付单");
    ErrorCode ORDER_PAY_CALLBACK_INVALID = new ErrorCode(1_099_004_019, "支付回调数据不合法");
    ErrorCode ORDER_PAY_ORDER_ALREADY_EXISTS = new ErrorCode(1_099_004_020, "订单已存在支付单");
    ErrorCode ORDER_STATUS_NOT_ALLOW_CANCEL = new ErrorCode(1_099_004_021, "当前订单状态不允许取消");
    ErrorCode ORDER_STATUS_NOT_ALLOW_CONFIRM = new ErrorCode(1_099_004_022, "当前订单状态不允许确认完成");
    ErrorCode ORDER_ANTI_ESCAPE_NOT_CONFIRMED = new ErrorCode(1_099_004_023, "请先确认防逃单提醒");
    ErrorCode ORDER_SPLIT_RULE_NOT_EXISTS = new ErrorCode(1_099_004_024, "订单拆单规则不存在");
    ErrorCode ORDER_SPLIT_PLAN_GENERATE_FAILED = new ErrorCode(1_099_004_025, "订单拆单方案生成失败");
    ErrorCode ORDER_UNIT_AMOUNT_EXCEED_LIMIT = new ErrorCode(1_099_004_026, "拆分后单元金额超过上限");
    ErrorCode ORDER_APPEAL_EXPIRED = new ErrorCode(1_099_004_027, "当前单元已超过申诉时效");
    ErrorCode ORDER_PUBLISH_RESTRICTED = new ErrorCode(1_099_004_028, "当前账号已被限制发单");
    ErrorCode ORDER_ACCEPT_RESTRICTED = new ErrorCode(1_099_004_029, "当前账号已被限制接单");
    ErrorCode ORDER_DEPOSIT_REQUIRED = new ErrorCode(1_099_004_030, "当前订单需先缴纳保证金");
    ErrorCode ORDER_DEPOSIT_PAY_STATUS_INVALID = new ErrorCode(1_099_004_031, "当前保证金状态不允许支付");
    ErrorCode ORDER_SELF_DEAL_BLOCKED = new ErrorCode(1_099_004_032, "禁止自接自单");
    ErrorCode ORDER_ACCOUNT_BLOCKED = new ErrorCode(1_099_004_033, "当前账号存在风控限制");
    ErrorCode ORDER_REAL_NAME_REQUIRED = new ErrorCode(1_099_004_034, "请先完成实名认证");
    ErrorCode ORDER_STATUS_NOT_ALLOW_UPDATE = new ErrorCode(1_099_004_035, "当前订单状态不允许修改");
    ErrorCode ORDER_VERIFY_CODE_INVALID = new ErrorCode(1_099_004_036, "核销码不存在或不匹配");
    ErrorCode ORDER_VERIFY_STATUS_INVALID = new ErrorCode(1_099_004_037, "当前单元状态不允许核销");
    ErrorCode ORDER_VERIFY_ALREADY_FINISHED = new ErrorCode(1_099_004_038, "当前单元已完成核销，请勿重复操作");
    ErrorCode ORDER_VERIFY_ACCESS_DENIED = new ErrorCode(1_099_004_039, "无权查看或操作当前核销信息");
    ErrorCode ORDER_PRICING_MODE_NOT_SUPPORTED = new ErrorCode(1_099_004_040, "当前服务类目不支持该计价方式");
    ErrorCode ORDER_INVOICE_NOT_SUPPORTED = new ErrorCode(1_099_004_041, "当前服务类目不支持开票");
    ErrorCode ORDER_PROJECT_ESCROW_AGREEMENT_REQUIRED = new ErrorCode(1_099_004_042, "当前用工类需求必须勾选工程托管协议");
    ErrorCode ORDER_PREVIEW_TOKEN_INVALID = new ErrorCode(1_099_004_043, "当前预览快照已失效，请重新预览后提交");
    ErrorCode ORDER_SPLIT_NOT_SUPPORTED = new ErrorCode(1_099_004_044, "当前服务类目不支持拆分");

    // ========== WALLET 模块 1-099-005-000 ==========
    ErrorCode WALLET_ACCOUNT_NOT_EXISTS = new ErrorCode(1_099_005_000, "钱包账户不存在");
    ErrorCode WALLET_FLOW_NOT_EXISTS = new ErrorCode(1_099_005_001, "钱包流水不存在");
    ErrorCode WALLET_WITHDRAW_NOT_EXISTS = new ErrorCode(1_099_005_002, "提现申请不存在");
    ErrorCode WALLET_BANK_CARD_NOT_EXISTS = new ErrorCode(1_099_005_003, "银行卡不存在");
    ErrorCode DIVIDE_RULE_NOT_EXISTS = new ErrorCode(1_099_005_004, "分账规则不存在");
    ErrorCode WALLET_BANK_CARD_INVALID = new ErrorCode(1_099_005_005, "银行卡不存在或无权使用");
    ErrorCode WALLET_AVAILABLE_AMOUNT_NOT_ENOUGH = new ErrorCode(1_099_005_006, "钱包可提现余额不足");
    ErrorCode WALLET_WITHDRAW_AMOUNT_INVALID = new ErrorCode(1_099_005_007, "提现金额不合法");

    // ========== REVIEW 模块 1-099-006-000 ==========
    ErrorCode COMPLAINT_NOT_EXISTS = new ErrorCode(1_099_006_000, "投诉单不存在");
    ErrorCode APPEAL_NOT_EXISTS = new ErrorCode(1_099_006_001, "申诉单不存在");
    ErrorCode REVIEW_COMMENT_NOT_EXISTS = new ErrorCode(1_099_006_002, "评价记录不存在");
    ErrorCode CREDIT_RULE_NOT_EXISTS = new ErrorCode(1_099_006_003, "信用规则不存在");
    ErrorCode REVIEW_ACCESS_DENIED = new ErrorCode(1_099_006_004, "无权提交当前售后或评价数据");
    ErrorCode CREDIT_RECORD_NOT_EXISTS = new ErrorCode(1_099_006_005, "信用记录不存在");
    ErrorCode REVIEW_UNIT_STATUS_INVALID = new ErrorCode(1_099_006_006, "当前单元状态不允许评价");
    ErrorCode REVIEW_DUPLICATED = new ErrorCode(1_099_006_007, "当前单元已提交过评价");
    ErrorCode REVIEW_EDIT_NOT_ALLOWED = new ErrorCode(1_099_006_008, "当前评价不允许修改");
    ErrorCode REVIEW_EDIT_EXPIRED = new ErrorCode(1_099_006_009, "评价可编辑时间已过");

    // ========== PROMOTE / PARTNER / MESSAGE 模块 1-099-007-000 ==========
    ErrorCode PROMOTER_NOT_EXISTS = new ErrorCode(1_099_007_000, "推广员不存在");
    ErrorCode COMMISSION_ORDER_NOT_EXISTS = new ErrorCode(1_099_007_001, "佣金单不存在");
    ErrorCode PARTNER_INFO_NOT_EXISTS = new ErrorCode(1_099_007_002, "区域合作商不存在");
    ErrorCode MERCHANT_PRICE_REPORT_NOT_EXISTS = new ErrorCode(1_099_007_003, "价格申报记录不存在");
    ErrorCode MESSAGE_TEMPLATE_NOT_EXISTS = new ErrorCode(1_099_007_004, "消息模板不存在");
    ErrorCode MESSAGE_RECORD_NOT_EXISTS = new ErrorCode(1_099_007_005, "消息记录不存在");
    ErrorCode HELP_FEEDBACK_NOT_EXISTS = new ErrorCode(1_099_007_006, "帮助反馈不存在");
    ErrorCode PLATFORM_CONFIG_NOT_EXISTS = new ErrorCode(1_099_007_007, "平台配置不存在");
    ErrorCode PLATFORM_CONFIG_CATEGORY_INVALID = new ErrorCode(1_099_007_008, "平台配置分类不受支持");
    ErrorCode MESSAGE_PUSH_TASK_NOT_EXISTS = new ErrorCode(1_099_007_009, "消息推送任务不存在");
    ErrorCode ADMIN_DYNAMIC_KEY_INVALID = new ErrorCode(1_099_007_010, "动态密钥校验失败");
    ErrorCode ADMIN_DYNAMIC_KEY_REQUIRED = new ErrorCode(1_099_007_011, "敏感操作需要先完成动态密钥校验");
    ErrorCode MESSAGE_SEND_CONTENT_BLOCKED = new ErrorCode(1_099_007_012, "消息内容包含敏感信息，已被拦截");
    ErrorCode PROMOTE_CONTENT_NOT_EXISTS = new ErrorCode(1_099_007_013, "推广内容不存在");
    ErrorCode PROMOTE_CONTENT_STATUS_INVALID = new ErrorCode(1_099_007_014, "当前推广内容状态不允许执行该操作");
    ErrorCode PROMOTE_APPEAL_NOT_EXISTS = new ErrorCode(1_099_007_015, "推广申诉不存在");
    ErrorCode PROMOTE_APPEAL_STATUS_INVALID = new ErrorCode(1_099_007_016, "当前推广申诉状态不允许执行该操作");
    ErrorCode PROMOTER_PENALTY_RECORD_NOT_EXISTS = new ErrorCode(1_099_007_017, "推广员处罚记录不存在");
    ErrorCode MESSAGE_SCENE_NOT_EXISTS = new ErrorCode(1_099_007_018, "消息场景不存在");
    ErrorCode MESSAGE_CAMPAIGN_NOT_EXISTS = new ErrorCode(1_099_007_019, "消息投放活动不存在");
    ErrorCode MESSAGE_CAMPAIGN_AUDIT_STATUS_INVALID = new ErrorCode(1_099_007_020, "当前投放活动状态不允许审核");
    ErrorCode MESSAGE_CAMPAIGN_EXECUTE_STATUS_INVALID = new ErrorCode(1_099_007_021, "当前投放活动状态不允许执行");
    ErrorCode MESSAGE_CAMPAIGN_CANCEL_STATUS_INVALID = new ErrorCode(1_099_007_022, "当前投放活动状态不允许取消");
    ErrorCode MESSAGE_FINANCE_SMS_REQUIRED = new ErrorCode(1_099_007_023, "金额变动类消息必须包含短信通道");
    ErrorCode APP_MESSAGE_SETTING_NOT_EXISTS = new ErrorCode(1_099_007_024, "消息偏好设置不存在");
    ErrorCode MESSAGE_FEEDBACK_STAT_NOT_EXISTS = new ErrorCode(1_099_007_025, "消息反馈统计不存在");
    ErrorCode MESSAGE_OPTIMIZATION_NOT_EXISTS = new ErrorCode(1_099_007_026, "消息优化记录不存在");
    ErrorCode MESSAGE_CAMPAIGN_DELIVERY_TIME_INVALID = new ErrorCode(1_099_007_027, "当前不在允许的投放时段内");
    ErrorCode MESSAGE_PUSH_TASK_RETRY_INVALID = new ErrorCode(1_099_007_028, "当前推送任务不允许重试");
    ErrorCode PARTNER_REGION_ACCESS_DENIED = new ErrorCode(1_099_007_029, "无权访问当前辖区数据");
    ErrorCode PARTNER_ENTRY_AUDIT_STATUS_INVALID = new ErrorCode(1_099_007_030, "当前入驻申请状态不允许合作商初审");
    ErrorCode PARTNER_PRICE_REPORT_STATUS_INVALID = new ErrorCode(1_099_007_031, "当前价格申报状态不允许执行该操作");
    ErrorCode PARTNER_COORDINATION_NOT_EXISTS = new ErrorCode(1_099_007_032, "合作商协调记录不存在");
    ErrorCode PARTNER_ROLE_REQUIRED = new ErrorCode(1_099_007_033, "当前账号未开通区域合作商身份");
    ErrorCode MEMBER_USER_RESTRICT_STATUS_INVALID = new ErrorCode(1_099_007_034, "当前用户限制状态不允许执行该操作");

    // ========== CONTENT / SENSITIVE 模块 1-099-009-000 ==========
    ErrorCode CONTENT_SENSITIVE_BLOCKED = new ErrorCode(1_099_009_000, "内容包含敏感信息，已被拦截");
    ErrorCode CONTENT_SENSITIVE_REVIEW_REQUIRED = new ErrorCode(1_099_009_001, "内容命中敏感规则，需人工审核");
    ErrorCode CONTENT_OCR_FAILED = new ErrorCode(1_099_009_002, "图片敏感识别失败，请稍后重试");
    ErrorCode USER_SENSITIVE_CUSTOM_WORD_NOT_EXISTS = new ErrorCode(1_099_009_003, "用户自定义脱敏内容不存在");

    // ========== MAP 模块 1-099-008-000 ==========
    ErrorCode MAP_KEY_NOT_CONFIGURED = new ErrorCode(1_099_008_000, "高德地图 Web Service Key 未配置");
    ErrorCode MAP_REQUEST_FAILED = new ErrorCode(1_099_008_001, "高德地图请求失败：{}");
    ErrorCode MAP_LOCATION_INVALID = new ErrorCode(1_099_008_002, "位置信息不完整，无法解析地图地址");
    ErrorCode MAP_ADDRESS_RESOLVE_FAILED = new ErrorCode(1_099_008_003, "地址解析失败：{}");

}
