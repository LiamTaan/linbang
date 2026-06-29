package cn.iocoder.yudao.module.linbang.controller.admin.walletwithdraw.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 提现申请详情 Response VO")
@Data
public class WalletWithdrawDetailRespVO {

    @Schema(description = "提现申请 ID", example = "1")
    private Long id;
    @Schema(description = "提现单号")
    private String withdrawNo;
    @Schema(description = "申请用户 ID", example = "1001")
    private Long userId;
    @Schema(description = "钱包账户 ID", example = "2001")
    private Long walletAccountId;
    @Schema(description = "银行卡 ID", example = "3001")
    private Long bankCardId;
    @Schema(description = "申请金额")
    private BigDecimal applyAmount;
    @Schema(description = "手续费")
    private BigDecimal feeAmount;
    @Schema(description = "实际到账金额")
    private BigDecimal realAmount;
    @Schema(description = OpenApiSchemaConstants.WITHDRAW_STATUS, example = "PENDING")
    private String status;
    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "PENDING")
    private String auditStatus;
    @Schema(description = "审核备注")
    private String auditRemark;
    @Schema(description = "审核人", example = "1")
    private Long auditBy;
    @Schema(description = "审核时间")
    private LocalDateTime auditTime;
    @Schema(description = "驳回原因")
    private String rejectReason;
    @Schema(description = "打款时间")
    private LocalDateTime payTime;
    @Schema(description = "支付转账单 ID", example = "7001")
    private Long payTransferId;
    @Schema(description = "支付转账单号")
    private String payTransferNo;
    @Schema(description = "出款失败原因")
    private String transferErrorMsg;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "申请用户摘要")
    private MemberUserSimpleRespVO user;
    @Schema(description = "钱包账户摘要")
    private WalletAccountSimpleRespVO walletAccount;
    @Schema(description = "银行卡摘要")
    private WalletBankCardSimpleRespVO bankCard;
    @Schema(description = "关联钱包流水")
    private List<WalletFlowSimpleRespVO> relatedFlows;

    @Schema(description = "用户摘要")
    @Data
    public static class MemberUserSimpleRespVO {
        @Schema(description = "用户 ID", example = "1001")
        private Long id;
        @Schema(description = "用户编号")
        private String userNo;
        @Schema(description = "手机号")
        private String mobile;
        @Schema(description = "昵称")
        private String nickname;
        @Schema(description = "当前角色编码", example = "MERCHANT")
        private String currentRoleCode;
        @Schema(description = OpenApiSchemaConstants.USER_STATUS, example = "ENABLE")
        private String status;
    }

    @Schema(description = "钱包账户摘要")
    @Data
    public static class WalletAccountSimpleRespVO {
        @Schema(description = "钱包账户 ID", example = "2001")
        private Long id;
        @Schema(description = "用户 ID", example = "1001")
        private Long userId;
        @Schema(description = "钱包角色编码", example = "MERCHANT")
        private String roleCode;
        @Schema(description = "累计总金额")
        private BigDecimal totalAmount;
        @Schema(description = "可用余额")
        private BigDecimal availableAmount;
        @Schema(description = "冻结金额")
        private BigDecimal frozenAmount;
        @Schema(description = "托管金额")
        private BigDecimal escrowAmount;
        @Schema(description = "佣金金额")
        private BigDecimal commissionAmount;
        @Schema(description = "钱包状态", example = "ENABLE")
        private String status;
    }

    @Schema(description = "银行卡摘要")
    @Data
    public static class WalletBankCardSimpleRespVO {
        @Schema(description = "银行卡 ID", example = "3001")
        private Long id;
        @Schema(description = "用户 ID", example = "1001")
        private Long userId;
        @Schema(description = "银行名称")
        private String bankName;
        @Schema(description = "银行编码")
        private String bankCode;
        @Schema(description = "脱敏卡号")
        private String cardNoMask;
        @Schema(description = "开户名")
        private String accountName;
        @Schema(description = "预留手机号")
        private String reservedMobile;
        @Schema(description = "银行卡状态", example = "ENABLE")
        private String status;
        @Schema(description = "是否默认卡")
        private Boolean isDefault;
    }

    @Data
    public static class WalletFlowSimpleRespVO {
        @Schema(description = "流水 ID", example = "1")
        private Long id;
        @Schema(description = "流水号", example = "WF202606260001")
        private String flowNo;
        @Schema(description = "用户 ID", example = "1001")
        private Long userId;
        @Schema(description = "钱包账户 ID", example = "2001")
        private Long walletAccountId;
        @Schema(description = OpenApiSchemaConstants.WALLET_FLOW_BIZ_TYPE, example = "WITHDRAW_APPLY")
        private String bizType;
        @Schema(description = OpenApiSchemaConstants.WALLET_FLOW_TYPE, example = "OUT")
        private String flowType;
        @Schema(description = "变动金额")
        private BigDecimal changeAmount;
        @Schema(description = "变动前金额")
        private BigDecimal beforeAmount;
        @Schema(description = "变动后金额")
        private BigDecimal afterAmount;
        @Schema(description = "关联支付订单 ID", example = "5001")
        private Long relatedPayOrderId;
        @Schema(description = "关联退款 ID", example = "6001")
        private Long relatedRefundId;
        @Schema(description = "备注")
        private String remark;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

}
