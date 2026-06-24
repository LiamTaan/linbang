package cn.iocoder.yudao.module.linbang.controller.admin.walletaccount.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 钱包账户详情 Response VO")
@Data
public class WalletAccountDetailRespVO {

    private Long id;
    private Long userId;
    private String roleCode;
    private BigDecimal totalAmount;
    private BigDecimal availableAmount;
    private BigDecimal frozenAmount;
    private BigDecimal escrowAmount;
    private BigDecimal commissionAmount;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private MemberUserSimpleRespVO user;
    private WalletBankCardSimpleRespVO defaultBankCard;
    private List<WalletFlowSimpleRespVO> recentFlows;
    private List<WalletWithdrawSimpleRespVO> recentWithdraws;
    private WithdrawStatRespVO withdrawStats;

    @Data
    public static class MemberUserSimpleRespVO {
        private Long id;
        private String userNo;
        private String mobile;
        private String nickname;
        private String currentRoleCode;
        private String status;
    }

    @Data
    public static class WalletBankCardSimpleRespVO {
        private Long id;
        private Long userId;
        private String bankName;
        private String bankCode;
        private String cardNoMask;
        private String accountName;
        private String reservedMobile;
        private String status;
        private Boolean isDefault;
        private LocalDateTime createTime;
    }

    @Data
    public static class WalletFlowSimpleRespVO {
        private Long id;
        private String flowNo;
        private Long userId;
        private Long walletAccountId;
        private String bizType;
        private String flowType;
        private BigDecimal changeAmount;
        private BigDecimal beforeAmount;
        private BigDecimal afterAmount;
        private Long relatedOrderId;
        private Long relatedUnitId;
        private Long relatedPayOrderId;
        private Long relatedRefundId;
        private String remark;
        private LocalDateTime createTime;
    }

    @Data
    public static class WalletWithdrawSimpleRespVO {
        private Long id;
        private String withdrawNo;
        private Long bankCardId;
        private BigDecimal applyAmount;
        private BigDecimal feeAmount;
        private BigDecimal realAmount;
        private String status;
        private String auditStatus;
        private String auditRemark;
        private String rejectReason;
        private LocalDateTime payTime;
        private LocalDateTime createTime;
    }

    @Data
    public static class WithdrawStatRespVO {
        private Integer totalCount;
        private BigDecimal totalApplyAmount;
        private Integer pendingCount;
        private BigDecimal pendingAmount;
        private Integer successCount;
        private BigDecimal successAmount;
    }

}
