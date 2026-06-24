package cn.iocoder.yudao.module.linbang.controller.admin.orderabnormal.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 异常订单详情 Response VO")
@Data
public class OrderAbnormalDetailRespVO {

    private Long id;
    private Long orderId;
    private Long unitId;
    private String abnormalNo;
    private String abnormalType;
    private String riskLevel;
    private String hitRuleCode;
    private String handleStatus;
    private Long handleBy;
    private LocalDateTime handleTime;
    private String remark;
    private LocalDateTime createTime;
    private OrderSimpleRespVO order;
    private OrderUnitSimpleRespVO unit;
    private RiskRuleSimpleRespVO hitRule;
    private List<OrderOperateLogRespVO> operateLogs;

    @Data
    public static class OrderSimpleRespVO {
        private Long id;
        private String orderNo;
        private String title;
        private String status;
        private Long userId;
        private Long merchantId;
    }

    @Data
    public static class OrderUnitSimpleRespVO {
        private Long id;
        private String unitNo;
        private Integer unitSeq;
        private String unitTitle;
        private String status;
        private Boolean isLocked;
        private String lockReason;
        private Long merchantId;
    }

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
    public static class OrderOperateLogRespVO {
        private Long id;
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
