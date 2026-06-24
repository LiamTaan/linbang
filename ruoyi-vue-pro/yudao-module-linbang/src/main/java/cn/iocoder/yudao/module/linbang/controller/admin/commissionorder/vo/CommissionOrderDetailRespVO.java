package cn.iocoder.yudao.module.linbang.controller.admin.commissionorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 佣金详情 Response VO")
@Data
public class CommissionOrderDetailRespVO {

    private Long id;
    private String commissionNo;
    private Long promoterId;
    private Long userId;
    private Long sourceOrderId;
    private Long sourceUnitId;
    private String commissionType;
    private BigDecimal commissionAmount;
    private String status;
    private LocalDateTime settleTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private PromoterRespVO promoter;
    private UserRespVO user;
    private OrderRespVO sourceOrder;
    private UnitRespVO sourceUnit;

    @Data
    public static class PromoterRespVO {
        private Long id;
        private Long userId;
        private String userNo;
        private String userNickname;
        private String userMobile;
        private String levelCode;
        private String inviteCode;
        private String status;
        private BigDecimal totalCommissionAmount;
        private BigDecimal availableCommissionAmount;
    }

    @Data
    public static class UserRespVO {
        private Long id;
        private String userNo;
        private String mobile;
        private String nickname;
        private String currentRoleCode;
        private String status;
    }

    @Data
    public static class OrderRespVO {
        private Long id;
        private String orderNo;
        private Long userId;
        private Long merchantId;
        private Long categoryId;
        private String title;
        private BigDecimal orderAmount;
        private String pricingMode;
        private String splitStatus;
        private String status;
        private LocalDateTime createTime;
    }

    @Data
    public static class UnitRespVO {
        private Long id;
        private Long orderId;
        private String unitNo;
        private Integer unitSeq;
        private String unitTitle;
        private BigDecimal unitAmount;
        private String splitMode;
        private Boolean isLocked;
        private String lockReason;
        private Long merchantId;
        private String status;
        private LocalDateTime finishTime;
        private LocalDateTime createTime;
    }
}
