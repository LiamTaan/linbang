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
    ErrorCode MEMBER_ROLE_APPLY_NOT_EXISTS = new ErrorCode(1_099_001_006, "身份申请不存在");
    ErrorCode MEMBER_ROLE_APPLY_AUDIT_STATUS_INVALID = new ErrorCode(1_099_001_007, "当前身份申请状态不允许审核");
    ErrorCode MEMBER_QUALIFICATION_EXPIRED = new ErrorCode(1_099_001_008, "服务资质已过期，暂不允许接单");

    // ========== MERCHANT 模块 1-099-002-000 ==========
    ErrorCode MERCHANT_SERVICE_CATEGORY_NOT_EXISTS = new ErrorCode(1_099_002_000, "服务类目不存在");
    ErrorCode MERCHANT_ENTRY_NOT_EXISTS = new ErrorCode(1_099_002_001, "服务商入驻申请不存在");
    ErrorCode MERCHANT_INFO_NOT_EXISTS = new ErrorCode(1_099_002_002, "服务商信息不存在");
    ErrorCode MERCHANT_AUTH_REQUIRED = new ErrorCode(1_099_002_003, "当前账号未开通服务商身份");
    ErrorCode MERCHANT_SERVICE_POINT_NOT_EXISTS = new ErrorCode(1_099_002_004, "服务点不存在");
    ErrorCode MERCHANT_PRICE_REPORT_AUDIT_STATUS_INVALID = new ErrorCode(1_099_002_005, "当前价格申报状态不允许审核");

    // ========== RISK 模块 1-099-003-000 ==========
    ErrorCode RISK_RULE_NOT_EXISTS = new ErrorCode(1_099_003_000, "风控规则不存在");
    ErrorCode SENSITIVE_WORD_NOT_EXISTS = new ErrorCode(1_099_003_001, "敏感词不存在");
    ErrorCode RISK_EVENT_NOT_EXISTS = new ErrorCode(1_099_003_002, "风险事件不存在");
    ErrorCode BLACKLIST_NOT_EXISTS = new ErrorCode(1_099_003_003, "黑名单记录不存在");

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

    // ========== MAP 模块 1-099-008-000 ==========
    ErrorCode MAP_KEY_NOT_CONFIGURED = new ErrorCode(1_099_008_000, "高德地图 Web Service Key 未配置");
    ErrorCode MAP_REQUEST_FAILED = new ErrorCode(1_099_008_001, "高德地图请求失败：{}");
    ErrorCode MAP_LOCATION_INVALID = new ErrorCode(1_099_008_002, "位置信息不完整，无法解析地图地址");
    ErrorCode MAP_ADDRESS_RESOLVE_FAILED = new ErrorCode(1_099_008_003, "地址解析失败：{}");

}
