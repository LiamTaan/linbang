package cn.iocoder.yudao.module.linbang.controller.admin.walletwithdraw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 提现申请详情 Response VO")
@Data
public class WalletWithdrawDetailRespVO {

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
    private String auditRemark;
    private Long auditBy;
    private LocalDateTime auditTime;
    private String rejectReason;
    private LocalDateTime payTime;
    private LocalDateTime createTime;
    private MemberUserSimpleRespVO user;
    private WalletAccountSimpleRespVO walletAccount;
    private WalletBankCardSimpleRespVO bankCard;
    private List<WalletFlowSimpleRespVO> relatedFlows;

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
    public static class WalletAccountSimpleRespVO {
        private Long id;
        private Long userId;
        private String roleCode;
        private BigDecimal totalAmount;
        private BigDecimal availableAmount;
        private BigDecimal frozenAmount;
        private BigDecimal escrowAmount;
        private BigDecimal commissionAmount;
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
        private Long relatedPayOrderId;
        private Long relatedRefundId;
        private String remark;
        private LocalDateTime createTime;
    }

}
