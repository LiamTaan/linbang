package cn.iocoder.yudao.module.linbang.controller.admin.walletbankcard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 用户银行卡详情 Response VO")
@Data
public class WalletBankCardDetailRespVO {

    private Long id;
    private Long userId;
    private String userNo;
    private String userNickname;
    private String userMobile;
    private String bankName;
    private String bankCode;
    private String cardNoEncrypt;
    private String cardNoMask;
    private String accountName;
    private String reservedMobile;
    private String status;
    private Boolean isDefault;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<WalletAccountSimpleRespVO> walletAccounts;
    private List<WalletWithdrawSimpleRespVO> recentWithdraws;
    private WithdrawStatRespVO withdrawStats;

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
    public static class WalletWithdrawSimpleRespVO {
        private Long id;
        private String withdrawNo;
        private Long walletAccountId;
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
