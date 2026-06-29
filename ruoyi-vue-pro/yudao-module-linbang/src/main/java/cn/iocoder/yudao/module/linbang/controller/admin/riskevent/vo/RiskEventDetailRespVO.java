package cn.iocoder.yudao.module.linbang.controller.admin.riskevent.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 风险事件详情 Response VO")
@Data
public class RiskEventDetailRespVO {

    @Schema(description = "风险事件 ID", example = "1")
    private Long id;
    @Schema(description = OpenApiSchemaConstants.CREDIT_BIZ_TYPE, example = "ORDER")
    private String bizType;
    @Schema(description = "业务 ID，指向被命中的订单、单元、投诉、申诉、提现等主键", example = "2001")
    private Long bizId;
    @Schema(description = "风险类型，按平台风控事件类型字典展示", example = "ORDER_LIMIT")
    private String riskType;
    @Schema(description = OpenApiSchemaConstants.RISK_LEVEL, example = "HIGH")
    private String riskLevel;
    @Schema(description = "命中规则编码", example = "ORDER_DAILY_CREATE_LIMIT")
    private String hitRuleCode;
    @Schema(description = OpenApiSchemaConstants.RISK_EVENT_STATUS, example = "PENDING")
    private String status;
    @Schema(description = OpenApiSchemaConstants.RISK_EVENT_DISPOSE_STATUS, example = "PENDING")
    private String disposeStatus;
    @Schema(description = OpenApiSchemaConstants.RISK_EVENT_DISPOSE_ACTION, example = "CONFIRM_VIOLATION")
    private String disposeAction;
    @Schema(description = "处理人 ID", example = "1")
    private Long handleBy;
    @Schema(description = "处理时间")
    private LocalDateTime handleTime;
    @Schema(description = "处理备注")
    private String remark;
    @Schema(description = "处置备注")
    private String disposeRemark;
    @Schema(description = "关联用户 ID 列表，逗号分隔", example = "1001,2002")
    private String relatedUserIds;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "命中规则摘要")
    private RiskRuleSimpleRespVO hitRule;
    @Schema(description = "关联主订单摘要；当风险事件命中主订单时返回")
    private OrderSimpleRespVO order;
    @Schema(description = "关联单元订单摘要；当风险事件命中单元订单时返回")
    private OrderUnitSimpleRespVO unit;
    @Schema(description = "关联异常单摘要")
    private OrderAbnormalSimpleRespVO abnormal;
    @Schema(description = "关联投诉摘要")
    private ComplaintSimpleRespVO complaint;
    @Schema(description = "关联申诉摘要")
    private AppealSimpleRespVO appeal;
    @Schema(description = "关联提现摘要")
    private WithdrawSimpleRespVO withdraw;
    @Schema(description = "关联订单操作日志")
    private List<OrderOperateLogRespVO> orderOperateLogs;

    @Data
    public static class RiskRuleSimpleRespVO {
        @Schema(description = "规则 ID", example = "310001")
        private Long id;
        @Schema(description = "规则编码", example = "ORDER_DAILY_CREATE_LIMIT")
        private String ruleCode;
        @Schema(description = "规则名称", example = "用户单日发单次数上限")
        private String ruleName;
        @Schema(description = "规则分组", example = "ORDER")
        private String ruleGroup;
        @Schema(description = "规则值", example = "20")
        private String ruleValue;
        @Schema(description = OpenApiSchemaConstants.RISK_RULE_VALUE_TYPE, example = "INTEGER")
        private String valueType;
        @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = "备注")
        private String remark;
    }

    @Data
    public static class OrderSimpleRespVO {
        @Schema(description = "主订单 ID", example = "2001")
        private Long id;
        @Schema(description = "订单编号", example = "LB202606260001")
        private String orderNo;
        @Schema(description = OpenApiSchemaConstants.ORDER_STATUS, example = "SERVING")
        private String status;
        @Schema(description = "下单用户 ID", example = "5001")
        private Long userId;
        @Schema(description = "下单用户编号", example = "LBU123456")
        private String userNo;
        @Schema(description = "下单用户昵称", example = "邻里用户")
        private String userNickname;
        @Schema(description = "下单用户手机号", example = "13800138000")
        private String userMobile;
        @Schema(description = "接单服务商 ID", example = "8001")
        private Long merchantId;
        @Schema(description = "服务商名称", example = "安心家政")
        private String merchantName;
        @Schema(description = "服务商联系人", example = "张师傅")
        private String merchantContactName;
        @Schema(description = "服务商联系电话", example = "13900139000")
        private String merchantContactMobile;
    }

    @Data
    public static class OrderUnitSimpleRespVO {
        @Schema(description = "单元订单 ID", example = "3001")
        private Long id;
        @Schema(description = "主订单 ID", example = "2001")
        private Long orderId;
        @Schema(description = "单元编号", example = "UNIT202606260001")
        private String unitNo;
        @Schema(description = "单元序号", example = "1")
        private Integer unitSeq;
        @Schema(description = "单元标题", example = "上门清洗第一单元")
        private String unitTitle;
        @Schema(description = OpenApiSchemaConstants.ORDER_UNIT_STATUS, example = "SERVING")
        private String status;
        @Schema(description = "是否已锁单", example = "false")
        private Boolean isLocked;
        @Schema(description = "锁单原因")
        private String lockReason;
        @Schema(description = "接单服务商 ID", example = "8001")
        private Long merchantId;
        @Schema(description = "服务商名称", example = "安心家政")
        private String merchantName;
        @Schema(description = "服务商联系人", example = "张师傅")
        private String merchantContactName;
        @Schema(description = "服务商联系电话", example = "13900139000")
        private String merchantContactMobile;
    }

    @Data
    public static class OrderAbnormalSimpleRespVO {
        @Schema(description = "异常单 ID", example = "1")
        private Long id;
        @Schema(description = "主订单 ID", example = "2001")
        private Long orderId;
        @Schema(description = "单元订单 ID", example = "3001")
        private Long unitId;
        @Schema(description = "异常单号", example = "AB202606260001")
        private String abnormalNo;
        @Schema(description = "异常类型", example = "ORDER_TIMEOUT")
        private String abnormalType;
        @Schema(description = OpenApiSchemaConstants.RISK_LEVEL, example = "HIGH")
        private String riskLevel;
        @Schema(description = OpenApiSchemaConstants.ORDER_ABNORMAL_HANDLE_STATUS, example = "PROCESSING")
        private String handleStatus;
        @Schema(description = "处理人 ID", example = "1")
        private Long handleBy;
        @Schema(description = "处理时间")
        private LocalDateTime handleTime;
        @Schema(description = "备注")
        private String remark;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Data
    public static class ComplaintSimpleRespVO {
        @Schema(description = "投诉 ID", example = "1")
        private Long id;
        @Schema(description = "投诉单号", example = "CP202606260001")
        private String complaintNo;
        @Schema(description = "主订单 ID", example = "2001")
        private Long orderId;
        @Schema(description = "单元订单 ID", example = "3001")
        private Long unitId;
        @Schema(description = "投诉人用户 ID", example = "5001")
        private Long complainantUserId;
        @Schema(description = "被投诉人用户 ID", example = "8001")
        private Long respondentUserId;
        @Schema(description = "投诉类型，由前端按业务字典展示")
        private String complaintType;
        @Schema(description = OpenApiSchemaConstants.COMPLAINT_STATUS, example = "PROCESSING")
        private String status;
        @Schema(description = "处理时间")
        private LocalDateTime handleTime;
        @Schema(description = "处理结果说明")
        private String resultDesc;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Data
    public static class AppealSimpleRespVO {
        @Schema(description = "申诉 ID", example = "1")
        private Long id;
        @Schema(description = "申诉单号", example = "AP202606260001")
        private String appealNo;
        @Schema(description = "主订单 ID", example = "2001")
        private Long orderId;
        @Schema(description = "单元订单 ID", example = "3001")
        private Long unitId;
        @Schema(description = "申诉用户 ID", example = "5001")
        private Long userId;
        @Schema(description = "申诉类型，由前端按业务字典展示")
        private String appealType;
        @Schema(description = OpenApiSchemaConstants.APPEAL_STATUS, example = "PROCESSING")
        private String status;
        @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "PENDING")
        private String auditStatus;
        @Schema(description = "审核人 ID", example = "1")
        private Long auditBy;
        @Schema(description = "审核时间")
        private LocalDateTime auditTime;
        @Schema(description = "审核备注")
        private String auditRemark;
        @Schema(description = "驳回原因")
        private String rejectReason;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Data
    public static class WithdrawSimpleRespVO {
        @Schema(description = "提现单 ID", example = "1")
        private Long id;
        @Schema(description = "提现单号", example = "WD202606260001")
        private String withdrawNo;
        @Schema(description = "提现用户 ID", example = "5001")
        private Long userId;
        @Schema(description = "钱包账户 ID", example = "1001")
        private Long walletAccountId;
        @Schema(description = "银行卡 ID", example = "2001")
        private Long bankCardId;
        @Schema(description = "申请提现金额", example = "100.00")
        private BigDecimal applyAmount;
        @Schema(description = "手续费金额", example = "2.00")
        private BigDecimal feeAmount;
        @Schema(description = "实际到账金额", example = "98.00")
        private BigDecimal realAmount;
        @Schema(description = OpenApiSchemaConstants.WITHDRAW_STATUS, example = "PENDING")
        private String status;
        @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "PENDING")
        private String auditStatus;
        @Schema(description = "审核人 ID", example = "1")
        private Long auditBy;
        @Schema(description = "审核时间")
        private LocalDateTime auditTime;
        @Schema(description = "审核备注")
        private String auditRemark;
        @Schema(description = "驳回原因")
        private String rejectReason;
        @Schema(description = "打款时间")
        private LocalDateTime payTime;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Data
    public static class OrderOperateLogRespVO {
        @Schema(description = "日志 ID", example = "1")
        private Long id;
        @Schema(description = "主订单 ID", example = "2001")
        private Long orderId;
        @Schema(description = "单元订单 ID", example = "3001")
        private Long unitId;
        @Schema(description = "操作类型，按订单日志字典展示", example = "LOCK")
        private String operateType;
        @Schema(description = "操作角色，按订单日志角色字典展示", example = "ADMIN")
        private String operateRole;
        @Schema(description = "操作人 ID", example = "1")
        private Long operateBy;
        @Schema(description = "操作前状态", example = "SERVING")
        private String beforeStatus;
        @Schema(description = "操作后状态", example = "AFTER_SALE")
        private String afterStatus;
        @Schema(description = "备注")
        private String remark;
        @Schema(description = "操作时间")
        private LocalDateTime operateTime;
    }

}
