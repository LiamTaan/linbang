package cn.iocoder.yudao.module.linbang.controller.admin.match.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 推送批次 Response VO")
@Data
public class MatchPushBatchRespVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "订单 ID")
    private Long orderId;

    @Schema(description = "单元 ID")
    private Long unitId;

    @Schema(description = "阶段号")
    private Integer stageNo;

    @Schema(description = "批次号")
    private Integer pushBatchNo;

    @Schema(description = "起始半径公里")
    private BigDecimal radiusStartKm;

    @Schema(description = "结束半径公里")
    private BigDecimal radiusEndKm;

    @Schema(description = "计划推送时间")
    private LocalDateTime plannedAt;

    @Schema(description = "过期时间")
    private LocalDateTime expiredAt;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "触发类型")
    private String triggerType;
}
