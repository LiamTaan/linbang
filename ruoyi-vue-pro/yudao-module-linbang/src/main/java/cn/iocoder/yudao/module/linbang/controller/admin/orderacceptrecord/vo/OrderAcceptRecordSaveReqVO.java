package cn.iocoder.yudao.module.linbang.controller.admin.orderacceptrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 抢单记录新增/修改 Request VO")
@Data
public class OrderAcceptRecordSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "24409")
    private Long id;

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "13466")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @Schema(description = "单元ID", example = "9636")
    private Long unitId;

    @Schema(description = "服务商ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2654")
    @NotNull(message = "服务商ID不能为空")
    private Long merchantId;

    @Schema(description = "接单时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "接单时间不能为空")
    private LocalDateTime acceptTime;

    @Schema(description = "距离公里")
    private BigDecimal distanceKm;

    @Schema(description = "接单结果", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "接单结果不能为空")
    private String acceptResult;

    @Schema(description = "备注", example = "你猜")
    private String remark;

}