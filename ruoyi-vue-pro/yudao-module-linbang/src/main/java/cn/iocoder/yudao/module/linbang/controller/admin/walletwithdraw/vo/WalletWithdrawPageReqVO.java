package cn.iocoder.yudao.module.linbang.controller.admin.walletwithdraw.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 提现申请分页 Request VO")
@Data
public class WalletWithdrawPageReqVO extends PageParam {

    @Schema(description = "提现单号")
    private String withdrawNo;

    @Schema(description = "用户ID", example = "24266")
    private Long userId;

    @Schema(description = "用户关键词（用户编号 / 昵称 / 手机号）", example = "13800138000")
    private String userKeyword;

    @Schema(description = "钱包账户ID", example = "19469")
    private Long walletAccountId;

    @Schema(description = "银行卡ID", example = "8562")
    private Long bankCardId;

    @Schema(description = "申请金额")
    private BigDecimal applyAmount;

    @Schema(description = "手续费")
    private BigDecimal feeAmount;

    @Schema(description = "实际到账金额")
    private BigDecimal realAmount;

    @Schema(description = "状态", example = "2")
    private String status;

    @Schema(description = "审核状态", example = "1")
    private String auditStatus;

    @Schema(description = "审核备注", example = "你猜")
    private String auditRemark;

    @Schema(description = "审核人")
    private Long auditBy;

    @Schema(description = "审核时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] auditTime;

    @Schema(description = "驳回原因", example = "不对")
    private String rejectReason;

    @Schema(description = "打款时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] payTime;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
