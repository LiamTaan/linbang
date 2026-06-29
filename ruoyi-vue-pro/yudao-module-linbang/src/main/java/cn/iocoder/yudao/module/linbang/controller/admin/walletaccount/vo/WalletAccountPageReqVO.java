package cn.iocoder.yudao.module.linbang.controller.admin.walletaccount.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 钱包账户分页 Request VO")
@Data
public class WalletAccountPageReqVO extends PageParam {

    @Schema(description = "用户ID", example = "6967")
    private Long userId;

    @Schema(description = "用户关键词（用户编号 / 昵称 / 手机号）", example = "13800138000")
    private String userKeyword;

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "总资产")
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
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
