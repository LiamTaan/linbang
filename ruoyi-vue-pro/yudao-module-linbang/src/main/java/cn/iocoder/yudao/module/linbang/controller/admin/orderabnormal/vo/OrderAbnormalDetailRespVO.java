package cn.iocoder.yudao.module.linbang.controller.admin.orderabnormal.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 异常订单详情 Response VO")
@Data
public class OrderAbnormalDetailRespVO {

    @Schema(description = "异常单 ID", example = "1")
    private Long id;
    @Schema(description = "主订单 ID", example = "2001")
    private Long orderId;
    @Schema(description = "单元订单 ID", example = "3001")
    private Long unitId;
    @Schema(description = "异常单号", example = "AB202606260001")
    private String abnormalNo;
    @Schema(description = "异常类型，按异常单业务字典展示", example = "ORDER_TIMEOUT")
    private String abnormalType;
    @Schema(description = OpenApiSchemaConstants.RISK_LEVEL, example = "HIGH")
    private String riskLevel;
    @Schema(description = "命中规则编码", example = "ORDER_TIMEOUT_LIMIT")
    private String hitRuleCode;
    @Schema(description = OpenApiSchemaConstants.ORDER_ABNORMAL_HANDLE_STATUS, example = "PENDING")
    private String handleStatus;
    @Schema(description = "处理人 ID", example = "1")
    private Long handleBy;
    @Schema(description = "处理时间")
    private LocalDateTime handleTime;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = OpenApiSchemaConstants.ORDER_ABNORMAL_FINAL_AUDIT_STATUS, example = "APPROVED")
    private String finalAuditStatus;
    @Schema(description = "终审人 ID", example = "1")
    private Long finalAuditBy;
    @Schema(description = "终审时间")
    private LocalDateTime finalAuditTime;
    @Schema(description = "终审意见")
    private String finalAuditRemark;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "主订单摘要")
    private OrderSimpleRespVO order;
    @Schema(description = "单元订单摘要")
    private OrderUnitSimpleRespVO unit;
    @Schema(description = "命中风控规则摘要")
    private RiskRuleSimpleRespVO hitRule;
    @Schema(description = "异常相关操作日志")
    private List<OrderOperateLogRespVO> operateLogs;

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
        @Schema(description = "接单服务商 ID", example = "8001")
        private Long merchantId;
    }

    @Data
    public static class OrderUnitSimpleRespVO {
        @Schema(description = "单元订单 ID", example = "3001")
        private Long id;
        @Schema(description = "单元编号", example = "UNIT202606260001")
        private String unitNo;
        @Schema(description = "单元序号", example = "1")
        private Integer unitSeq;
        @Schema(description = "单元标题", example = "上门清洗第一单元")
        private String unitTitle;
        @Schema(description = OpenApiSchemaConstants.ORDER_UNIT_STATUS, example = "SERVING")
        private String status;
        @Schema(description = "是否已锁单", example = "true")
        private Boolean isLocked;
        @Schema(description = "锁单原因")
        private String lockReason;
        @Schema(description = "接单服务商 ID", example = "8001")
        private Long merchantId;
    }

    @Data
    public static class RiskRuleSimpleRespVO {
        @Schema(description = "规则 ID", example = "310001")
        private Long id;
        @Schema(description = "规则编码", example = "ORDER_TIMEOUT_LIMIT")
        private String ruleCode;
        @Schema(description = "规则名称", example = "订单超时风控")
        private String ruleName;
        @Schema(description = "规则分组", example = "ORDER")
        private String ruleGroup;
        @Schema(description = "规则值", example = "30")
        private String ruleValue;
        @Schema(description = OpenApiSchemaConstants.RISK_RULE_VALUE_TYPE, example = "INTEGER")
        private String valueType;
        @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = "备注")
        private String remark;
    }

    @Data
    public static class OrderOperateLogRespVO {
        @Schema(description = "日志 ID", example = "1")
        private Long id;
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
        @Schema(description = "操作后状态", example = "APPEALING")
        private String afterStatus;
        @Schema(description = "备注")
        private String remark;
        @Schema(description = "操作时间")
        private LocalDateTime operateTime;
    }

}
