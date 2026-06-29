package cn.iocoder.yudao.module.linbang.controller.admin.ordermatchrecord.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 订单匹配记录详情 Response VO")
@Data
public class OrderMatchRecordDetailRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;
    @Schema(description = "订单 ID", example = "1001")
    private Long orderId;
    @Schema(description = "单元 ID", example = "2001")
    private Long unitId;
    @Schema(description = "服务商 ID", example = "3001")
    private Long merchantId;
    @Schema(description = "命中规则编码", example = "MERCHANT_ACCEPT_RADIUS_KM")
    private String matchRuleCode;
    @Schema(description = "匹配分值")
    private BigDecimal matchScore;
    @Schema(description = "距离公里")
    private BigDecimal distanceKm;
    @Schema(description = "推送时间")
    private LocalDateTime pushTime;
    @Schema(description = "接单截止时间")
    private LocalDateTime acceptDeadlineTime;
    @Schema(description = OpenApiSchemaConstants.MATCH_RECORD_STATUS, example = "PUSHED")
    private String status;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "订单摘要")
    private OrderRespVO order;
    @Schema(description = "单元摘要")
    private UnitRespVO unit;
    @Schema(description = "服务商摘要")
    private MerchantRespVO merchant;
    @Schema(description = "命中规则摘要")
    private RuleRespVO rule;
    @Schema(description = "统计摘要")
    private SummaryRespVO summary;
    @Schema(description = "关联抢单记录")
    private List<AcceptRecordRespVO> acceptRecords;

    @Data
    public static class OrderRespVO {
        @Schema(description = "订单 ID", example = "1001")
        private Long id;
        @Schema(description = "订单号", example = "LBO202606260001")
        private String orderNo;
        @Schema(description = OpenApiSchemaConstants.ORDER_STATUS, example = "PENDING_ACCEPT")
        private String status;
        @Schema(description = "下单用户 ID", example = "5001")
        private Long userId;
        @Schema(description = "用户编号", example = "LBU123456")
        private String userNo;
        @Schema(description = "用户昵称", example = "邻里用户")
        private String userNickname;
        @Schema(description = "用户手机号", example = "13800138000")
        private String userMobile;
        @Schema(description = "已接单服务商 ID", example = "3001")
        private Long merchantId;
        @Schema(description = "订单金额")
        private BigDecimal orderAmount;
    }

    @Data
    public static class UnitRespVO {
        @Schema(description = "单元 ID", example = "2001")
        private Long id;
        @Schema(description = "订单 ID", example = "1001")
        private Long orderId;
        @Schema(description = "单元号", example = "LBU202606260001-1")
        private String unitNo;
        @Schema(description = "单元序号", example = "1")
        private Integer unitSeq;
        @Schema(description = "单元标题", example = "厨房油烟机清洗")
        private String unitTitle;
        @Schema(description = "单元金额")
        private BigDecimal unitAmount;
        @Schema(description = OpenApiSchemaConstants.ORDER_UNIT_STATUS, example = "PENDING_ACCEPT")
        private String status;
        @Schema(description = "是否锁单")
        private Boolean isLocked;
        @Schema(description = "锁单原因", example = "投诉处理中")
        private String lockReason;
        @Schema(description = "承接服务商 ID", example = "3001")
        private Long merchantId;
        @Schema(description = "接单截止时间")
        private LocalDateTime acceptDeadlineTime;
    }

    @Data
    public static class MerchantRespVO {
        @Schema(description = "服务商 ID", example = "3001")
        private Long id;
        @Schema(description = "服务商名称", example = "安心家政")
        private String merchantName;
        @Schema(description = "联系人", example = "李师傅")
        private String contactName;
        @Schema(description = "联系手机", example = "13800138001")
        private String contactMobile;
        @Schema(description = OpenApiSchemaConstants.MERCHANT_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = OpenApiSchemaConstants.MERCHANT_ACCEPT_STATUS, example = "ENABLE")
        private String acceptStatus;
        @Schema(description = "信用分")
        private Integer creditScore;
        @Schema(description = "信用等级", example = "NORMAL")
        private String creditLevel;
    }

    @Data
    public static class RuleRespVO {
        @Schema(description = "规则 ID", example = "310005")
        private Long id;
        @Schema(description = "规则编码", example = "MERCHANT_ACCEPT_RADIUS_KM")
        private String ruleCode;
        @Schema(description = "规则名称", example = "服务商抢单距离上限")
        private String ruleName;
        @Schema(description = "规则分组", example = "MATCH")
        private String ruleGroup;
        @Schema(description = "规则值", example = "15")
        private String ruleValue;
        @Schema(description = OpenApiSchemaConstants.RISK_RULE_VALUE_TYPE, example = "DECIMAL")
        private String valueType;
        @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = "备注", example = "超出服务半径不给予推送")
        private String remark;
    }

    @Data
    public static class SummaryRespVO {
        @Schema(description = "关联抢单记录数")
        private Integer acceptRecordCount;
        @Schema(description = "接单成功次数")
        private Integer acceptedCount;
        @Schema(description = "未成功接单次数")
        private Integer rejectedCount;
        @Schema(description = "当前推送是否已过截止时间")
        private Boolean deadlineExpired;
        @Schema(description = "最近服务距离（公里）")
        private BigDecimal closestDistanceKm;
    }

    @Data
    public static class AcceptRecordRespVO {
        @Schema(description = "抢单记录 ID", example = "1")
        private Long id;
        @Schema(description = "订单 ID", example = "1001")
        private Long orderId;
        @Schema(description = "单元 ID", example = "2001")
        private Long unitId;
        @Schema(description = "服务商 ID", example = "3001")
        private Long merchantId;
        @Schema(description = "服务商名称", example = "安心家政")
        private String merchantName;
        @Schema(description = "联系人", example = "李师傅")
        private String merchantContactName;
        @Schema(description = "联系手机", example = "13800138001")
        private String merchantContactMobile;
        @Schema(description = "接单时间")
        private LocalDateTime acceptTime;
        @Schema(description = "距离公里")
        private BigDecimal distanceKm;
        @Schema(description = "接单结果：SUCCESS 接单成功、FAILED 接单失败", example = "SUCCESS")
        private String acceptResult;
        @Schema(description = "备注", example = "服务商接单成功")
        private String remark;
    }
}
