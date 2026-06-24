package cn.iocoder.yudao.module.linbang.controller.app.promote.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 App - 推广中心 Response VO")
@Data
public class AppPromoteCenterRespVO {

    @Schema(description = "推广员 ID", example = "1")
    private Long promoterId;

    @Schema(description = "等级编码", example = "L1")
    private String levelCode;

    @Schema(description = "邀请码", example = "INVITE888")
    private String inviteCode;

    @Schema(description = "邀请链接")
    private String inviteUrl;

    @Schema(description = "绑定用户数", example = "12")
    private Integer bindUserCount;

    @Schema(description = "转化用户数", example = "5")
    private Integer convertCount;

    @Schema(description = "累计佣金", example = "88.80")
    private BigDecimal totalCommissionAmount;

    @Schema(description = "可提现佣金", example = "28.80")
    private BigDecimal availableCommissionAmount;

    @Schema(description = "推广员状态：ENABLE 启用、DISABLE 停用", example = "ENABLE")
    private String status;

    @Schema(description = "待结算佣金单数", example = "2")
    private Integer pendingCommissionCount;

    @Schema(description = "已结算佣金单数", example = "4")
    private Integer settledCommissionCount;

    @Schema(description = "失效佣金单数", example = "1")
    private Integer invalidCommissionCount;

    @Schema(description = "待结算佣金金额", example = "12.50")
    private BigDecimal pendingCommissionAmount;

    @Schema(description = "已结算佣金金额", example = "76.30")
    private BigDecimal settledCommissionAmount;

    @Schema(description = "最近佣金记录")
    private List<RecentCommissionRespVO> recentCommissionOrders;

    @Data
    public static class RecentCommissionRespVO {

        @Schema(description = "主键", example = "1")
        private Long id;

        @Schema(description = "佣金单号", example = "LBCM20260001")
        private String commissionNo;

        @Schema(description = "来源用户 ID", example = "1001")
        private Long userId;

        @Schema(description = "来源订单 ID", example = "2001")
        private Long sourceOrderId;

        @Schema(description = "来源单元 ID", example = "3001")
        private Long sourceUnitId;

        @Schema(description = "佣金类型", example = "ORDER")
        private String commissionType;

        @Schema(description = "佣金金额", example = "8.80")
        private BigDecimal commissionAmount;

        @Schema(description = "佣金单状态：PENDING 待结算、SETTLED 已结算、INVALID 已失效", example = "PENDING")
        private String status;

        @Schema(description = "结算时间")
        private LocalDateTime settleTime;

        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }
}
