package cn.iocoder.yudao.module.linbang.controller.app.promote.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "用户 App - 推广佣金分页项 Response VO")
@Data
public class AppCommissionPageItemRespVO {

    @Schema(description = "佣金单 ID", example = "1")
    private Long id;

    @Schema(description = "佣金单号", example = "LBCM20260001")
    private String commissionNo;

    @Schema(description = "佣金类型，按推广业务字典展示", example = "ORDER")
    private String commissionType;

    @Schema(description = "佣金金额")
    private BigDecimal commissionAmount;

    @Schema(description = "佣金单状态：PENDING 待结算、SETTLED 已结算、INVALID 已失效", example = "PENDING")
    private String status;

    @Schema(description = "结算时间")
    private LocalDateTime settleTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
