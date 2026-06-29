package cn.iocoder.yudao.module.linbang.controller.admin.walletaccount.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 钱包账户详情 Response VO")
@Data
public class WalletAccountDetailRespVO {

    @Schema(description = "钱包账户 ID", example = "1")
    private Long id;
    @Schema(description = "用户 ID", example = "1001")
    private Long userId;
    @Schema(description = "钱包角色编码", example = "MERCHANT")
    private String roleCode;
    @Schema(description = "累计总金额")
    private BigDecimal totalAmount;
    @Schema(description = "可提现金额")
    private BigDecimal availableAmount;
    @Schema(description = "冻结金额")
    private BigDecimal frozenAmount;
    @Schema(description = "托管金额")
    private BigDecimal escrowAmount;
    @Schema(description = "佣金金额")
    private BigDecimal commissionAmount;
    @Schema(description = OpenApiSchemaConstants.WALLET_STATUS, example = "ENABLE")
    private String status;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "用户摘要")
    private MemberUserSimpleRespVO user;
    @Schema(description = "默认银行卡")
    private WalletBankCardSimpleRespVO defaultBankCard;
    @Schema(description = "最近钱包流水")
    private List<WalletFlowSimpleRespVO> recentFlows;
    @Schema(description = "最近业务账单")
    private List<WalletBillSimpleRespVO> recentBills;
    @Schema(description = "最近提现记录")
    private List<WalletWithdrawSimpleRespVO> recentWithdraws;
    @Schema(description = "提现统计")
    private WithdrawStatRespVO withdrawStats;

    @Data
    public static class MemberUserSimpleRespVO {
        @Schema(description = "用户 ID", example = "1001")
        private Long id;
        @Schema(description = "用户编号", example = "LBU123456")
        private String userNo;
        @Schema(description = "手机号", example = "13800138000")
        private String mobile;
        @Schema(description = "昵称", example = "邻里用户")
        private String nickname;
        @Schema(description = "当前角色编码", example = "MERCHANT")
        private String currentRoleCode;
        @Schema(description = OpenApiSchemaConstants.USER_STATUS, example = "ENABLE")
        private String status;
    }

    @Data
    public static class WalletBankCardSimpleRespVO {
        @Schema(description = "银行卡 ID", example = "3001")
        private Long id;
        @Schema(description = "用户 ID", example = "1001")
        private Long userId;
        @Schema(description = "银行名称", example = "中国建设银行")
        private String bankName;
        @Schema(description = "银行编码", example = "CCB")
        private String bankCode;
        @Schema(description = "脱敏卡号", example = "6222****8888")
        private String cardNoMask;
        @Schema(description = "开户名", example = "张三")
        private String accountName;
        @Schema(description = "预留手机号", example = "13800138000")
        private String reservedMobile;
        @Schema(description = OpenApiSchemaConstants.BANK_CARD_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = "是否默认卡")
        private Boolean isDefault;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
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
        @Schema(description = "关联订单 ID", example = "3001")
        private Long relatedOrderId;
        @Schema(description = "关联单元 ID", example = "4001")
        private Long relatedUnitId;
        @Schema(description = "关联支付订单 ID", example = "5001")
        private Long relatedPayOrderId;
        @Schema(description = "关联退款 ID", example = "6001")
        private Long relatedRefundId;
        @Schema(description = "备注", example = "提现申请冻结余额")
        private String remark;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Data
    public static class WalletWithdrawSimpleRespVO {
        @Schema(description = "提现申请 ID", example = "1")
        private Long id;
        @Schema(description = "提现单号", example = "WW202606260001")
        private String withdrawNo;
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
        @Schema(description = "审核备注", example = "待财务复核")
        private String auditRemark;
        @Schema(description = "驳回原因", example = "银行卡信息异常")
        private String rejectReason;
        @Schema(description = "打款时间")
        private LocalDateTime payTime;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Data
    public static class WalletBillSimpleRespVO {
        @Schema(description = "账单主键 ID", example = "1")
        private Long id;
        @Schema(description = OpenApiSchemaConstants.WALLET_BILL_TYPE, example = "WITHDRAW")
        private String billType;
        @Schema(description = "账单标题", example = "提现到账")
        private String billTitle;
        @Schema(description = "业务状态", example = "SUCCESS")
        private String bizStatus;
        @Schema(description = "账单金额", example = "88.80")
        private BigDecimal amount;
        @Schema(description = "账单方向 IN/OUT", example = "OUT")
        private String amountDirection;
        @Schema(description = "关联订单 ID", example = "10001")
        private Long relatedOrderId;
        @Schema(description = "关联单元 ID", example = "20001")
        private Long relatedUnitId;
        @Schema(description = "备注")
        private String remark;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Data
    public static class WithdrawStatRespVO {
        @Schema(description = "提现总次数")
        private Integer totalCount;
        @Schema(description = "累计申请金额")
        private BigDecimal totalApplyAmount;
        @Schema(description = "待处理次数")
        private Integer pendingCount;
        @Schema(description = "待处理金额")
        private BigDecimal pendingAmount;
        @Schema(description = "成功次数")
        private Integer successCount;
        @Schema(description = "成功金额")
        private BigDecimal successAmount;
    }

}
