package cn.iocoder.yudao.module.linbang.controller.admin.ordermatchrecord.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 订单匹配记录新增/修改 Request VO")
@Data
public class OrderMatchRecordSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "27554")
    private Long id;

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "6003")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @Schema(description = "单元ID", example = "22484")
    private Long unitId;

    @Schema(description = "服务商ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "16471")
    @NotNull(message = "服务商ID不能为空")
    private Long merchantId;

    @Schema(description = "命中规则编码")
    private String matchRuleCode;

    @Schema(description = "匹配分值")
    private BigDecimal matchScore;

    @Schema(description = "距离公里")
    private BigDecimal distanceKm;

    @Schema(description = "推送时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "推送时间不能为空")
    private LocalDateTime pushTime;

    @Schema(description = "接单截止时间")
    private LocalDateTime acceptDeadlineTime;

    @Schema(description = OpenApiSchemaConstants.MATCH_RECORD_STATUS, requiredMode = Schema.RequiredMode.REQUIRED,
            example = "PUSHED")
    @NotEmpty(message = "状态不能为空")
    private String status;

}
