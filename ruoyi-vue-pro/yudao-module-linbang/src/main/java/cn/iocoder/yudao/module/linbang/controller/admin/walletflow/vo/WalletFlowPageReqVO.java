package cn.iocoder.yudao.module.linbang.controller.admin.walletflow.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 钱包流水分页 Request VO")
@Data
public class WalletFlowPageReqVO extends PageParam {

    @Schema(description = "流水号")
    private String flowNo;

    @Schema(description = "用户关键词，支持用户编号/昵称/手机号", example = "13800138000")
    private String userKeyword;

    @Schema(description = "钱包账户ID", example = "16689")
    private Long walletAccountId;

    @Schema(description = "业务类型", example = "1")
    private String bizType;

    @Schema(description = "流水类型", example = "1")
    private String flowType;

    @Schema(description = "变动金额")
    private BigDecimal changeAmount;

    @Schema(description = "变动前金额")
    private BigDecimal beforeAmount;

    @Schema(description = "变动后金额")
    private BigDecimal afterAmount;

    @Schema(description = "关联订单号")
    private String relatedOrderNo;

    @Schema(description = "关联单元ID", example = "28489")
    private Long relatedUnitId;

    @Schema(description = "关联支付订单ID", example = "16116")
    private Long relatedPayOrderId;

    @Schema(description = "关联退款ID", example = "12674")
    private Long relatedRefundId;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
