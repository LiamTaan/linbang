package cn.iocoder.yudao.module.linbang.controller.admin.riskevent.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 风险事件详情 Response VO")
@Data
public class RiskEventDetailRespVO {

    private Long id;
    private String bizType;
    private Long bizId;
    private String riskType;
    private String riskLevel;
    private String hitRuleCode;
    private String status;
    private Long handleBy;
    private LocalDateTime handleTime;
    private String remark;
    private LocalDateTime createTime;
    private RiskRuleSimpleRespVO hitRule;
    private OrderSimpleRespVO order;
    private OrderUnitSimpleRespVO unit;
    private OrderAbnormalSimpleRespVO abnormal;
    private ComplaintSimpleRespVO complaint;
    private AppealSimpleRespVO appeal;
    private WithdrawSimpleRespVO withdraw;
    private List<OrderOperateLogRespVO> orderOperateLogs;

    @Data
    public static class RiskRuleSimpleRespVO {
        private Long id;
        private String ruleCode;
        private String ruleName;
        private String ruleGroup;
        private String ruleValue;
        private String valueType;
        private String status;
        private String remark;
    }

    @Data
    public static class OrderSimpleRespVO {
        private Long id;
        private String orderNo;
        private String title;
        private String status;
        private Long userId;
        private String userNo;
        private String userNickname;
        private String userMobile;
        private Long merchantId;
        private String merchantName;
        private String merchantContactName;
        private String merchantContactMobile;
    }

    @Data
    public static class OrderUnitSimpleRespVO {
        private Long id;
        private Long orderId;
        private String unitNo;
        private Integer unitSeq;
        private String unitTitle;
        private String status;
        private Boolean isLocked;
        private String lockReason;
        private Long merchantId;
        private String merchantName;
        private String merchantContactName;
        private String merchantContactMobile;
    }

    @Data
    public static class OrderAbnormalSimpleRespVO {
        private Long id;
        private Long orderId;
        private Long unitId;
        private String abnormalNo;
        private String abnormalType;
        private String riskLevel;
        private String handleStatus;
        private Long handleBy;
        private LocalDateTime handleTime;
        private String remark;
        private LocalDateTime createTime;
    }

    @Data
    public static class ComplaintSimpleRespVO {
        private Long id;
        private String complaintNo;
        private Long orderId;
        private Long unitId;
        private Long complainantUserId;
        private Long respondentUserId;
        private String complaintType;
        private String status;
        private LocalDateTime handleTime;
        private String resultDesc;
        private LocalDateTime createTime;
    }

    @Data
    public static class AppealSimpleRespVO {
        private Long id;
        private String appealNo;
        private Long orderId;
        private Long unitId;
        private Long userId;
        private String appealType;
        private String status;
        private String auditStatus;
        private Long auditBy;
        private LocalDateTime auditTime;
        private String auditRemark;
        private String rejectReason;
        private LocalDateTime createTime;
    }

    @Data
    public static class WithdrawSimpleRespVO {
        private Long id;
        private String withdrawNo;
        private Long userId;
        private Long walletAccountId;
        private Long bankCardId;
        private BigDecimal applyAmount;
        private BigDecimal feeAmount;
        private BigDecimal realAmount;
        private String status;
        private String auditStatus;
        private Long auditBy;
        private LocalDateTime auditTime;
        private String auditRemark;
        private String rejectReason;
        private LocalDateTime payTime;
        private LocalDateTime createTime;
    }

    @Data
    public static class OrderOperateLogRespVO {
        private Long id;
        private Long orderId;
        private Long unitId;
        private String operateType;
        private String operateRole;
        private Long operateBy;
        private String beforeStatus;
        private String afterStatus;
        private String remark;
        private LocalDateTime operateTime;
    }

}
